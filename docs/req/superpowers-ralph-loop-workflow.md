# 用 Superpowers + Ralph-Loop 做 Java+React 0→1 项目的工作流

## Context
- 起点：一份 Word 需求文档
- 技术栈：Java（后端）+ **Vue 3**（前端，`<script setup>` + TypeScript）
- 性质：0 → 1 全新项目
- 问题：能否用 superpowers + ralph-loop 完成？流程长什么样？

## 简短回答
先用 superpowers 把 Word 需求拆成「带可执行验证的小任务」，再用 ralph-loop 在每个小任务上自动收敛。

## Ralph-Loop 的适用边界（来自插件 README）
- ✅ 适合：成功标准清晰、有自动验证（测试/lint/curl）、可放手让 AI 跑

所以 0→1 项目里「需求理解 + 架构设计」阶段必须人和 AI 协作完成，不能甩给 ralph-loop。

## 推荐工作流（5 个阶段）

### Phase 1：需求消化（brainstorming skill）

**0. Word 文件读取（前置）**
Claude 不能直接 Read `.docx`，要先转成 markdown / 纯文本：
- 推荐：`pandoc input.docx -o requirements-raw.md`（保留标题/列表/表格结构）
- 备选：Python `python-docx` 脚本提取（适合需要程序化处理时）
- 兜底：用户在 Word 里"另存为 → Web 页（筛选过的）"或手工导出 md
转完后先把 raw md 放在 `docs/requirements/_raw.md`，**不要直接进入拆分**——先快速通读一遍，看有没有图片/表格丢失，缺了用 OCR 或手工补。

**1. Word 拆分策略（重要）**

拆分分两步：**先列功能点清单，再按功能点组织 md**。

**Step 0：通读后列出模块 + 功能点清单（先不动 Word）**
```
模块 A
  ├─ 功能 A1：描述
  ├─ 功能 A2：描述
  └─ 功能 A3：描述
模块 B
  ├─ 功能 B1：描述
  └─ 功能 B2：描述
...
```
原则：
- 一个功能 = 一个独立的 ralph-loop 单元（不要贪大）
- 如果一个功能预计超过 200 行实现代码，**继续拆**成子功能
- 跨模块依赖的功能点，在清单里标注清楚依赖关系

**Step 1：按功能点组织 md**
```
docs/requirements/
  00-overview.md          # 整体目标、角色、术语表、全局非功能需求、模块依赖总图
  01-auth.md              # 用户/登录/权限（含所有子功能）
  02-<module>-A1.md       # 模块 A 的功能 A1（如果功能多到需要独立文件）
  02-<module>-A2.md       # 模块 A 的功能 A2
  ...
  90-glossary.md          # 名词解释（可选）
  99-open-questions.md    # 我读完后列出的疑点
```

拆分原则：
- 一份 md = 能独立跑 ralph-loop 的功能单元（可以是一个模块，也可以是模块里的一个子功能）
- 单文件控制在 300-800 行，超过就再拆
- 模块/功能之间的依赖在 `00-overview.md` 顶部画清楚（A 功能依赖 B 功能的什么 API）
- **每份 md 的文件名前缀要能对应到功能点清单里的条目**（方便后续 ralph-loop prompt 引用）

**Step 2：写 ralph-loop prompt 时注释功能点**
每条 `/ralph-loop` 命令前加一行注释，说明本次覆盖哪些功能点：
```bash
# 本次目标：模块A > 功能A1（注册+登录）+ 功能A2（JWT刷新）
/ralph-loop "在 backend/ 下实现...
```

这样拆分有几个好处：ralph-loop 目标单一好收敛、中途可以灵活暂停在功能边界、出了问题是哪个功能一目了然。

**2. brainstorming**
调用 `superpowers:brainstorming`，**逐模块**走流程（不是一次过整个系统）：
- 拆解每个模块的功能点
- 明确成功标准（每条都要能写成测试或 curl / chrome-devtools MCP 脚本）
- 标出跨模块依赖、外部依赖（数据库、第三方 API）

**3. 产出**：`docs/superpowers/specs/YYYY-MM-DD-<module>-design.md`，每个模块一份。

**4. Phase 1 退出 gate（重要）**
不允许带未解决的关键问题进 Phase 2。退出条件：
- `docs/requirements/*.md` 全部拆好，每个模块独立可读
- `99-open-questions.md` 里每条都打了 `[resolved]`（user 已回答）或 `[deferred: <原因>]`（明确推迟，且不影响 Phase 2 决策）
- 仍然 `[open]` 的问题 → 不能进入 brainstorming，回去找 user 确认

### Phase 2：技术选型与架构设计
技术栈核心选型如下（写进 design 文档时按项目实际情况微调）：

**前端 (frontend/)**
- **Vue 3.5** + Vue Router 4 + **Pinia 2**（JS + `<script setup>`，需要类型保护处用 JSDoc）
- **Element Plus**（按需导入，仅用于 Form/Table/DatePicker 等重型组件）
- **Tailwind CSS 3**（主视觉层，承接 `doc/req01/_tailwind.config.js` token）
- **VueUse**（`useEventListener` / `useIntervalFn` / `useClipboard` / `useMediaDevices` 等）
- **axios** HTTP 客户端
- 构建：**Vite 5** + `@vitejs/plugin-vue`
- 运行时配置：`public/config.js` → `window.globalConfig`
- 包管理：`pnpm`（推荐）或 `npm`
- **API 类型同步**：`openapi-typescript`，从后端 `/v3/api-docs` 拉 schema → 生成 `frontend/src/api/generated/schema.ts`
  - `package.json` 加 `"gen:api": "openapi-typescript http://localhost:8080/v3/api-docs -o src/api/generated/schema.ts"`
  - 每个**后端**模块完成时必须跑 `pnpm run gen:api` 并 commit，否则前端用的是旧类型

**后端 (backend/)**
- 框架：Spring Boot 3.4.1
- JDK：Java 24
- 数据库：MySQL 5.7+
- ORM：Spring Data JPA + Hibernate 6.6
- 数据库迁移：**Flyway（Day 1 就上，禁用 `ddl-auto=update`）**
  - 目录：`backend/src/main/resources/db/migration/V{N}__{desc}.sql`
  - `application.yml`：`spring.jpa.hibernate.ddl-auto=validate` + `spring.flyway.enabled=true`
  - 每个后端模块的 ralph-loop 完成标准里必须包含「migration 脚本写完且 `mvn flyway:migrate` 成功 + JPA validate 通过」
- 安全：Spring Security + JWT
- API 文档：SpringDoc OpenAPI (Swagger)，暴露 `/v3/api-docs`
- 日志：Logback + JSON encoder（`logstash-logback-encoder`），MDC 注入 `traceId`，Day 1 就配
- Excel 处理：Apache POI 5.2（按需）
- 构建：Gradle

**工程**
- 目录结构：`frontend/` + `backend/` + `scripts/` + `docs/`
- 启动/停止：`scripts/start.sh` / `scripts/stop.sh`（Git Bash / WSL）
- 子路径部署能力（如需要）：Vite `base` + Vue Router `basename` + `VITE_API_BASE_URL`
- **Secrets 管理（Day 1 就规范）**：
  - 根目录 `.env.example`（提交）+ `.env`（如有敏感信息则 gitignore）
  - 后端 `application.yml`（提交，`spring.profiles.active` 默认为 `local`）+ `application-local.yml`（**提交**，放测试环境配置）+ `application-prod.yml`（生产环境配置）
  - `.gitignore` 起手就加：`*.pem`, `*.key`（真正的私钥文件）
  - **VPN 要求**：连接测试环境数据库前需确认 VPN 已开启
- **文档目录职责（写死，避免乱）**：
  - `docs/requirements/` = 业务需求（来自 Word，user 是权威，AI 不主动改）
  - `docs/superpowers/specs/` = 设计/方案（brainstorming 产物）
  - `docs/superpowers/plans/` = 可执行任务清单（writing-plans 产物，ralph-loop 输入）
  - `docs/superpowers/ralph-log.md` = 每个 ralph-loop run 的复盘记录
  - `docs/superpowers/frontend-verify-checklist.md` = 前端 MCP 浏览器验证通用流程（被前端 ralph-loop prompt 引用）

**测试**
- 后端：JUnit5 + MockMvc + Testcontainers
- 前端：Vitest + Testing Library
- E2E：chrome-devtools MCP

### Phase 3：实现计划（writing-plans skill）
调用 `superpowers:writing-plans` 把每个模块拆成 TDD 友好的小任务。每个任务的「完成时怎么验证」必须直接对应测试：
- **只有后端改动**：UnitTest（JUnit + MockMvc）或 curl 接口测试
- **有前端改动**：必须用 chrome-devtools MCP 在真实浏览器里跑通页面流程

### Phase 4：用 ralph-loop 迭代实现（核心）
**按功能点**跑 ralph-loop，不要把整个项目一把梭。

**4.0 环境准备（必须先确认）**
在执行任何 ralph-loop 命令之前，**必须先与用户确认并获取以下开发环境连接信息**：
- **VPN 连接状态**：确认 VPN 是否已开启，否则可能无法连接到 MySQL、Redis 等数据库
- **MySQL 连接信息**：host、port、database、username、password
- **Redis 连接信息**：host、port、password（如有）
- **其他中间件**：消息队列、对象存储等（按项目实际需要）

这些信息应该：
1. 写入 `backend/src/main/resources/application-local.yml`（**可以提交**，用于团队共享测试环境配置）
2. `application.yml` 中 `spring.profiles.active` 默认设置为 `local`，直接启动即可使用测试环境
3. 在根目录 `.env.example` 里列出变量名（提交），`.env` 里填实际值（如有敏感信息则 gitignore）
4. 在 `CLAUDE.md` 的「环境变量」章节记录变量名清单和 VPN 要求

**没有这些连接信息或 VPN 未开启，项目无法启动，ralph-loop 会因为无法验证而失败。**

**4.1 分支与 worktree 策略**
- 每个模块独立分支：`feat/auth-backend`, `feat/auth-frontend`, `feat/<module>-backend` ...
- 独立模块（无文件冲突）建议用 `superpowers:using-git-worktrees`，多个 ralph-loop 在不同 worktree 并行
- 主分支只接受 user review 过的合并，ralph-loop 不直接动 main
- 跑 ralph-loop 前 **打一个 baseline tag**：`git tag ralph-baseline-<module>-<date>`，方便失败时 reset

**4.2 后端模块**（示例：用户管理）
```
/ralph-loop "在 backend/ 下用 Spring Boot 实现用户管理（注册/登录/JWT/me）。
参考 docs/requirements/01-auth.md 与 docs/superpowers/specs/<auth-design>.md。
1. 写 Flyway migration：V1__create_user.sql（user 表 + 索引）
2. 先写失败的 JUnit 测试：注册成功、注册重复邮箱失败、登录成功、登录错误密码失败、JWT 校验
3. 实现 entity → repository → service → controller
4. mvn flyway:migrate 成功，mvn test 全绿（必须全量跑，不能只跑改过的）
5. 用 curl 跑通 4 个 endpoint，全部返回符合 spec 的 JSON
6. 跑 npm --prefix ../frontend run gen:api，commit 生成的 schema
7. 按「CLAUDE.md 维护规则」章节检查本次改动是否触发更新条件（新启动脚本 / 新工程约定 / 新测试账号 / 端口或环境变量改动 / 框架版本升级），触发则更新 `CLAUDE.md`
8. 在输出 promise 前，独立再跑一遍 mvn clean test + 全量 curl，任何一项失败不得输出 promise
9. 全部 OK 时输出 <promise>AUTH-BACKEND-DONE</promise>" \
  --max-iterations 30 --completion-promise "AUTH-BACKEND-DONE"
```

**4.3 前端模块 + chrome-devtools MCP 验证**（重要）
前端模块完成标准里**必须**包含「用 chrome-devtools MCP 在真实浏览器里跑通」，否则 ralph-loop 只会让单元测试过、UI 实际坏掉。

通用浏览器验证流程（MCP 工具顺序、console / network 红线、截图清理）写在 `docs/superpowers/frontend-verify-checklist.md`，prompt 里只写**模块特定部分**：入口 URL / user flow / 期望文案 / 允许的业务异常。

模板：
```
/ralph-loop "在 frontend/ 下实现登录页（参考 docs/requirements/01-auth.md）。
0. 前置检查：curl http://localhost:8080/actuator/health 必须 200；不是则先把后端起来再继续
1. 确认 src/api/generated/schema.ts 是最新（git 里没未提交修改），否则跑 npm run gen:api
2. 写 Vitest 单元测试：表单校验、错误展示、loading 状态
3. 实现页面 + Axios 调 /api/auth/login（用生成的类型）
4. npm test 全绿
5. 按 docs/superpowers/frontend-verify-checklist.md 跑浏览器验证，覆盖：
   - 入口：/login
   - 成功路径：正确账号 → 跳转首页
   - 错误路径：错误账号 → 显示\"密码错误\"
   - 业务预期允许的异常：登录失败时的 401
6. 按「CLAUDE.md 维护规则」章节检查本次改动是否触发更新条件（新启动脚本 / 新页面入口 URL / 新测试账号 / 新环境变量 / 框架版本升级），触发则更新 `CLAUDE.md`
7. 在输出 promise 前，独立再跑一遍 npm test + 完整浏览器流程，任何一项失败不得输出 promise
8. 全部 OK 时输出 <promise>LOGIN-PAGE-DONE</promise>" \
  --max-iterations 30 --completion-promise "LOGIN-PAGE-DONE"
```

**4.4 chrome-devtools MCP 在 ralph-loop 里的关键点**
- 通用流程在 checklist，prompt 里别再复制粘贴；模块特定的 URL / user flow / 文案 / 业务异常**必须**写进 prompt
- 中间迭代：每轮用 `console.get_messages` + `network.get_requests` 快速扫红，不跑完整浏览器流程
- 终态确认（输出 promise 前）：跑一次完整浏览器流程 + `page.snapshot`，验证页面最终状态
- console error / 非业务预期的 4xx-5xx 算失败，必须修

**4.5 模块顺序建议**
1. 基础设施：数据库初始化、Flyway 起手 migration、本地启动脚本
2. 后端 auth → 前端 auth（先把登录链路打通，后续模块都能复用 token）
3. 按业务依赖顺序逐个模块（每个模块：后端 → 前端 → MCP 联调验证）
4. 全部模块完成后再做跨模块 E2E

> CI 暂时跳过：当前是单人本地仓库、不推远端。需要多人协作或部署时再补一份 `.github/workflows/ci.yml`（后端 `./gradlew test` + 前端 `npm ci && npm run lint && npm test && npm run build`），监听 push/PR。

**4.6 ralph-loop 卡 max-iterations 时的恢复 checklist**
插件要求只有「完全为真」才输出 promise。如果 30 轮跑完没退出，**不要直接再加 max-iterations 重跑**，先诊断：
1. 看最后 3 次迭代的 diff 走向：
   - **越改越坏**（测试越来越红 / 出现 git 冲突 / 在反复改同一段代码）→ `git reset --hard ralph-baseline-<module>-<date>`，回到起点。多半是任务太大或验证标准太弱，**重新拆任务**（一个 ralph-loop 只覆盖一个 endpoint / 一个页面），再跑
   - **接近完成**（90% 测试过，剩 1-2 个边缘 case）→ 切手工模式补最后几步，**不要再开一轮 ralph**（ROI 极差）
2. 看是不是验证命令本身错了（比如端口占用、DB 没起）→ 修环境后再跑，不是 ralph 的锅
3. 看 prompt 里有没有「魔法步骤」（依赖 ralph 自己想清楚架构）→ 拆出去先做 design，再跑实现

**4.7 每个模块跑完记一笔到 `docs/superpowers/ralph-log.md`**
格式：
```
## <module> - <date>
- iterations used: N / 30
- exit: promise / max-iter / manual
- 踩坑: <一两句>
- 下次改进: <一两句>
```
用来估后续模块的预算和优化 prompt。

**4.8 CLAUDE.md 维护 + 分支收尾（重要）**
ralph-loop 输出 promise 退出后、commit 前，必须按下面规则同步项目级 `CLAUDE.md`，然后用 `superpowers:finishing-a-development-branch` skill 决定分支合并方式（合并/PR/保留）。

> **CLAUDE.md 更新原则：简明扼要，只记录重点**
> - 每条记录控制在 1-2 行，能一眼看懂
> - 只记录「下次新会话需要立刻知道」的信息（启动命令、端口、测试账号、关键约定）
> - 不要记录：实现细节、本次改动的 diff 描述、调试过程、临时 workaround、过期信息
> - 不要为了"显得有产出"而堆砌内容，宁可不写也不要写废话
> - 每次更新后，整份 CLAUDE.md 应该越来越精炼，而不是越来越臃肿
> - 如果发现某条记录已过时或冗余，**主动删除或合并**，保持文档清爽

> **ralph-loop 内部如何执行 CLAUDE.md 同步**
每个模块的 ralph-loop prompt 里都有一步「按 CLAUDE.md 维护规则检查」（见 4.2 / 4.3 模板）。检查动作：对照"触发事件"清单，命中任一就编辑 CLAUDE.md 对应章节；没命中就跳过，**不要为了"显得有产出"硬塞内容**。修改后 `git diff CLAUDE.md` 自检：每行新增都能对应到本轮真实的代码/脚本变化，不能凭空写"未来计划"，也不能把实现细节塞进去。

**该进 CLAUDE.md（每次新会话都需要的"项目说明书"，简明扼要为主）**
- 启动/停止命令：`scripts/start.sh`、`scripts/stop.sh`、本地端口、健康检查 URL（一行一条）
- 常用命令：`mvn test`、`npm test`、`npm run gen:api`、`mvn flyway:migrate`（只列命令，不解释用法）
- 技术栈核心版本（Spring Boot / Vue 3 / JDK / Node 等大版本，一行带过）
- 目录约定：`frontend/` / `backend/` / `docs/requirements/` / `docs/superpowers/...`（一行列清楚）
- 关键工程约定：Flyway 必须走 migration（禁 `ddl-auto=update`）、API 类型走 codegen、secrets 放 `.env` / `application-local.yml`（每条一句话）
- 本地测试账号：用户名 / 密码 / 角色，直接写明文（仅限本地 dev 假数据，表格形式最简洁）
- 主要环境变量名清单（不含值）：`VITE_API_BASE_URL`、`SPRING_PROFILES_ACTIVE` …（只列名）
- 关键文档地图：design / plans / ralph-log / frontend-verify-checklist 的位置（一行一条）

**不该进 CLAUDE.md**
- 一次性的业务实现细节（"这个 service 用了 XX 算法"）—— 代码和 git log 已经有
- 近期改动记录、bug 修复说明 —— 走 commit message 和 `ralph-log.md`
- 临时调试发现、过期的 workaround
- 冗长的解释、多段落说明 —— CLAUDE.md 不是文档，是速查表
- 重复的内容（同一条信息只在最相关的章节出现一次）

**触发更新的事件（满足任一即更新）**
- 新增/修改启动脚本、端口、健康检查路径
- 新增/删除/重命名顶层目录或关键约定目录
- 框架核心版本升级（Spring Boot / Vue 3 / JDK / Node 大版本动了）
- 新增/修改测试账号 → 直接改 CLAUDE.md「本地测试账号」章节
- 新增/修改环境变量、profile、secrets 加载方式
- 新增重要工程命令（codegen、migration、E2E 启动）
- 关键文档新增或挪位置

**不触发更新的事件**
- 仅新增业务代码 / 业务接口 / 页面
- 仅修 bug、重构内部实现
- 仅改测试用例

### Phase 5：联调与验证
- 前后端接通后，用 chrome-devtools MCP 跑跨模块 E2E（登录 → 进核心页面 → 创建数据 → 列表展示 → 详情）
- 走通后把 chrome-devtools MCP 的验证步骤写进 design 文档，作为该模块的回归用例
- 启动脚本（`scripts/start.sh`）：再跑一轮 ralph-loop，完成标准 = 脚本启动后前后端都能访问、chrome-devtools MCP E2E 通过

## 关键经验（避坑）
1. **Word 必须先按功能点拆 md**：整份大文档塞进 prompt 会被截断，模型抓不住重点；而且大模块必须拆到子功能粒度才能让 ralph-loop 好收敛
2. **不要把整份需求直接丢给 ralph-loop**：模糊需求 + 没有验证 = 死循环或一堆没用的代码
3. **每个 ralph-loop 都要有可执行的 verify**：`mvn test` / `npm test` / `curl` / chrome-devtools MCP
4. **前端模块必须用浏览器验证**：单元测试过 ≠ 页面能用
5. **永远带 `--max-iterations`**：默认 20-30，防无限循环
6. **completion-promise 要绑定事实**：最好等同于「测试全过 + 浏览器验证全过」
7. **逐模块跑**：任务越聚焦，ralph-loop 收敛越快、成本越低
8. **截图测完即删**（你 CLAUDE.md 的硬性规则）
9. **禁止虚假声明完成**：插件本身要求只有「完全为真」才输出 promise，不能为了退出而骗
10. **每个 ralph-loop 退出前必须同步 CLAUDE.md**：按 4.8 规则判断是否触发更新，触发就改、不触发就跳过；**记录要简明扼要，每条 1-2 行**，只记重点（启动命令、端口、测试账号、关键约定），不堆砌实现细节
11. **ralph-loop 执行前必须先确认开发环境连接信息和 VPN 状态**：MySQL、Redis 等中间件的连接信息必须提前获取并配置好，VPN 必须开启，否则项目无法启动，ralph-loop 会因验证失败而卡住
12. **application-local.yml 可以提交**：用于团队共享测试环境配置，`application.yml` 的 `spring.profiles.active` 默认设置为 `local`

## 验证（怎么知道每阶段做对了）
- Phase 1：Word 已转 md 并按功能点拆成 `docs/requirements/*.md`（功能点清单在 00-overview.md）；`99-open-questions.md` 全部标 `[resolved]` 或 `[deferred]`，无 `[open]`
- Phase 2：design 文档已 commit，user 已 review approve；Flyway / OpenAPI codegen / secrets / 日志 都已落地
- Phase 3：plan 文件每条任务都有可执行的 verify 命令
- Phase 4：每个模块的 ralph-loop 都用真实的 promise 退出（不是被 max-iterations 卡掉）；前端模块都通过 chrome-devtools MCP 浏览器验证；`docs/superpowers/ralph-log.md` 有对应记录；`CLAUDE.md` 与本轮代码改动一致（启动命令、目录约定、测试账号、环境变量都最新）；**开发环境连接信息已确认并配置完成**
- Phase 5：`scripts/start.sh` 启动后跨模块 E2E 全绿，截图全部清理

## 关键文件 / 工具引用
- ralph-loop 命令：`/ralph-loop "<prompt>" --max-iterations N --completion-promise "TEXT"`
- 取消：`/cancel-ralph`
- brainstorming skill：`superpowers:brainstorming`
- writing-plans skill：`superpowers:writing-plans`
- TDD skill：`superpowers:test-driven-development`
- worktree skill：`superpowers:using-git-worktrees`
- chrome-devtools MCP：`page.navigate` / `input.insert_text` / `locator.click` / `locator.wait_for` / `console.get_messages` / `network.get_requests` / `page.snapshot`（Playwright API）
- Word 转 md：`pandoc input.docx -o requirements-raw.md`
- API 类型生成：`openapi-typescript http://localhost:8080/v3/api-docs -o frontend/src/api/generated/schema.ts`
- 数据库迁移：Flyway，`backend/src/main/resources/db/migration/V{N}__{desc}.sql`
- baseline tag：`git tag ralph-baseline-<module>-<date>`（每次 ralph-loop 跑前打）
- 需求拆分目录：`docs/requirements/NN-<module>.md`
- 设计文档：`docs/superpowers/specs/YYYY-MM-DD-<topic>-design.md`
- 任务计划：`docs/superpowers/plans/<module>-plan.md`
- ralph-loop 复盘：`docs/superpowers/ralph-log.md`
- 项目说明书：根目录 `CLAUDE.md`（启动命令 / 技术栈 / 目录约定 / 本地测试账号，按 4g 规则维护）

## 第一步建议
1. 把 Word 文件路径告诉我
2. 我先通读 → 给你一份**模块拆分草案**（哪些模块、依赖关系、建议顺序）
3. 你确认后，我把 Word 转成 `docs/requirements/NN-<module>.md` 多个文件
4. 进入 brainstorming → writing-plans → ralph-loop 链路
