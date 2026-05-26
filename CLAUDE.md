# 项目启动约定

## 启动方式

**所有服务（前端 + 后端）必须通过项目根目录的 `start_all.sh` 一键启动**，不要手动单独跑 `mvn spring-boot:run` 或 `npm run dev`。

```bash
cd F:/githubprojects/sls-plantform
bash ./start_all.sh
```

理由：
- 脚本会先清理残留进程并强杀占用 8283 / 5173 端口的 java.exe / node.exe（Git Bash 下 `pkill` 杀不到 mvn 的 java 子进程，必须用 `taskkill` 兜底）
- 自动设置 `JAVA_HOME` 为 JDK 21（pom 要求 21，系统默认是 15）
- 启动后做健康检查，确认两个端口都通了再退出
- 日志统一落到 `logs/backend.log` 和 `logs/frontend.log`

## 端口

- 后端：8283（Spring Boot）
- 前端：5173（Vite）
- 局域网手机访问：`http://<本机 IP>:5173`

## 停止

```bash
pkill -f 'spring-boot\|vite\|PhotoApplication'
```
若上面杀不干净（端口仍被占），用：
```bash
netstat -ano | grep ":8283 \|:5173 " | awk '{print $5}' | sort -u | xargs -I{} taskkill //PID {} //F
```

## 接口清单

| 类 | 方法 | 路径 | 参数 | 功能 |
| --- | --- | --- | --- | --- |
| `com.example.photo.PhotoController` | POST | `/api/upload` | `file`（MultipartFile）、`phone`（手机号） | 按手机号查入职工单 dataId，再将文件上传到 ServiceGo 的 `validIdCard` 附件字段；返回 `previewUrl` 供前端展示服务器图片 |
| `com.example.photo.PhotoController` | GET | `/api/attachment` | `dataId`（工单 ID）、`field`（字段 API 名） | 代理下载 ServiceGo 附件：查工单详情取最新附件 `docAddress`，加签名后流式返回图片字节流（解决跨域和签名问题） |
| `com.example.photo.ContractWorkOrderTest` | @Test | — | — | 合同工单列表查询（`/api/v1/datas` + `filterId=241618`，分页） |
| `com.example.photo.ContractWorkOrderQueryTest` | @Test | — | — | 按 ID 查询单条合同工单（`id=162870171`） |
| `com.example.photo.ContractWorkOrderQueryByPhoneTest` | @Test | — | — | 按手机号（`phone`）唯一字段查询合同工单 |
| `com.example.photo.EntryWorkOrderQueryByPhoneTest` | @Test | — | — | 按手机号（`personalPhone`）唯一字段查询入职工单 |
| `com.example.photo.EntryWorkOrderUpdateTest` | @Test | — | — | 按 ID 更新入职工单全量字段（PUT `/api/v1/data`） |
| `com.example.photo.EntryWorkOrderSearchByPhoneAndStatusTest` | @Test | — | — | 高级搜索入职工单（POST `/api/v1/datas/search`）：`personalPhone is_any 15651818750` AND `orderStatus not "入职终止"`；`filterId` 待后台配 |
| `com.example.photo.ContractWorkOrderSearchByPhoneAndStatusTest` | @Test | — | — | 高级搜索合同工单（POST `/api/v1/datas/search`）：`personalPhone is_any 15651818750` AND `contractStatus not "合同办理完成"`，`filterId=241618` |
| `com.example.sms.SmsServiceTest` | @Test | — | `phone`、`templateId` | 调用 SERES 短信平台发送短信（先取 token 再 POST `/api/sms/messages`），需用 `@SpringBootTest(classes = PhotoApplication.class)` 启动 |

## ServiceGo 测试类参考资料

写 `backend/src/test/java/com/example/photo/` 下这批 ServiceGo 调测类时参考的文档、关键内容与字段映射。

### 参考文件清单

| 文件 | 类型 | 关键内容 | 在测试类里的用途 |
| --- | --- | --- | --- |
| `docs/req/orginal-doc/ServiceGo工单平台接口规范-HRSSC.md` | 接口规范 | 鉴权（`sign=SHA1(email&api_token&timestamp)`，实际用 SHA-256）、统一调用地址 `https://<host>/api/v1/...`、3.1 创建/3.2 更新/3.3 查询/3.4-3.6 附件/3.7 推送、字段类型与值格式表 | 提供 URL 模板、签名拼接、Body/参数结构、字段值写法 |
| `https://www.udesk.cn/doc/sercive/data/`（在线 / Udesk 开发者中心 - 记录接口） | 官方在线文档 | 比 HRSSC.md 更全的接口集合，特别是 HRSSC.md 未覆盖的：**`POST /v1/datas/search`**（多条件搜索，operator 列表：`is/not/is_any/not_any/contains_any/...`、字段类型 ↔ 操作符对应表、`filterId` 必填、`judgeStrategy` 1=AND/2=OR/3=自定义）、`POST /v1/datas/scrollSearch`（大数据量滚动搜索）、批量创建/更新等 | 写 `*SearchBy*Test` 类时的协议来源：决定 body 结构（`objectApiName`+`filterId`+`conditionList`）、电话字段必须用 `is_any/not_any`、单选字段用 `is/not` |
| `docs/req/orginal-doc/主数据清单_md/清单.md` | 对象总表 | 各业务对象与 `objectApiName` 的对应关系（如 `entryWorkOrder`、`contractWorkOrder`、`CertInfo`、`Education` 等子表） | 决定 `OBJECT_API_NAME` 常量 |
| `docs/req/orginal-doc/主数据清单_md/入职工单.md` | 字段字典 | 入职工单全字段：`fieldApiName`、SF API 名称、字段类型（单选/日期/电话/文件等）、选项值、必填规则 | 决定测试 Body 里 `fieldDataList` 中每个字段的 ApiName、类型、可选值；唯一字段查询用的 `personalPhone` 也来自此 |
| `docs/req/orginal-doc/ServiceGo接口对接经验.md` | 自维护沉淀 | 鉴权坑（SHA-256）、定位方式（id vs 唯一字段）、`filterId` 必须后台预配、附件三件套、排查清单 | 后续接新对象时的速查 |

### 文件之间的关系

```
ServiceGo工单平台接口规范-HRSSC.md   ← 协议层（基础）：URL/方法/签名/字段值格式
            │
            ├── 决定每个 test 类的：URL 拼装 + 签名 + Body 结构
            │
udesk.cn/doc/sercive/data/         ← 协议层（补充）：HRSSC.md 没有的高级搜索/滚动搜索
            │
            └── 决定 *Search* 测试类的：operator 选择 + conditionList 结构
            │
清单.md                            ← 对象层：业务对象 ↔ objectApiName
            │
            └── 决定每个 test 类的：OBJECT_API_NAME 常量
            │
入职工单.md / 合同工单.md（同目录）   ← 字段层：业务字段 ↔ fieldApiName + 类型 + 选项
            │
            └── 决定 fieldDataList 里每一项的 fieldApiName/fieldValue/类型；同时**字段类型决定 search 用哪个 operator**（电话→is_any，单选→is/not）

ServiceGo接口对接经验.md              ← 经验层：上面四份的踩坑总结
```

接口规范（HRSSC.md + udesk 在线文档）说"怎么调"，清单说"调哪个对象"，字段字典说"传哪些字段、值怎么写、操作符选哪个"，对接经验说"哪里容易翻车"。五份合起来才能完整还原一个 test 类。

### 测试类与参考字段的映射

| 测试类 | 对象（来自 清单.md） | 关键字段（来自 入职工单.md / 接口规范） | 接口 | 定位方式 |
| --- | --- | --- | --- | --- |
| `ContractWorkOrderTest` | `contractWorkOrder` | — | GET `/api/v1/datas` | 列表 + `filterId=241618` + 分页 |
| `ContractWorkOrderQueryTest` | `contractWorkOrder` | — | GET `/api/v1/data` | `id=162870171` |
| `ContractWorkOrderQueryByPhoneTest` | `contractWorkOrder` | `personalPhone`（唯一字段） | GET `/api/v1/data` | `uniqueFieldApiName + uniqueFieldValue` |
| `EntryWorkOrderQueryByPhoneTest` | `entryWorkOrder` | `personalPhone`（个人电话，入职工单.md 第31行） | GET `/api/v1/data` | `uniqueFieldApiName + uniqueFieldValue` |
| `EntryWorkOrderUpdateTest` | `entryWorkOrder` | `foreignName`（外文全名，入职工单.md 第33行）等 50+ 字段 | PUT `/api/v1/data` | `id=162352692` |
| `EntryWorkOrderSearchByPhoneAndStatusTest` | `entryWorkOrder` | `personalPhone`（电话→`is_any`）+ `orderStatus`（单选→`not "入职终止"`，入职工单.md 第86行） | POST `/api/v1/datas/search` | `filterId` + `conditionList`（AND） |
| `ContractWorkOrderSearchByPhoneAndStatusTest` | `contractWorkOrder` | `personalPhone`（电话→`is_any`）+ `contractStatus`（单选→`not "合同办理完成"`，合同工单.md 第27行） | POST `/api/v1/datas/search` | `filterId=241618` + `conditionList`（AND） |

### 关键字段联系（跨文件）

| 业务概念 | 入职工单.md `fieldApiName` | 合同工单同义字段 | 接口规范字段类型 | 测试类如何使用 |
| --- | --- | --- | --- | --- |
| 个人电话 | `personalPhone` | `personalPhone`（同名复用） | `field_type_telephone`，多值逗号分隔 | 两个 `*QueryByPhoneTest` 都用它做 `uniqueFieldApiName` |
| 中文全名 | `chineseName` | — | `field_type_single_line` | 创建/更新示例的 Body 字段 |
| 外文全名 | `foreignName` | — | `field_type_single_line` | `EntryWorkOrderUpdateTest` 把它改成 "Gary"/"yangjunping-Gary" |
| 招聘渠道 | `recruitChannel` | — | `field_type_single_listbox`，传选项中文名 | Update Body 中传 "劳务外包" |
| 入司日期 | `entryDate` | — | `field_type_date`，`yyyy-MM-dd` | Update Body 中传 "2026-05-13" |
| 文件附件（如身份证） | `validIdCard` | — | `field_type_attachment`，**不能放 fieldDataList**，走 `/api/v1/fileField/attachments` | `PhotoController` 上传走的就是它 |
| 记录主键 | `KEY`（中文叫"记录ID"） | 同 | 数字 | 所有 test 的 `id` 参数 |
