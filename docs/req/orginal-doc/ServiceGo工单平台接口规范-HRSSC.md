# ServiceGo工单平台 接口文档

## 文档修订记录

| 版本 | 状态 | 简要说明 | 日期 | 变更人 | 批准日期 | 批准人 |
|------|------|----------|------|--------|----------|--------|
| 1.0 | A | 初始版本 | 2026.05-07 | - | - | - |

> 状态说明：A——增加，M——修改，D——删除

---

## 1. 引言

### 1.1 文档说明

ServiceGo 系统最大的特点是可以根据业务需求自定义对象和字段，对应的 ServiceGo 记录 API 最大的特点是 **一个接口支持所有的系统对象和自定义对象**。

### 1.2 读者范围

项目组需求、设计、开发全体人员。

---

## 2. 通用说明

### 2.1 HTTP 状态码

| 状态码 | 说明 |
|--------|------|
| 200 OK | HTTP请求成功的标准响应。实际的响应将取决于请求的方法。在GET请求，响应将包含对应于请求的资源实体。在POST请求，响应将包含一个实体的说明或包含的行动的结果。 |
| 201 Created | 该请求已完成，并创建一个新的资源。 |
| 204 No Content | 服务器成功处理了请求，但未返回任何内容。 |
| 400 Bad Request | 由于客户端错误（例如，错误的请求语法，无效的请求消息帧，或欺骗性请求路由），服务器不能或不会处理请求。 |
| 401 Unauthorized | 未认证。 |
| 404 Not Found | 找不到请求的资源，但将来可能再次可用。客户的后续请求是允许的。 |
| 500 Internal Server Error | 服务器内部错误。 |

### 2.2 分页

ServiceGo服务需要分页返回的对象会符合以下报文格式：

| 属性名 | 类型 | 说明 |
|--------|------|------|
| paging | 对象 | 分页对象 |
| paging.pageNum | 整型 | 页码 |
| paging.pageSize | 整型 | 页码大小 |
| paging.total | 整型 | 总数 |

### 2.3 调用地址

```
https://<host>/api/v1/[接口相对地址]?[URL参数]&email=[管理员邮箱]&timestamp=[时间戳]&sign=[签名]
```

其中括号包含的部分含义如下：

| 变量 | 说明 |
|------|------|
| 接口相对地址 | API的相对URL，每个API中都会单独标明 |
| URL参数 | 请求地址中参数，多个时使用&分隔 |
| 管理员邮箱 | 您的超级管理员邮箱 |
| 时间戳 | 发起请求时的时间戳，'1970-01-01 00:00:00'至今的秒数。默认过期时间五分钟 |
| 签名 | 身份认证签名，每次API请求均需要附加此参数 |

**签名计算方法：**

```
sign=SHA1(email&api_token&timestamp)
```

其中：
- email: 管理员邮箱地址
- api_token: 鉴权私钥
- timestamp: 时间戳，'1970-01-01 00:00:00'至今的秒数

系统部署后，提供email值和 api_token 值。

### 2.4 字段类型、格式组装说明

| 字段类型 | 类型名称 | 字段值格式 | 说明 |
|----------|----------|------------|------|
| field_type_single_line | 单行文本 | "测试_single_line" | 文本类字段, 长度不能超过限制 |
| field_type_rich_text | 富文本 | "测试_rich_text" | 文本类字段, 长度不能超过限制 |
| field_type_multi_line | 多行文本 | "测试_multi_line" | 文本类字段, 长度不能超过限制 |
| field_type_date | 日期 | "2020-01-01" | "yyyy-MM-dd" |
| field_type_date_time | 时间 | "2020-01-01 12:12:12" | "yyyy-MM-dd HH:mm:ss", 精确到秒 |
| field_type_numeric | 数字 | "1" | - |
| field_type_telephone | 电话 | "13300000001,13300000002" | 多个号码使用逗号分隔。号码必须合法 |
| field_type_email | 电子邮件 | "admin1@test.cn,admin2@test.cn" | 多个邮箱地址使用逗号分隔。邮箱地址必须合法 |
| field_type_single_listbox | 单选列表 | "选项名称1" | - |
| field_type_multi_listbox | 多选列表 | "选项名称1,选项名称2" | 多个选项使用逗号分隔 |
| field_type_lookup | 查找型 | "admin1@test.cn" | 查找对象唯一字段的值, 需要配合foreignExternalFieldApiName一起使用。不传foreignExternalFieldApiName时，fieldDataList[].fieldValue要传dataId |
| field_type_staff | 查找员工 | "admin3@test.cn" | 员工邮箱地址 |
| field_type_percent | 百分比 | "0.3" | 原始值, 如30%, 传0.3即可 |
| field_type_cascade | 级联 | "选项1,选项11" | "父级选项名称,子级选项名称"，英文逗号隔开 |
| field_type_attachment | 文件 | - | - |

### 2.5 目标对象API名称

详见主数据清单，后续同步维护。

### 2.6 对象的字段API名称

详见主数据各个目标对象页，后续同步维护。

---

## 3. 接口方案

### 3.1 创建记录

| 项目 | 说明 |
|------|------|
| 协议 | https |
| URL | `<host>/api/v1/data` |
| 方法 | POST |
| 参数类型 | Content-Type:application/json |

#### 3.1.1 请求参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| objectApiName | 字符串 | 是 | 目标对象api名称 |
| fieldDataList | 数组 | 是 | 字段API名称与字段值List |
| fieldDataList[].fieldApiName | 字符串 | 是 | 字段API名称 |
| fieldDataList[].fieldValue | 字符串 | 否 | 字段值 |
| fieldDataList[].foreignExternalFieldApiName | 字符串 | 否 | 指定查找字段关联对象的外部唯一字段API名称。字段类型为查找时，非必填。当此字段不传时，fieldDataList[].fieldValue要传dataId |

#### 3.1.2 响应数据

| 属性名 | 类型 | 说明 |
|--------|------|------|
| code | 整型 | 响应编码 |
| message | 字符串 | 响应消息 |
| visible | 布尔型 | 是否可见，true / false |
| data | 对象 | 业务对象 |
| data.id | 整型 | 新创建记录ID |

#### 3.1.3 请求示例

```bash
$curl 'https://servicego.udesk.cn/api/v1/data?email=admin@udesk.cn&timestamp=1496631984&sign=ef506d04ef74e1031f91025494244e88f3559b78' -i -X POST -H 'Content-Type: application/json' -H 'Accept: application/json' -d '{
  "objectApiName" : "entryWorkOrder",
  "fieldDataList" : [ {
    "fieldApiName" : "chineseName",
    "fieldValue" : "张三"
  }, {
    "fieldApiName" : "recruitChannel",
    "fieldValue" : "劳务外包"
  } ]
}'
```

#### 3.1.4 响应示例

```json
{
  "code" : 200,
  "visible" : false,
  "data" : {
    "id" : 61528
  }
}
```

---

### 3.2 更新记录

当我们更新一条记录时，除了要传对象ApiName和字段值，还需要传参数定位某对象的某一条记录，有两种定位方式：

1. 记录ID
2. 使用该对象的唯一字段+字段值

| 项目 | 说明 |
|------|------|
| 协议 | https |
| URL | `<host>/api/v1/data` |
| 方法 | PUT |
| 参数类型 | Content-Type:application/json |

#### 3.2.1 请求参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| objectApiName | 字符串 | 是 | 目标对象api名称 |
| id | 整型 | 否 | 记录ID。如果ID为空，则uniqueFieldApiName与uniqueFieldValue必填 |
| uniqueFieldApiName | 字符串 | 否 | 指定外部唯一字段API名称。如果uniqueFieldApiName与uniqueFieldValue为空，则ID必填 |
| uniqueFieldValue | 字符串 | 否 | uniqueFieldApiName对应的外部唯一字段值。如果uniqueFieldApiName与uniqueFieldValue为空，则ID必填 |
| fieldDataList | 数组 | 是 | 字段API名称与字段值List |
| fieldDataList[].fieldApiName | 字符串 | 是 | 字段API名称 |
| fieldDataList[].fieldValue | 字符串 | 否 | 字段值 |
| fieldDataList[].foreignExternalFieldApiName | 字符串 | 否 | 指定查找字段关联对象的外部唯一字段API名称。字段类型为查找时，非必填。当此字段不传时，fieldDataList[].fieldValue要传dataId |

#### 3.2.2 响应数据

| 属性名 | 类型 | 说明 |
|--------|------|------|
| code | 整型 | 响应编码 |
| message | 字符串 | 响应消息 |
| visible | 布尔型 | 是否可见，true / false |

#### 3.2.3 请求示例

```bash
$curl 'https://servicego.udesk.cn/api/v1/data?email=admin@udesk.cn&timestamp=1496631984&sign=ef506d04ef74e1031f91025494244e88f3559b78' -i -X PUT -H 'Content-Type: application/json' -H 'Accept: application/json' -d '{
  "objectApiName" : "entryWorkOrder",
  "id" : 61528,
  "fieldDataList" : [ {
    "fieldApiName" : "chineseName",
    "fieldValue" : "张山"
  }, {
    "fieldApiName" : "recruitChannel",
    "fieldValue" : "内部推荐"
  } ]
}'
```

#### 3.2.4 响应示例

```json
{
  "code" : 200,
  "message" : "OK",
  "visible" : false
}
```

---

### 3.3 查询记录

当我们查询一条记录时，就无需传入字段值了，而是传入可以定位到某对象下某条记录的参数，即可返回该记录的所有字段和字段值。依然是提供了两种定位方式：

1. 记录ID
2. 使用该对象的唯一字段+字段值

查询记录为GET方式，在URL中传中参数。

| 项目 | 说明 |
|------|------|
| 协议 | https |
| URL | `<host>/api/v1/data` |
| 方法 | GET |
| 参数类型 | Content-Type:application/json |

#### 3.3.1 请求参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| objectApiName | 字符串 | 是 | 对象API名称。使用唯一字段查询时，必填 |
| id | 整型 | 否 | 记录ID。如果ID为空，则uniqueFieldApiName与uniqueFieldValue必填 |
| uniqueFieldApiName | 字符串 | 否 | 外部唯一字段API名称。如果uniqueFieldApiName与uniqueFieldValue为空，则ID必填 |
| uniqueFieldValue | 字符串 | 否 | uniqueFieldApiName对应的外部唯一字段值。如果uniqueFieldApiName与uniqueFieldValue为空，则ID必填 |

#### 3.3.2 响应数据

| 属性名 | 类型 | 说明 |
|--------|------|------|
| code | 整型 | 响应编码 |
| message | 字符串 | 响应消息 |
| visible | 布尔型 | 是否可见，true / false |
| data | 对象 | 业务对象 |
| data.id | 整型 | 记录ID |
| data.fieldDataList | 数组 | 字段信息List |
| data.fieldDataList[].fieldApiName | 字符串 | 字段API名称 |
| data.fieldDataList[].fieldTypeApiName | 字符串 | 字段类型API名称 |
| data.fieldDataList[].fieldValue | 字符串 | 字段值 |
| data.fieldDataList[].foreignDataName | 字符串 | 关联对象记录名称 |
| data.fieldDataList[].userEmail | 字符串 | 关联员工邮箱 |
| data.fieldDataList[].optionNameList | 数组 | 选项名称列表(选择类型字段) |
| data.fieldDataList[].tagValueList | 数组 | 标签字段详细信息(电话或邮箱) |
| data.fieldDataList[].tagValueList[].tagName | 字符串 | 标签名称 |
| data.fieldDataList[].tagValueList[].tagValue | 字符串 | 标签值 |
| data.fieldDataList[].richText | 对象 | 富文本字段详细信息(内容及附件) |
| data.fieldDataList[].richText.content | 字符串 | 富文本内容 |
| data.fieldDataList[].richText.attachmentList | 数组 | 附件列表 |
| data.fieldDataList[].richText.attachmentList[].name | 字符串 | 原始文件名 |
| data.fieldDataList[].richText.attachmentList[].docAddress | 字符串 | 外链 |
| data.fieldDataList[].richText.attachmentList[].size | 整型 | 大小 |
| data.fieldDataList[].ownerResult | 对象 | 所有人 |
| data.fieldDataList[].ownerResult.ownerType | 整型 | 所有人类型 |
| data.fieldDataList[].ownerResult.ownerName | 字符串 | 所有人名称 |
| data.fieldDataList[].attachment | 对象 | 文件字段详细信息(内容及附件) |
| data.fieldDataList[].attachment.attachmentList | 数组 | 附件列表 |
| data.fieldDataList[].attachment.attachmentList[].name | 字符串 | 原始文件名 |
| data.fieldDataList[].attachment.attachmentList[].docAddress | 字符串 | 外链 |
| data.fieldDataList[].attachment.attachmentList[].size | 整型 | 大小 |

#### 3.3.3 请求示例

```bash
$curl 'https://servicego.udesk.cn/api/v1/data?email=admin@udesk.cn&timestamp=1496631984&sign=ef506d04ef74e1031f91025494244e88f3559b78&objectApiName=entryWorkOrder&id=61528' -i -H 'Content-Type: application/json' -H 'Accept: application/json'
```

#### 3.3.4 响应示例

```json
{
  "code" : 200,
  "visible" : false,
  "data" : {
    "id" : 61528,
    "fieldDataList" : [ {
      "fieldApiName" : "chineseName",
      "fieldTypeApiName" : "field_type_single_line",
      "fieldValue" : "张山"
    }, {
      "fieldApiName" : "recruitChannel",
      "fieldTypeApiName" : "field_type_single_listbox",
      "fieldValue" : "内部推荐"
    }, {
      "fieldApiName" : "coordinate",
      "fieldTypeApiName" : "field_type_geography",
      "fieldValue" : ""
    } ]
  }
}
```

---

### 3.4 文件图片字段上传

此接口用于上传文件字段附件,支持一次上传多个文件,请求体采用 multipart/form-data 类型。

目前支持：pdf、doc(docx)、xls(xlsx)、ppt(pptx)、png、jpg、jpeg 和 gif 格式文件。

| 项目 | 说明 |
|------|------|
| 协议 | https |
| URL | `<host>/api/v1/fileField/attachments` |
| 方法 | POST |
| 参数类型 | Content-Type: multipart/form-data |

#### 3.4.1 请求参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| dataId | 整型 | 是 | 记录ID |
| objectApiName | 字符串 | 是 | 记录对象api名称 |
| fieldApiName | 字符串 | 是 | 字段ApiName名称 |

#### 3.4.2 响应数据

| 属性名 | 类型 | 说明 |
|--------|------|------|
| code | 整型 | 响应编码 |
| message | 字符串 | 响应消息 |
| visible | 布尔型 | 是否可见，true / false |

#### 3.4.3 请求示例

```bash
$curl 'https://servicego.udesk.cn/api/v1/fileField/attachments?email=admin@udesk.cn&timestamp=1496631984&sign=ef506d04ef74e1031f91025494244e88f3559b78&dataId=86834651&objectApiName=entryWorkOrder&fieldApiName=positionApplyForm' -i -H 'Content-Type: multipart/form-data' -H 'Accept: application/json' -F 'file=职位申请表.pdf'
```

#### 3.4.4 响应示例

```json
{
  "code" : 200,
  "message" : "OK",
  "visible" : false
}
```

---

### 3.5 查询文件图片字段列表

| 项目 | 说明 |
|------|------|
| 协议 | https |
| URL | `<host>/v1/fileField/attachments` |
| 方法 | GET |
| 参数类型 | Content-Type: application/json |

#### 3.5.1 请求参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| dataId | 整型 | 是 | 记录ID |
| objectApiName | 字符串 | 是 | 记录对象api名称 |
| fieldApiName | 字符串 | 是 | 字段ApiName名称 |

#### 3.5.2 响应数据

| 属性名 | 类型 | 说明 |
|--------|------|------|
| code | 整型 | 响应编码 |
| message | 字符串 | 响应消息 |
| visible | 布尔型 | 是否可见，true / false |
| data | 数组 | 业务对象 |
| data[].docId | 字符串 | 附件ID标识 |
| data[].name | 字符串 | 附件文件名 |
| data[].size | 字符串 | 附件大小 |
| data[].downloadAddress | 字符串 | 下载地址 |

#### 3.5.3 请求示例

```bash
$curl 'https://servicego.udesk.cn/api/v1/fileField/attachments?email=admin@udesk.cn&timestamp=1496631984&sign=ef506d04ef74e1031f91025494244e88f3559b78&dataId=86834651&objectApiName=entryWorkOrder&fieldApiName=positionApplyForm' -i -H 'Content-Type: application/json' -H 'Accept: application/json'
```

#### 3.5.4 响应示例

```json
{
  "code" : 200,
  "message" : "OK",
  "visible" : false,
  "data" : [ {
    "docId" : "4f6adc8e1866f7dba9cd722c03b78d72",
    "name" : "职位申请表.pdf",
    "size" : 11,
    "downloadAddress" : "https://servicego.udesk.cn/bucket-sg/file/2/0/20260507/tAtNHyXawQ/职位申请表.pdf?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=OXJWU84WK3ZHZWQHGEXK%2F20260508%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20260508T021355Z&X-Amz-Expires=300&X-Amz-SignedHeaders=host&X-Amz-Signature=3bcb51a77cc6f7bd33b3b07e727e8430e14e885ce5aa5a55be8f8ef855226abe"
  } ]
}
```

---

### 3.6 文件图片字段删除附件

| 项目 | 说明 |
|------|------|
| 协议 | https |
| URL | `<host>/v1/fileField/attachments/remove` |
| 方法 | PUT |
| 参数类型 | Content-Type: application/json |

#### 3.6.1 请求参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| dataId | 整型 | 是 | 记录ID |
| objectApiName | 字符串 | 是 | 记录对象api名称 |
| fieldApiName | 字符串 | 是 | 字段ApiName名称 |
| isClear | 整型 | 是 | 是否清空，0 否 1 是 |
| docIds | 字符串数组 | 否 | 要删除的 docId 数组，如果 isClear 为 0 时数组里面必须有元素 |

#### 3.6.2 响应数据

| 属性名 | 类型 | 说明 |
|--------|------|------|
| code | 整型 | 响应编码 |
| message | 字符串 | 响应消息 |
| visible | 布尔型 | 是否可见，true / false |

#### 3.6.3 请求示例

```bash
$curl 'https://servicego.udesk.cn/api/v1/fileField/attachments/remove?email=admin@udesk.cn&timestamp=1496631984&sign=ef506d04ef74e1031f91025494244e88f3559b78&dataId=86834651&objectApiName=entryWorkOrder&fieldApiName=positionApplyForm&isClear=1' -i -X PUT -H 'Content-Type: application/json' -H 'Accept: application/json'
```

#### 3.6.4 响应示例

```json
{
  "code" : 200,
  "message" : "OK",
  "visible" : false
}
```

---

### 3.7 推送功能

目标系统实现该接口，由工单系统调用该接口往目标系统推送记录信息。

| 项目 | 说明 |
|------|------|
| 协议 | https |
| URL | 目标系统实现接口 |
| 方法 | POST |
| 参数类型 | Content-Type: application/json |

#### 3.7.1 支持鉴权

```
sign=SHA1(value1&value2&value3&value4&value5&secretKey)
```

其中：
- value: URL参数值，计算签名时需严格按照顺序使用&拼接
- secretKey: 应用鉴权私钥，目标系统接口配置时生成

实际请求接口时，sign签名会附带在URL参数中，所以接口提供方在接到请求之后计算签名时，将sign参数过滤。

#### 3.7.2 最终请求URL事例

```
http://<host>?dataId=86834635&sign=c82fd819aeab9c328ba0dd6b807d75b08c05b8d2
```

Body内容例子：

```json
{
  "chineseName": "张三",
  "recruitChannel": "内部推荐"
}
```

其中：url上的dataId的值是工单系统记录主键。