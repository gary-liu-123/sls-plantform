# 项目启动约定

## 启动

```bash
cd F:/githubprojects/sls-plantform
bash ./start_all.sh
```

- 自动 `taskkill` 清理占用 8283 / 5173 端口的残留进程
- 自动设置 `JAVA_HOME` 为 JDK 21
- 健康检查通过后退出，日志落到 `logs/backend.log` / `logs/frontend.log`

## 端口

| 服务 | 端口 |
| --- | --- |
| 后端（Spring Boot） | 8283 |
| 前端（Vite） | 5173 |
| 局域网手机访问 | `http://<本机 IP>:5173` |

## 停止

```bash
pkill -f 'spring-boot\|vite\|PhotoApplication'
# 若端口仍被占：
netstat -ano | grep ":8283 \|:5173 " | awk '{print $5}' | sort -u | xargs -I{} taskkill //PID {} //F
```

---

# 本项目接口（后端 REST API）

| 方法 | 路径 | 参数 | 功能 |
| --- | --- | --- | --- |
| POST | `/api/upload` | `file`（MultipartFile）、`phone` | 按手机号查入职工单 dataId，上传文件到 ServiceGo `validIdCard` 附件字段；返回 `previewUrl` |
| GET | `/api/attachment` | `dataId`（工单 ID）、`field`（字段 API 名） | 代理下载 ServiceGo 附件，加签名后流式返回图片字节流（解决跨域和签名问题） |

---

# ServiceGo 对接参考

## 参考文档

| 文档 | 用途 |
| --- | --- |
| `docs/req/orginal-doc/ServiceGo工单平台接口规范-HRSSC.md` | URL 模板、签名拼接（实际用 SHA-256，非 SHA-1）、Body 结构、字段值格式 |
| [Udesk 记录接口](https://www.udesk.cn/doc/sercive/data/) | `POST /v1/datas/search` 高级搜索：operator 列表、`filterId` 必填、`judgeStrategy`（1=AND/2=OR） |
| [Udesk 自定义字段接口](https://www.udesk.cn/doc/sercive/custom-fields/) | `GET /v1/fields` 字段元数据、字段类型对照表 |
| `docs/req/orginal-doc/主数据清单_md/清单.md` | 业务对象 → `objectApiName` 对照 |
| `docs/req/orginal-doc/主数据清单_md/入职工单.md` | 入职工单全字段：apiName、类型、选项值、必填规则 |
| `docs/req/orginal-doc/ServiceGo接口对接经验.md` | 踩坑总结：鉴权、filterId 预配、附件三件套、排查清单 |

## 字段定义接口（GET /api/v1/fields）

用于获取工单所有字段的元数据，**查询展示、保存、更新都依赖这个接口的返回值**。

```
GET /api/v1/fields?objectApiName=entryWorkOrder&email=...&timestamp=...&sign=...
```

Java 调用：`serviceGoClient.queryFields("entryWorkOrder")`

**Response 结构：**

```json
{
  "code": 200,
  "paging": { "total": 178 },
  "data": [
    {
      "id": 758564,
      "label": "招聘渠道",
      "apiName": "recruitChannel",
      "fieldTypeName": "选择列表",
      "permissionCode": 4,
      "optionList": [
        { "id": 1, "name": "劳务外包" },
        { "id": 2, "name": "招聘网站" }
      ]
    }
  ]
}
```

**字段说明：**

| 字段 | 含义 | 使用方式 |
| --- | --- | --- |
| `apiName` | 字段 API 名 | `fieldDataList` 里的 key；查询时从工单数据取值的 key |
| `label` | 字段中文名 | 页面展示 |
| `fieldTypeName` | 字段类型 | 决定前端控件（见下表） |
| `permissionCode` | 2=只读，4=可编辑 | 控制字段是否可编辑 |
| `optionList[].name` | 选项文本 | **下拉框选项必须从这里取，不能硬编码**；保存时也传这个值 |
| `optionList[].id` | 选项 ID | **下拉框选项按 id 升序排列显示** |

**fieldTypeName → 前端控件：**

| fieldTypeName | 控件 | 备注 |
| --- | --- | --- |
| `选择列表` | 下拉框 | 取 `optionList[].name` 展示和传值 |
| `单行文本` / `自定义` | 文本输入框 | |
| `数字` | 数字输入框 | |
| `日期` | 日期选择器 | 值格式 `yyyy-MM-dd` |
| `日期时间` | 日期时间选择器 | |
| `电话` / `邮箱` | 文本输入框（格式校验） | |
| `文件字段` | 文件上传组件 | **不走 `fieldDataList`，走 `/api/v1/fileField/attachments`** |
| `员工查找` | 员工搜索组件 | |

**前端缓存建议：** 进入工单页面时调用一次，按 `apiName` 建 Map 缓存，整个会话复用。

```javascript
const fieldMap = new Map(fieldsData.map(f => [f.apiName, f]));
const options = fieldMap.get('recruitChannel')?.optionList ?? [];
```

## 测试类速查

| 测试类 | 对象 | 接口 | 定位方式 |
| --- | --- | --- | --- |
| `ContractWorkOrderTest` | `contractWorkOrder` | GET `/api/v1/datas` | 列表 + `filterId=241618` + 分页 |
| `ContractWorkOrderQueryTest` | `contractWorkOrder` | GET `/api/v1/data` | `id=162870171` |
| `ContractWorkOrderQueryByPhoneTest` | `contractWorkOrder` | GET `/api/v1/data` | `uniqueFieldApiName=phone` |
| `EntryWorkOrderQueryByPhoneTest` | `entryWorkOrder` | GET `/api/v1/data` | `uniqueFieldApiName=personalPhone` |
| `EntryWorkOrderUpdateTest` | `entryWorkOrder` | PUT `/api/v1/data` | `id=162352692` |
| `EntryWorkOrderSearchByPhoneAndStatusTest` | `entryWorkOrder` | POST `/api/v1/datas/search` | `personalPhone is_any` + `orderStatus not "入职终止"`，`filterId` 待后台配 |
| `ContractWorkOrderSearchByPhoneAndStatusTest` | `contractWorkOrder` | POST `/api/v1/datas/search` | `personalPhone is_any` + `contractStatus not "合同办理完成"`，`filterId=241618` |
| `EntryWorkOrderFieldsTest` | `entryWorkOrder` | GET `/api/v1/fields` | `objectApiName=entryWorkOrder` |
| `SmsServiceTest` | — | POST `/api/sms/messages` | 先取 token，需 `@SpringBootTest(classes = PhotoApplication.class)` |

## 关键字段速查

| 业务概念 | apiName | 类型 | 注意事项 |
| --- | --- | --- | --- |
| 个人电话 | `personalPhone` | 电话 | search 用 `is_any` operator |
| 中文全名 | `chineseName` | 单行文本 | |
| 外文全名 | `foreignName` | 单行文本 | |
| 招聘渠道 | `recruitChannel` | 单选 | 传选项中文名，search 用 `is/not` |
| 入司日期 | `entryDate` | 日期 | 格式 `yyyy-MM-dd` |
| 身份证附件 | `validIdCard` | 文件字段 | 不走 `fieldDataList`，走附件接口 |
| 工单状态 | `orderStatus` | 单选 | search 用 `not "入职终止"` |
| 记录主键 | `KEY` | 数字 | 所有按 id 操作的参数 |
