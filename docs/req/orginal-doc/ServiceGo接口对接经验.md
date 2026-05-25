# ServiceGo 工单平台接口对接经验

整理自入职工单、合同工单接口联调过程中的要点，作为后续对接其他对象的参考。

---

## 1. 鉴权与签名

### 1.1 必备参数
每次请求 URL 上都要附带：

| 参数 | 来源 | 备注 |
| --- | --- | --- |
| `email` | 超管邮箱（如 `ServiceGo.demo@udesk.cn`） | 需要 URL 编码 |
| `timestamp` | `Instant.now().getEpochSecond()`，**秒级** | 默认 5 分钟内有效 |
| `sign` | 见下 | 16 进制小写 |

### 1.2 签名算法
官方文档写的是 `sign=SHA1(email&api_token&timestamp)`，**但实际联调走的是 SHA-256**（见 `ContractWorkOrderTest.java`、`ContractWorkOrderQueryTest.java`）。

> ⚠️ 文档与实现不一致，新接接入时务必先用 SHA-256 跑一次验证；如果 401 再回退到 SHA-1。

```java
String signInput = EMAIL + "&" + API_TOKEN + "&" + timestamp;
String sign = sha256(signInput); // 16进制小写
```

### 1.3 调用地址模板
```
https://<host>/api/v1/<resource>?<业务参数>&email=<email>&timestamp=<ts>&sign=<sign>
```

---

## 2. 核心接口速查

| 操作 | URL | 方法 | 说明 |
| --- | --- | --- | --- |
| 创建记录 | `/api/v1/data` | POST | 通用，所有对象同一个接口 |
| 更新记录 | `/api/v1/data` | PUT | 用 `id` 或 `uniqueFieldApiName + uniqueFieldValue` 定位 |
| 查询单条 | `/api/v1/data` | GET | 同上两种定位方式 |
| 列表查询 | `/api/v1/datas` | GET | 需配合 `filterId`、`pageNum`、`pageSize` |
| 上传附件 | `/api/v1/fileField/attachments` | POST | `multipart/form-data` |
| 查附件列表 | `/v1/fileField/attachments` | GET | 注意路径前缀比其它接口少 `/api` |
| 删附件 | `/v1/fileField/attachments/remove` | PUT | 同上 |

> 一个接口管所有对象 —— 切换对象只换 `objectApiName`，是 ServiceGo 的核心设计特点。

---

## 3. 对象与字段 API 名（已确认）

| 对象中文名 | `objectApiName` |
| --- | --- |
| 入职工单 | `entryWorkOrder` |
| 合同工单 | `contractWorkOrder` |
| 入职工单-证件信息 | `CertInfo` |
| 入职工单-语言能力 | `Language` |
| 入职工单-正规教育 | `Education` |
| 入职工单-过往工作经历 | `WorkExperience` |
| 入职工单-家庭成员及社会关系 | `FamilyRelation` |
| 入职工单-资格证信息 | `Certificate` |
| 入职工单-工作许可及签证信息 | `VisaPermit` |

入职工单常用字段速查（完整字段见 `主数据清单_md/入职工单.md`）：

| 中文 | `fieldApiName` | 类型 |
| --- | --- | --- |
| 中文全名 | `chineseName` | 单行文本 |
| 个人电话 | `personalPhone` | 电话 |
| 个人邮箱 | `personalEmail` | 邮箱 |
| 员工号 | `staffNo` | 单行文本 |
| 入司日期 | `entryDate` | 日期 |
| 招聘渠道 | `recruitChannel` | 单选列表 |
| 用工形式 | `employType` | 单选列表 |
| 组织/部门 | `deptOrg` | 单选（来源:组织数据） |
| 岗位 | `post` | 单选（来源:岗位数据） |

---

## 4. 字段类型与值格式

| `fieldTypeApiName` | 写入示例 | 备注 |
| --- | --- | --- |
| `field_type_single_line` | `"张三"` | 长度有限制 |
| `field_type_multi_line` | `"多行文本"` | |
| `field_type_rich_text` | `"<p>富文本</p>"` | 响应里 `richText.attachmentList` 含附件 |
| `field_type_date` | `"2026-01-01"` | `yyyy-MM-dd` |
| `field_type_date_time` | `"2026-01-01 12:00:00"` | 精确到秒 |
| `field_type_numeric` | `"1"` | 字符串形式 |
| `field_type_telephone` | `"13300000001,13300000002"` | 多值逗号分隔，会校验合法性 |
| `field_type_email` | `"a@x.cn,b@x.cn"` | 同上 |
| `field_type_single_listbox` | `"内部推荐"` | **传选项中文名**而不是 code |
| `field_type_multi_listbox` | `"选项1,选项2"` | 多选用逗号 |
| `field_type_lookup` | `"admin@test.cn"` | 配合 `foreignExternalFieldApiName` 用；不传时 `fieldValue` 必须是 `dataId` |
| `field_type_staff` | `"admin@test.cn"` | 员工邮箱 |
| `field_type_percent` | `"0.3"` | 30% 写 0.3 |
| `field_type_cascade` | `"父级,子级"` | 中文逗号要换成英文 |
| `field_type_attachment` | — | 走文件上传接口，不在 `fieldDataList` 里直接传 |

---

## 5. 查询定位方式

`/api/v1/data` 的 GET 与 PUT 支持两种定位：

### 5.1 按记录 ID
```
?objectApiName=entryWorkOrder&id=61528
```

### 5.2 按唯一字段
```
?objectApiName=entryWorkOrder&uniqueFieldApiName=personalPhone&uniqueFieldValue=15651818750
```

⚠️ **唯一字段定位的前提**：该字段在 ServiceGo 后台被配置为「外部唯一字段」。如果只是普通字段（如 `personalPhone` 默认情况），接口会报错。这种场景下要走列表查询接口 `/api/v1/datas` + 过滤器。

---

## 6. 列表查询的坑

`/api/v1/datas` 必须传 `filterId` —— 这是 ServiceGo 后台预先配置好的过滤器 ID，不是动态条件。意味着：

- 想按某字段动态查询，**得在管理后台先建一个过滤器**，拿到 `filterId` 再调用。
- 或者拉取较大范围的数据后，在客户端二次过滤（性能差，仅适合小数据量）。

参考 `ContractWorkOrderTest.java` 中 `FILTER_ID = 241618` 的用法。

---

## 7. 响应结构

### 7.1 单条查询响应
```json
{
  "code": 200,
  "visible": false,
  "data": {
    "id": 61528,
    "fieldDataList": [
      {
        "fieldApiName": "chineseName",
        "fieldTypeApiName": "field_type_single_line",
        "fieldValue": "张三"
      }
    ]
  }
}
```

### 7.2 字段值要分类型解析
- 普通文本/数字/日期 → `fieldValue`
- 选择类 → 也读 `fieldValue`，必要时取 `optionNameList`
- 电话/邮箱 → 读 `tagValueList[].tagValue`
- 富文本 → 读 `richText.content`、`richText.attachmentList`
- 文件字段 → 读 `attachment.attachmentList`，下载走外链 `docAddress`
- 查找型 → 读 `foreignDataName`（关联记录展示名）
- 员工查找型 → 读 `userEmail`

### 7.3 分页响应
```
paging: { pageNum, pageSize, total }
```

---

## 8. 文件字段操作

文件字段不能直接在 `data` 接口里写值，必须走独立的三个接口：

1. **先创建/拿到记录**，得到 `dataId`
2. POST `/api/v1/fileField/attachments` （`multipart/form-data`）上传
3. GET `/v1/fileField/attachments` 查询附件列表，拿到 `docId`、签名下载链接 `downloadAddress`
4. PUT `/v1/fileField/attachments/remove` 删除（`isClear=1` 清空，`isClear=0` 按 `docIds` 删）

支持格式：pdf、doc(x)、xls(x)、ppt(x)、png、jpg、jpeg、gif。

> 下载链接是临时签名 URL（默认 5 分钟有效），不要落库长期保存，需要时重新查。

---

## 9. 推送接口（反向回调）

ServiceGo 会主动回调目标系统：
- 方法 POST，URL 上带 `dataId` 和 `sign`
- `sign = SHA1(value1&value2&...&secretKey)`，按 URL 参数顺序拼接，**计算时要剔除 sign 自身**
- Body 是 JSON，键就是 `fieldApiName`

实现接收端时记得：把请求里的所有 query 参数按到达顺序拼起来（去掉 sign），用配置好的 `secretKey` 算 SHA-1 比对。

---

## 10. 通用排查清单

接口报错时按下面顺序查：

1. **401** → 签名不对：检查 `email` 是否 URL 编码、`timestamp` 是否秒级、SHA 算法是否正确（先试 SHA-256）
2. **400** → 参数问题：
   - 单选/多选字段值是不是「选项中文名」而不是 code
   - 日期格式是否 `yyyy-MM-dd`、时间是否到秒
   - 多值字段（电话/邮箱/多选）是否用英文逗号
3. **唯一字段查询失败** → 字段未配置为外部唯一字段，改走列表查询
4. **找不到对象** → `objectApiName` 大小写敏感（如 `CertInfo` 首字母大写、`entryWorkOrder` 首字母小写）
5. **附件传了没生效** → 检查是不是把附件值塞进了 `fieldDataList`，应该走文件接口

---

## 11. 已落地的测试入口

代码位置：`backend/src/test/java/com/aicoding/proxy/`

| 文件 | 作用 |
| --- | --- |
| `ContractWorkOrderTest.java` | 合同工单 列表查询（`/datas` + `filterId`） |
| `ContractWorkOrderQueryTest.java` | 合同工单 按 ID 查询单条 |
| `EntryWorkOrderQueryByPhoneTest.java` | 入职工单 按 `personalPhone` 唯一字段查询 |

每个 test 都是 `main` 方法直跑，没引入 JUnit；调试新对象时复制最近的一个文件改 `OBJECT_API_NAME` 和定位参数即可。

---

## 12. 待验证 / 待补充

- [ ] SHA-1 vs SHA-256 究竟哪种是当前环境正解，确认后回填本文
- [ ] 子表对象（CertInfo / Education 等）创建时如何关联主单 —— 可能用 `field_type_lookup`
- [ ] `filterId` 的批量获取方式（管理后台路径未确认）
- [ ] `personalPhone` 是否能配置为唯一字段，还是必须走列表过滤
