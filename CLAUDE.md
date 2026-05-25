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
