# 前端 chrome-devtools MCP 验证清单

每个前端模块的 ralph-loop 完成标准都必须包含「在真实浏览器里跑通 user flow」这一步。
本 checklist 列出**通用流程**；模块特定信息（入口 URL / user flow / 文案 / 业务预期异常）由每个 ralph-loop prompt 单独提供。

## 前置
- dev server 已启动：`npm run dev`，确认监听端口（默认 5173）
- 后端依赖（如本模块需要）：`curl http://localhost:8080/actuator/health` 返回 200

## 验证流程
1. `page.navigate` 打开模块入口 URL
2. 按 prompt 里列出的每条 user flow 跑：
   - `input.insert_text` 填入输入
   - `locator.click` 触发动作
   - `locator.wait_for` 期望出现的文案 / 页面跳转
3. 每条 user flow 跑完后调用：
   - `console.get_messages`：不允许 `error` 级别
   - `network.get_requests`：不允许 4xx/5xx，除非 prompt 明确列为业务预期
4. 关键页面调 `page.snapshot` 确认 DOM 结构（文本快照，不产生图片文件）

## 红线
- console error / 非业务预期的 4xx-5xx → 视为失败，必须修后重跑
- 单元测试过但浏览器流程没跑通 → 视为失败

## 收尾
- 截图产生的图片仅用于调试，测完立即删除（全局 CLAUDE.md 规则）
- 不把任何截图 commit 到仓库
