# AGENTS.md — AI Agent 协作规则

本文档为 AI Agent 提供**硬性规则**。详细导航见 `docs/context/code-map.md`。

---

## 1. 硬性规则

### 1.1 代码规范
- **中文注释**：公共类/方法 Javadoc 中文注释；复杂逻辑行内注释
- **异常处理**：`BizException` + `ErrorCode`，通过 `GlobalExceptionHandler` 统一处理
- **安全**：敏感信息（API Key）必须加密存储，禁止硬编码

### 1.2 Git 规则
- **分支**：`feature/{变更名}` / `fix/{问题}`
- **提交**：必须使用 Conventional Commits（见第 2 节）
- **禁止**： 在 `main/master` 分支上进行开发；
- **禁止**：`push --force` 到 main/master；直接提交到 main/master

### 1.3 前端规范
- API 调用：通过 `src/api/request.ts` 封装
- 状态管理：使用 Pinia stores
- 权限控制：`v-permission` 指令

### 1.4 工具使用
- **检查目录是否存在**：优先用 `LS` 工具，而非 `Glob`。Glob 匹配文件路径，空目录不会被匹配到，返回空结果无法区分"目录不存在"和"目录存在但无匹配文件"
- **按模式搜索文件**：用 `Glob`（如 `**/*.java`）
- **搜索文件内容**：用 `Grep`

### 1.5 禁止事项
- 硬编码密码/API Key/Token
- `application.yml` 明文存储密钥
- 直接修改他人分支

---

## 2. 提交规范

### 格式
```
<type>(<scope>): <subject>

[body]

Closes #xxx
```

### Type
| Type | 说明 |
|------|------|
| `feat` | 新功能 |
| `fix` | Bug 修复 |
| `docs` | 文档 |
| `refactor` | 重构 |
| `perf` | 性能优化 |
| `test` | 测试 |
| `chore` | 构建/工具 |

### Scope
`common` | `runtime` | `admin` | `web` | `security` | `db` | `ci`

---

## 3. OpenSpec 变更管理

项目使用 OpenSpec 工作流，产物在 `openspec/`。

| 阶段 | 说明 |
|------|------|
| `open` | 探索想法 |
| `design` | 深度设计 |
| `build` | 实现 |
| `verify` | 验证 |
| `archive` | 归档 |

**命令**：
```bash
openspec list                              # 列出变更
openspec new change <name>                 # 创建变更
openspec status --change <name>            # 查看状态
```

**config.yaml 规则**：创建变更后立即执行 `git checkout -b feature/{变更名}`
