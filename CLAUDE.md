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
| `com.aicoding.proxy.ContractWorkOrderTest` | main | — | — | 合同工单列表查询（`/api/v1/datas` + `filterId=241618`，分页） |
| `com.aicoding.proxy.ContractWorkOrderQueryTest` | main | — | — | 按 ID 查询单条合同工单（`id=162870171`） |
| `com.aicoding.proxy.ContractWorkOrderQueryByPhoneTest` | main | — | — | 按手机号（`phone`）唯一字段查询合同工单 |
| `com.aicoding.proxy.EntryWorkOrderQueryByPhoneTest` | main | — | — | 按手机号（`personalPhone`）唯一字段查询入职工单 |
| `com.aicoding.proxy.EntryWorkOrderUpdateTest` | main | — | — | 按 ID 更新入职工单全量字段（PUT `/api/v1/data`） |
