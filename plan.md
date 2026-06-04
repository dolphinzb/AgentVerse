# AgentVerse — 企业级智能体开发运营平台 总体规划

## 一、平台定位与愿景

AgentVerse 定位为面向企业的**一站式智能体开发运营平台**，让开发者能够：

- **低代码/零代码** 创建和配置 Agent
- **可视化管理** 工具、知识库、工作流
- **多用户协同** 支持组织内多用户协同工作
- **全链路可观测** 监控 Agent 运行状态
- **生产级运维** 版本管理、灰度发布、性能监控

***

## 二、部署架构：多模块单进程

**核心设计原则：** 所有功能模块作为独立 Maven module 开发，但通过 `agent-platform-runtime` 统一依赖引入，最终打包为一个 Spring Boot fat jar，`java -jar` 一键启动。**不是微服务，是单进程内的模块化库架构。**

```
┌─────────────────────────── 前端 (Vue 3) ───────────────────────────┐
│  Studio │ Chat │ Tool Mgr │ Knowledge │ Workflow │ Monitor │ Admin  │
└─────────────────────────────┬───────────────────────────────────────┘
                              │ REST / SSE
                              ▼
┌─────────────────── agent-platform-runtime (唯一启动入口) ───────────┐
│                                                                     │
│  ┌─ WebFilter 层 ─────────────────────────────────────────────────┐ │
│  │  JWT 认证 · RBAC 鉴权 · 限流 · CORS · 请求日志                 │ │
│  └────────────────────────────────────────────────────────────────┘ │
│                                                                     │
│  ┌─ Controller 层（各模块提供自己的 Controller）──────────────────┐ │
│  │  ChatCtrl │ AgentCtrl │ ToolCtrl │ KnowledgeCtrl │ WorkflowCtrl│ │
│  │  MonitorCtrl │ AdminCtrl │ AuthCtrl                             │ │
│  └────────────────────────────────────────────────────────────────┘ │
│                                                                     │
│  ┌─ Service 层（各模块提供自己的 Service）────────────────────────┐ │
│  │  ┌─────────┐ ┌───────┐ ┌──────────┐ ┌────────┐ ┌───────────┐ │ │
│  │  │ runtime │ │studio │ │knowledge │ │workflow│ │observabil │ │ │
│  │  │ (核心)  │ │       │ │          │ │        │ │   ity     │ │ │
│  │  └─────────┘ └───────┘ └──────────┘ └────────┘ └───────────┘ │ │
│  │  ┌─────────┐ ┌───────┐ ┌──────────┐                          │ │
│  │  │  tool   │ │security│ │  admin   │                          │ │
│  │  │         │ │       │ │          │                          │ │
│  │  └─────────┘ └───────┘ └──────────┘                          │ │
│  └────────────────────────────────────────────────────────────────┘ │
│                                                                     │
│  ┌─ agent-platform-common (公共层) ──────────────────────────────┐ │
│  │              DTO · Entity · Exception · Util · Config           │ │
│  └────────────────────────────────────────────────────────────────┘ │
│                                                                     │
│  ┌─ AgentScope-Java 1.1.0 (核心引擎) ───────────────────────────┐ │
│  │    HarnessAgent (生产级 Agent 引擎，封装 ReActAgent)            │ │
│  │    WorkspaceManager · 文件系统 · SubAgent · Compaction          │ │
│  │    Toolkit · Memory · Pipeline · RAG · Skill · Hook · Tracing  │ │
│  └────────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────────┘
                              │
                  ┌───────────┼───────────┐
                  ▼           ▼           ▼
             PostgreSQL     Redis       ChromaDB
             (主数据库)    (缓存/会话)   (向量存储)
                              │
                         本地文件系统
                         (文件存储)
```

**模块间调用方式：** 直接 Java 方法调用（同一 JVM 进程内），无 RPC/HTTP 开销。

**按需裁剪：** 如果某个模块暂时不需要，直接从 runtime 的 pom.xml 中移除依赖即可，不影响其他模块运行。例如初期不需要 workflow，移除 `agent-platform-workflow` 依赖即可。

***

## 三、模块划分

**Maven 多模块，单进程部署。** runtime 是唯一可运行的模块，其余业务模块均作为库（jar）被 runtime 依赖引入。

```
AgentVerse (parent pom)
│
├── agent-platform-common          ← 公共基础（DTO、Entity、异常、工具类）
│
├── agent-platform-security        ← 安全库（认证、权限、审计、用户上下文拦截器）
├── agent-platform-runtime         ← ⭐ 核心运行时 + 唯一 Spring Boot 启动入口
├── agent-platform-studio          ← Studio 库（Agent 设计器、版本管理、评测）
├── agent-platform-tool            ← 工具库（工具注册、MCP、工具组、SubAgent）
├── agent-platform-skill           ← 技能库（Skill CRUD、仓库管理、生命周期）
├── agent-platform-knowledge       ← 知识库库（RAG、文档管理、向量检索）
├── agent-platform-workflow        ← 工作流库（DAG 编排、多Agent协作、调度）
├── agent-platform-observability   ← 可观测性库（Trace、指标、告警）
├── agent-platform-admin           ← 运维库（用户、模型、系统配置）
│
└── agent-platform-web             ← Vue 3 前端项目（独立构建，非 Maven 模块）
```

**模块依赖关系：**

```
common ← security
  ↑        ↑
  └────────┴── runtime ──→ studio
                      ├──→ tool
                      ├──→ skill
                      ├──→ knowledge
                      ├──→ workflow
                      ├──→ observability
                      └──→ admin
```

**runtime 的 pom.xml 示例（阶段一仅依赖 common + security）：**

```xml
<dependencies>
    <!-- 必选 -->
    <dependency>
        <groupId>com.agentverse</groupId>
        <artifactId>agent-platform-common</artifactId>
    </dependency>
    <dependency>
        <groupId>com.agentverse</groupId>
        <artifactId>agent-platform-security</artifactId>
    </dependency>

    <!-- 按需引入（阶段二+逐步打开） -->
    <!-- <dependency> agent-platform-studio </dependency>      -->
    <!-- <dependency> agent-platform-tool </dependency>        -->
    <!-- <dependency> agent-platform-skill </dependency>       -->
    <!-- <dependency> agent-platform-knowledge </dependency>   -->
    <!-- <dependency> agent-platform-workflow </dependency>    -->
    <!-- <dependency> agent-platform-observability </dependency> -->
    <!-- <dependency> agent-platform-admin </dependency>       -->
</dependencies>
```

**每个业务模块的内部结构约定：**

```
agent-platform-xxx/
├── pom.xml                           ← 普通 jar 库（不含 Spring Boot Maven Plugin，不能独立启动）
└── src/main/java/com/agentverse/xxx/
    ├── controller/                   ← REST Controller（Spring Bean）
    ├── service/                      ← 业务逻辑（Spring Bean）
    ├── mapper/                       ← MyBatis-Plus Mapper
    ├── config/                       ← @Configuration 配置类
    └── META-INF/
        └── spring/
            └── org.springframework.boot.autoconfigure.AutoConfiguration.imports
                ← Spring Boot 3 自动配置注册文件
```

**说明：** 
- 业务模块是**普通 jar 库**，不包含 Spring Boot Maven Plugin，因此不能 `java -jar` 独立启动
- 但模块中包含 Spring 组件（Controller、Service、@Configuration 等），这些都是普通的 Spring Bean
- runtime 模块启动时，会通过 classpath 扫描自动发现并加载这些组件
- `AutoConfiguration.imports` 文件告诉 Spring Boot 哪些 `@Configuration` 类需要自动加载

***

## 四、各模块详细功能规划

### 4.1 agent-platform-common（公共基础）

| 子包          | 内容                                              |
| ----------- | ----------------------------------------------- |
| `dto`       | ApiResponse、PageResult、各业务 DTO                  |
| `entity`    | 所有数据库实体（User、Agent、Tool、KnowledgeBase 等） |
| `enums`     | 状态枚举（AgentStatus、ToolStatus、PublishStatus 等）    |
| `exception` | BizException、ErrorCode 统一错误码                    |
| `constant`  | 系统常量                                            |
| `util`      | UUID 工具、JSON 工具、加解密工具                           |

### 4.2 agent-platform-runtime（运行时引擎）⭐ 核心 + 唯一启动入口

这是**唯一包含 Spring Boot 启动类**的模块，也是与 AgentScope-Java 深度集成的核心模块。负责：

- Spring Boot 应用启动与配置加载
- 各业务模块的依赖引入与 Bean 扫描
- WebFilter 层提供网关级能力（认证、限流、CORS 等）
- **基于 HarnessAgent 构建生产级 Agent 引擎**（不再直接使用 ReActAgent）

**子包结构：**

| 子包                      | 功能              | 说明                                                                 |
| ----------------------- | --------------- | ------------------------------------------------------------------ |
| `AgentVerseApplication` | Spring Boot 启动类 | `@SpringBootApplication` 主类                                        |
| `filter`                | WebFilter 层     | 替代独立网关，提供认证/限流/CORS/日志等能力                                          |
| `config`                | 全局配置            | WebFlux 配置、MyBatis-Plus 配置、Redis 配置等                               |
| `model`                 | 模型管理            | 多提供商注册、负载均衡、Fallback                                                 |
| `harness`               | **HarnessAgent 引擎** | **生产级 Agent 构建与生命周期管理**                                              |
| `harness.builder`       | HarnessAgent 构建器 | 从数据库规范化表读取配置，转换为 HarnessAgent.Builder，集成工作区、文件系统、SubAgent 等    |
| `harness.workspace`     | 工作区管理           | WorkspaceManager 适配，支持用户工作区隔离（workspace/user/{userId}/agent/{agentId}） |
| `harness.filesystem`    | 文件系统配置          | 三种模式选择：本地（单实例）、远程共享（多实例）、沙箱（隔离执行）                                  |
| `harness.subagent`      | SubAgent 编排     | 基于 SubagentsHook 的 SubAgent 工厂注册与管理                              |
| `harness.hook`          | 生产级 Hook 扩展    | 平台定制 Hook（用户上下文注入、对话记录、Token 计量等）                                   |
| `agent`                 | Agent 生命周期      | 基于 HarnessAgent 的创建/加载/执行/销毁                                    |
| `tool`                  | 工具注册与执行         | 扩展 Toolkit，集成 HarnessAgent 内置工具（FilesystemTool、ShellExecuteTool 等）     |
| `memory`                | 记忆管理            | 基于 HarnessAgent 的记忆维护（MemoryMaintenanceHook、MemoryFlushHook）            |
| `session`               | 会话管理            | 基于 SessionPersistenceHook 的会话持久化                                 |
| `chat`                  | 对话服务            | 同步/流式 SSE 对话（调用 HarnessAgent.run）                                |
| `rag`                   | RAG 集成          | 知识库检索增强                                                             |
| `hook`                  | 钩子扩展            | HITL、审计、限流等 Hook（与 HarnessAgent 内置 Hook 协同）                         |

**WebFilter 层（替代独立 API Gateway）：**

| Filter                 | 功能      | 说明                               |
| ---------------------- | ------- | -------------------------------- |
| `JwtAuthFilter`        | JWT 认证  | 解析 Token、校验签名、提取用户信息             |
| `RbacPermissionFilter` | RBAC 鉴权 | 基于 `@RequirePermission` 注解校验权限   |
| `UserContextFilter`  | 用户上下文   | 从请求中提取 user\_id，注入 ThreadLocal |
| `RateLimitFilter`      | 限流熔断    | 基于 Redis + Lua 的令牌桶/滑动窗口限流       |
| `CorsFilter`           | 跨域处理    | WebFlux CorsConfiguration        |
| `AccessLogFilter`      | 访问日志    | 记录请求方法、路径、耗时、状态码                 |
| `TraceIdFilter`        | 链路追踪    | 生成/传播 TraceId，注入 MDC             |

**为什么使用 HarnessAgent 而非直接使用 ReActAgent：**

`HarnessAgent` 是 AgentScope-Java 1.1.0-RC 推出的**生产级 Agent 引擎**，它在 `ReActAgent` 之上封装了工程落地所需的全部能力。直接使用 ReActAgent 意味着我们需要自己实现记忆刷盘、上下文压缩、会话持久化、工作区管理、SubAgent 编排、上下文溢出恢复等——这些 HarnessAgent 已经做好了。

```
HarnessAgent vs ReActAgent 对比：
─────────────────────────────────────────────────────────────
能力                    ReActAgent         HarnessAgent
─────────────────────────────────────────────────────────────
记忆管理                InMemoryMemory      InMemoryMemory + MemoryFlushHook
                                           + MemoryMaintenanceHook
上下文溢出              手动处理            自动检测 + CompactionHook 压缩重试
会话持久化              手动 saveTo/loadFrom  SessionPersistenceHook 自动
工作区上下文            无                  WorkspaceContextHook
                                           (AGENTS.md / MEMORY.md / KNOWLEDGE.md)
文件系统                无                  AbstractFilesystem
                                           (本地/远程共享/沙箱隔离)
Shell 执行              无                  ShellExecuteTool
文件读写                无                  FilesystemTool
记忆检索                无                  MemorySearchTool / MemoryGetTool
SubAgent 编排           SubAgentTool        SubagentsHook + DefaultAgentManager
                                           + AgentSpawnTool (后台任务)
Skill 加载              手动 SkillBox       自动从 workspace/skills/ 加载
工具结果过大            无                  ToolResultEvictionHook
分布式沙箱              无                  SandboxManager (Docker/K8s/E2B)
─────────────────────────────────────────────────────────────
结论：HarnessAgent 是 ReActAgent 的生产化包装，
      我们应该基于它构建而非重复实现这些能力。
```

**对应 AgentScope 能力：**

| 功能          | 对应 AgentScope 能力 |
| ----------- | ---------------- |
| `harness`   | `HarnessAgent.builder()`（核心引擎）、`WorkspaceManager`、`WorkspaceSession` |
| `model`     | `DashScopeChatModel`、`OpenAIChatModel`、`AnthropicChatModel`、`OllamaChatModel`、`GeminiChatModel`、`ModelRegistry` |
| `agent`     | `HarnessAgent`（内部委托 `ReActAgent`）、`Agent` 接口 |
| `tool`      | `Toolkit`、`@Tool`、`ToolRegistry`、`ToolGroup`、MCP 支持；内置 `FilesystemTool`、`ShellExecuteTool`、`MemorySearchTool`、`MemoryGetTool`、`SessionSearchTool` |
| `memory`    | `InMemoryMemory` + `MemoryFlushHook` + `MemoryMaintenanceHook` + `CompactionHook`；`LongTermMemory`、`extensions-memory-bailian`、`extensions-mem0` |
| `session`   | `SessionPersistenceHook` + `WorkspaceSession`；`extensions-session-redis`、`extensions-session-mysql` |
| `filesystem`| `LocalFilesystemSpec`（单实例）、`RemoteFilesystemSpec`（多实例共享）、`SandboxFilesystemSpec`（沙箱隔离：Docker/K8s/E2B/Daytona） |
| `subagent`  | `SubagentsHook`、`DefaultAgentManager`、`AgentSpawnTool`、`WorkspaceTaskRepository` |
| `chat`      | `harnessAgent.call(msgs, ctx)`、`harnessAgent.stream(msgs, options, ctx)` |
| `rag`       | `Knowledge`、`GenericRAGHook`、`extensions-rag-*` |
| `hook`      | 内置：`AgentTraceHook`、`WorkspaceContextHook`、`MemoryFlushHook`、`MemoryMaintenanceHook`、`CompactionHook`、`ToolResultEvictionHook`、`SessionPersistenceHook`、`SubagentsHook`；平台扩展：用户上下文 Hook、对话记录 Hook、Token 计量 Hook |
| `config`    | HarnessAgent Builder 参数映射（数据库表字段 → Builder 方法调用） |

**Agent 配置存储策略：规范化数据库表（单一事实源）**

Agent 配置采用**规范化数据库表设计**作为唯一的事实源（Single Source of Truth），所有配置项直接存储在关系表中，通过外键约束保证数据完整性。

| 存储层 | 格式 | 用途 |
|--------|------|------|
| **运行时配置** | 数据库规范化表 | Agent 定义、模型配置、工具引用、知识库引用、SubAgent 声明等 |
| **HarnessAgent 构建** | 数据库读取 → Builder API | 直接从数据库表加载配置，调用 HarnessAgent.Builder 方法 |
| **版本快照** | `agent_version.snapshot_yaml` | 发布时序列化完整配置为 YAML，用于版本回滚和审计 |
| **导入/导出** | YAML 文件 | 跨环境迁移、模板市场、Agent 分享（派生格式） |
| **API 返回** | JSON | 前端表单编辑使用 JSON，不使用 YAML |

**为什么选择数据库表而非 YAML Blob：**
- **关系查询** —— "哪些 Agent 使用了工具 X？" 直接 JOIN `agent_tool_ref` 表
- **外键约束** —— 删除工具/知识库时，数据库自动级联删除引用关系
- **字段级更新** —— 前端可以独立修改 Prompt、工具列表、模型配置，无需解析整个配置
- **统计分析** —— "有多少 Agent 启用了 RAG？" 直接 COUNT + WHERE
- **并发安全** —— 多人同时编辑不同配置维度不会互相覆盖
- **事务保证** —— 创建 Agent 时，主表 + 子表可以在同一事务中原子提交

**数据库表 → HarnessAgent.Builder 映射：**

```java
// AgentLoaderService.java — 从数据库直接构建 HarnessAgent
public HarnessAgent loadAgent(String agentId) {
    // 从数据库表读取配置（规范化设计，无 YAML 解析）
    AgentDefinition def = agentDefinitionMapper.selectById(agentId);
    AgentModelConfig modelCfg = agentModelConfigMapper.selectByAgentId(agentId);
    List<AgentToolRef> toolRefs = agentToolRefMapper.selectByAgentId(agentId);
    List<AgentSkillRef> skillRefs = agentSkillRefMapper.selectByAgentId(agentId);
    List<AgentKnowledgeRef> kbRefs = agentKnowledgeRefMapper.selectByAgentId(agentId);
    List<AgentSubagentDecl> subagents = agentSubagentDeclMapper.selectByAgentId(agentId);
    List<AgentContextFile> ctxFiles = agentContextFileMapper.selectByAgentId(agentId);
    AgentLongTermMemory ltm = agentLongTermMemoryMapper.selectByAgentId(agentId);
    
    return HarnessAgent.builder()
        .name(def.getName())
        .description(def.getDescription())
        .sysPrompt(def.getSysPrompt())
        .model(modelFactory.createFromConfig(modelCfg))
        .maxIters(def.getMaxIterations())
        
        // ── 工作区配置（agent_definition 主表字段）──
        .workspace(Path.of(def.getWorkspaceBaseDir(), def.getCreatedBy(), agentId))
        .maxContextTokens(def.getMaxContextTokens())
        .additionalContextFiles(ctxFiles.stream()
            .map(AgentContextFile::getFilePath).toList())
        
        // ── 文件系统配置（agent_definition 主表字段）──
        .filesystem(resolveFilesystemSpec(def))
        .disableFilesystemTools(!def.getEnableFilesystemTools())
        .disableShellTool(!def.getEnableShellTool())
        
        // ── 记忆配置（agent_definition 主表 + agent_long_term_memory 子表）──
        .disableMemoryHooks(!def.getEnableMemoryFlush())
        .disableMemoryMaintenance(!def.getEnableMemoryMaintenance())
        .longTermMemory(ltm != null ? resolveLongTermMemory(ltm) : null)
        
        // ── 上下文压缩（agent_definition 主表字段）──
        .compaction(def.getEnableCompaction()
            ? CompactionConfig.builder()
                .triggerPercentage(def.getCompactionTriggerPct())
                .keepRecentMessages(def.getCompactionKeepRecent())
                .build()
            : null)
        
        // ── 工具结果驱逐（agent_definition 主表字段）──
        .toolResultEviction(def.getToolResultEvictionEnabled()
            ? ToolResultEvictionConfig.builder()
                .maxResultChars(def.getToolResultEvictionMaxChars())
                .build()
            : null)
        
        // ── SubAgent 编排（agent_subagent_decl 子表）──
        .disableSubagents(!def.getEnableSubagents())
        .subagents(subagentFactory.createFromDeclarations(subagents))
        
        // ── 工具注册（agent_tool_ref 关联表）──
        .toolkit(toolkitFactory.createFromRefs(toolRefs))
        
        // ── Skill 技能（agent_skill_ref 关联表 → 从文件系统加载定义文件）──
        .skillBox(skillBoxFactory.createFromRefs(skillRefs))
        
        // ── RAG 知识库（agent_knowledge_ref 关联表）──
        .knowledges(knowledgeFactory.createFromRefs(kbRefs))
        
        // ── 计划执行（agent_definition 主表字段）──
        .planNotebook(def.getEnablePlan() ? PlanNotebook.builder().build() : null)
        
        // ── 会话持久化（agent_definition 主表字段）──
        .session(sessionFactory.create(def.getSessionBackend()))
        .disableSessionPersistence(!def.getEnableSessionPersistence())
        
        // ── 生成选项（agent_model_config 子表字段）──
        .generateOptions(GenerateOptions.builder()
            .temperature(modelCfg.getTemperature())
            .topP(modelCfg.getTopP())
            .maxTokens(modelCfg.getMaxTokens())
            .build())
        .build();
}
```

**YAML 导入/导出格式（仅用于导入导出和版本快照，非运行时存储）：**

```yaml
apiVersion: v1
kind: Agent
metadata:
  name: "customer-service-bot"
  version: "1.2.0"
  createdBy: "user-001"
spec:
  description: "智能客服机器人"
  sysPrompt: |
    你是一个专业的客服助手...
  maxIterations: 10
  
  # ── 模型（导出时展开，运行时从 agent_model_config 表读取）──
  model:
    provider: dashscope
    modelName: qwen-plus
    temperature: 0.7
    maxTokens: 4096
    topP: 0.9
  
  # ── HarnessAgent 工作区 ──
  workspace:
    mode: isolated
    baseDir: "/data/workspaces"
    contextFiles: ["SOUL.md", "PREFERENCE.md"]
    maxContextTokens: 8000
  
  # ── HarnessAgent 文件系统 ──
  filesystem:
    type: local
    enableShell: true
    enableFilesystem: true
  
  # ── HarnessAgent 记忆 ──
  memory:
    enableFlush: true
    enableMaintenance: true
    longTerm:
      enabled: true
      mode: BOTH
  
  # ── HarnessAgent 上下文压缩 ──
  compaction:
    enabled: true
    triggerThreshold: 80
    keepRecentMessages: 10
  
  # ── HarnessAgent 工具结果驱逐 ──
  toolResultEviction:
    enabled: true
    maxResultChars: 4000
  
  # ── HarnessAgent SubAgent ──
  subagents:
    enabled: true
    declarations:
      - name: "researcher"
        description: "专门负责信息检索和分析"
        model: "openai:gpt-4o-mini"
        workspace: { mode: isolated, path: "agents/researcher/workspace" }
        tools: ["web_search", "document_reader"]
  
  # ── 工具引用（导出时包含完整引用，运行时从 agent_tool_ref 表 JOIN）──
  tools:
    - toolId: "tool-001"
      name: "search_order"
  
  # ── 技能引用（导出时包含完整引用，运行时从 agent_skill_ref 表 JOIN）──
  skills:
    - skillId: "skill-001"
      name: "code-review"
      description: "代码审查技能"
      filePath: "/data/skills/code-review/SKILL.md"
  
  # ── RAG 引用 ──
  knowledgeBases:
    - kbId: "kb-001"
      ragMode: GENERIC
      retrieveLimit: 5
      scoreThreshold: 0.5
  
  # ── 会话 ──
  session:
    enablePersistence: true
    backend: json
```

### 4.3 agent-platform-studio（可视化工作台）

| 功能            | 说明                                            |
| ------------- | --------------------------------------------- |
| **Agent 设计器** | 拖拽式 Agent 配置（选模型 → 写 Prompt → 挂工具 → 配记忆 → 发布） |
| **Prompt 管理** | 版本管理、A/B 测试、Prompt 模板库                        |
| **在线调试**      | Playground 实时对话测试，支持查看每一步 Reasoning/Acting 过程 |
| **版本管理**      | Agent 版本快照、回滚、灰度发布（按流量比例）                     |
| **克隆/模板**     | 从模板创建 Agent、克隆现有 Agent                        |
| **批量评测**      | 预设测试集自动化评测，输出准确率/相关性等指标                       |

### 4.4 agent-platform-tool（工具管理中心）

| 功能              | 说明                                       |
| --------------- | ---------------------------------------- |
| **内置工具库**       | HTTP 调用、数据库查询、代码执行、文件读写等                 |
| **自定义工具**       | Java `@Tool` 注解开发，自动注册                   |
| **MCP 协议接入**    | 通过 `McpClientBuilder` 接入外部 MCP Server 工具 |
| **工具市场**        | 分类浏览、安装/卸载、评分评价                          |
| **工具组管理**       | `ToolGroup` 动态装备/卸载（`enableMetaTool`）    |
| **工具审核**        | 工具发布审核流程、安全扫描                            |
| **SubAgent 工具** | 通过 `SubAgentTool` 将其他 Agent 作为工具调用       |

### 4.5 agent-platform-skill（技能管理中心）

Skill 是 Agent 的**可插拔能力单元**，比 Tool 更高阶——一个 Skill 可以包含 Prompt 模板、多个工具组合、以及完整的执行生命周期。与 Tool（单一函数调用）不同，Skill 是「一套完整的能力方案」。

**存储策略：混合存储**
- **元数据 → 数据库**：`skill_definition` 表存储 Skill 的基本信息（名称、描述、分类、版本、状态等）
- **定义文件 → 文件系统**：Skill 的完整定义（Markdown 格式，包含 Prompt、工具声明、执行逻辑）存储在文件系统中（如 `/data/skills/{name}/SKILL.md`）
- **引用关系 → 数据库**：`agent_skill_ref` 表记录 Agent 与 Skill 的绑定关系

| 功能 | 说明 |
|------|------|
| **Skill CRUD** | 创建/编辑/删除 Skill，管理 Skill 元数据（数据库）+ 定义文件（文件系统） |
| **Skill 仓库管理** | 多仓库后端：`FileSystemSkillRepository`（默认）、`GitSkillRepository`、`MySQLSkillRepository` |
| **Skill 解析** | `MarkdownSkillParser` 解析 Skill 定义文件（Markdown 格式） |
| **Skill 市场** | 分类浏览、安装/卸载、版本管理 |
| **Skill 注册到 Agent** | 通过 `SkillBox` 将 Skill 注入 Agent，`SkillHook` 在 PreCallEvent 自动激活 |
| **Skill 工具化** | `SkillToolFactory` 将 Skill 转化为 Tool 注册到 Toolkit |
| **Skill 版本控制** | Skill 定义文件的版本管理、回滚（`skill_version` 表记录版本历史） |
| **Skill 审核** | 发布审核流程、安全扫描 |

**运行时与管理面的职责分离：**

```
agent-platform-skill (管理面 — 本模块)
  ├── Skill CRUD、版本管理、市场
  ├── Skill 仓库管理（File/Git/MySQL）
  ├── Skill 解析、校验、审核
  └── Skill 配置生成（为 Agent 生成 SkillBox 配置）

agent-platform-runtime (运行面)
  ├── 接收 SkillBox 配置
  ├── ReActAgent.builder().skillBox(skillBox) 注入
  ├── SkillHook 自动激活 Skill
  └── SkillToolFactory 注册 Skill 工具到 Toolkit
```

**对应 AgentScope 能力：** `SkillBox`、`AgentSkill`、`SkillRegistry`、`SkillHook`、`SkillToolFactory`、`FileSystemSkillRepository`、`extensions-skill-git-repository`、`extensions-skill-mysql-repository`

### 4.6 agent-platform-knowledge（知识库管理）

| 功能           | 说明                                                |
| ------------ | ------------------------------------------------- |
| **知识库 CRUD** | 创建/删除知识库，配置 Embedding 模型                          |
| **文档管理**     | 上传（PDF/Word/TXT/Markdown）、解析、分块（Chunk）            |
| **向量化**      | 自动 Embedding 并存入向量库                               |
| **检索测试**     | Playground 测试检索效果，调整 scoreThreshold               |
| **RAG 模式选择** | `GENERIC`（自动注入上下文）/ `KNOWLEDGE_TOOLS`（Agent 主动检索） |
| **多 RAG 后端** | 支持百炼/Dify/Haystack/RAGFlow/自建（`extensions-rag-*`） |
| **权限控制**     | 知识库的用户级别访问控制                                     |

### 4.7 agent-platform-workflow（工作流引擎）

| 功能             | 说明                                         |
| -------------- | ------------------------------------------ |
| **工作流设计器**     | DAG 可视化编排（拖拽节点、连线）                         |
| **节点类型**       | Agent 节点、条件分支、循环、并行（`FanoutPipeline`）、人工审批 |
| **多 Agent 协作** | `MsgHub` 广播通信、`SequentialPipeline` 串行管道    |
| **定时调度**       | Cron 定时触发（`extensions-scheduler`）          |
| **工作流版本**      | 版本管理、启用/禁用                                 |
| **执行日志**       | 每次执行的全链路记录                                 |

### 4.8 agent-platform-observability（可观测性）

| 功能           | 说明                                      |
| ------------ | --------------------------------------- |
| **对话追踪**     | 基于 `Tracer` 的每次对话完整 Trace（模型调用、工具执行、耗时） |
| **Token 用量** | 按 Agent/模型维度统计 `ChatUsage`              |
| **成本核算**     | Token 用量 × 单价 = 成本，按用户出账单               |
| **性能指标**     | 响应延迟 P50/P95/P99、错误率、工具调用成功率            |
| **Hook 审计**  | 通过 `Hook` 机制记录每个 Reasoning/Acting 步骤    |
| **告警规则**     | 延迟/错误率/成本超阈值自动告警                        |
| **仪表盘**      | Grafana 风格的数据可视化大屏                      |

### 4.9 agent-platform-security（安全中心）

| 功能              | 说明                               |
| --------------- | -------------------------------- |
| **认证**          | JWT + OAuth2.0 / OIDC（对接企业 SSO）  |
| **RBAC 权限**     | 角色：管理员 → 开发者 → 运营 → 访客              |
| **用户权限隔离**     | 基于角色的数据访问控制（RBAC）              |
| **API Key 管理**  | Agent 对外发布时生成 API Key，支持吊销和过期    |
| **操作审计**        | 所有写操作记录审计日志                      |
| **数据脱敏**        | 对话记录中的敏感信息自动脱敏                   |
| **Prompt 注入防护** | 输入/输出安全过滤                        |

### 4.10 agent-platform-admin（运维管理）

| 功能       | 说明                               |
| -------- | -------------------------------- |
| **用户管理** | 创建/禁用用户、分配角色                     |
| **用户管理** | 用户 CRUD、角色分配、邀请                  |
| **模型管理** | 全局模型注册（API Key、Endpoint、配额）      |
| **系统配置** | 全局参数（默认 maxIters、Token 上限、存储配置等） |
| **资源监控** | Agent 实例数、活跃会话数、Redis/PG 使用率     |

***

## 五、数据库设计（核心表）

### 用户与权限

```sql
-- 用户表
CREATE TABLE sys_user (
    id              VARCHAR(36) PRIMARY KEY,
    username        VARCHAR(64) NOT NULL UNIQUE,
    email           VARCHAR(128) NOT NULL UNIQUE,
    password_hash   VARCHAR(256) NOT NULL,
    nickname        VARCHAR(64),
    avatar_url      VARCHAR(512),
    role            VARCHAR(32) NOT NULL DEFAULT 'viewer',   -- admin | developer | operator | viewer
    status          VARCHAR(16) NOT NULL DEFAULT 'active',   -- active | disabled | deleted
    last_login_at   TIMESTAMP,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

-- 角色表
CREATE TABLE sys_role (
    id              VARCHAR(36) PRIMARY KEY,
    name            VARCHAR(64) NOT NULL,
    code            VARCHAR(64) NOT NULL,
    description     VARCHAR(256),
    created_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

-- 权限表
CREATE TABLE sys_permission (
    id              VARCHAR(36) PRIMARY KEY,
    code            VARCHAR(128) NOT NULL UNIQUE,            -- agent:read, agent:write, tool:manage 等
    name            VARCHAR(128) NOT NULL,
    module          VARCHAR(64) NOT NULL,                    -- agent, tool, knowledge, workflow 等
    created_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

-- 角色-权限关联表
CREATE TABLE sys_role_permission (
    role_id         VARCHAR(36) NOT NULL REFERENCES sys_role(id),
    permission_id   VARCHAR(36) NOT NULL REFERENCES sys_permission(id),
    PRIMARY KEY (role_id, permission_id)
);
```

### Agent 核心（规范化表设计 — 单一事实源）

```sql
-- Agent 定义主表（标量配置项直接存储，关联配置拆分到子表）
CREATE TABLE agent_definition (
    id                  VARCHAR(36) PRIMARY KEY,
    name                VARCHAR(128) NOT NULL,
    description         VARCHAR(1024),
    sys_prompt          TEXT,                                 -- 系统提示词
    current_version     VARCHAR(32) NOT NULL DEFAULT '0.1.0',
    current_version_id  VARCHAR(36) REFERENCES agent_version(id),  -- 当前发布的版本 ID
    status              VARCHAR(16) NOT NULL DEFAULT 'draft', -- draft | published | archived | deleted
    published_at        TIMESTAMP,                             -- 发布时间
    published_by        VARCHAR(36) REFERENCES sys_user(id),   -- 发布人
    created_by          VARCHAR(36) NOT NULL REFERENCES sys_user(id),
    created_at          TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP NOT NULL DEFAULT NOW(),

    -- ── 迭代与生成选项 ──
    max_iterations      INT NOT NULL DEFAULT 10,

    -- ── HarnessAgent: 工作区 ──
    workspace_mode      VARCHAR(16) NOT NULL DEFAULT 'isolated',  -- isolated | shared | sandbox
    workspace_base_dir  VARCHAR(512) NOT NULL DEFAULT '/data/workspaces',
    max_context_tokens  INT NOT NULL DEFAULT 8000,

    -- ── HarnessAgent: 文件系统 ──
    filesystem_type     VARCHAR(16) NOT NULL DEFAULT 'local',     -- local | remote | sandbox
    enable_filesystem_tools BOOLEAN NOT NULL DEFAULT TRUE,
    enable_shell_tool   BOOLEAN NOT NULL DEFAULT TRUE,
    sandbox_provider    VARCHAR(32),                              -- docker | kubernetes | e2b | daytona
    sandbox_image       VARCHAR(256),
    sandbox_timeout     INT,

    -- ── HarnessAgent: 记忆 ──
    enable_memory_flush       BOOLEAN NOT NULL DEFAULT TRUE,
    enable_memory_maintenance BOOLEAN NOT NULL DEFAULT TRUE,

    -- ── HarnessAgent: 上下文压缩 ──
    enable_compaction         BOOLEAN NOT NULL DEFAULT FALSE,
    compaction_trigger_pct    INT NOT NULL DEFAULT 80,
    compaction_keep_recent    INT NOT NULL DEFAULT 10,

    -- ── HarnessAgent: 工具结果驱逐 ──
    tool_result_eviction_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    tool_result_eviction_max_chars INT NOT NULL DEFAULT 4000,

    -- ── HarnessAgent: SubAgent ──
    enable_subagents      BOOLEAN NOT NULL DEFAULT FALSE,

    -- ── HarnessAgent: 计划 ──
    enable_plan           BOOLEAN NOT NULL DEFAULT FALSE,

    -- ── HarnessAgent: 会话 ──
    enable_session_persistence BOOLEAN NOT NULL DEFAULT TRUE,
    session_backend       VARCHAR(16) NOT NULL DEFAULT 'json'     -- json | redis | mysql
);

-- Agent 模型配置表 (1:1)
-- 阶段一：不需要此表，模型在代码中硬编码
-- 阶段三：引入模型管理，创建此表关联 agent_definition 和 model_provider
CREATE TABLE agent_model_config (
    id              VARCHAR(36) PRIMARY KEY,
    agent_id        VARCHAR(36) NOT NULL UNIQUE REFERENCES agent_definition(id) ON DELETE CASCADE,
    provider_id     VARCHAR(36) REFERENCES model_provider(id),  -- 阶段三前为 NULL
    provider_name   VARCHAR(64) NOT NULL,               -- dashscope | openai | anthropic | ollama | gemini
    model_name      VARCHAR(128) NOT NULL,              -- qwen-plus, gpt-4o, claude-sonnet 等
    temperature     DECIMAL(3,2) NOT NULL DEFAULT 0.7,
    max_tokens      INT NOT NULL DEFAULT 4096,
    top_p           DECIMAL(3,2) NOT NULL DEFAULT 0.9,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Agent-工具引用表 (N:M)
CREATE TABLE agent_tool_ref (
    id              VARCHAR(36) PRIMARY KEY,
    agent_id        VARCHAR(36) NOT NULL REFERENCES agent_definition(id) ON DELETE CASCADE,
    tool_id         VARCHAR(36) NOT NULL REFERENCES tool_definition(id) ON DELETE CASCADE,
    sort_order      INT NOT NULL DEFAULT 0,
    UNIQUE(agent_id, tool_id)
);

-- Agent-知识库引用表 (N:M)
CREATE TABLE agent_knowledge_ref (
    id                  VARCHAR(36) PRIMARY KEY,
    agent_id            VARCHAR(36) NOT NULL REFERENCES agent_definition(id) ON DELETE CASCADE,
    knowledge_base_id   VARCHAR(36) NOT NULL REFERENCES knowledge_base(id) ON DELETE CASCADE,
    rag_mode            VARCHAR(32) NOT NULL DEFAULT 'GENERIC',  -- GENERIC | KNOWLEDGE_TOOLS
    retrieve_limit      INT NOT NULL DEFAULT 5,
    score_threshold     DECIMAL(3,2) NOT NULL DEFAULT 0.5,
    sort_order          INT NOT NULL DEFAULT 0,
    UNIQUE(agent_id, knowledge_base_id)
);

-- Agent SubAgent 声明表 (1:N)
CREATE TABLE agent_subagent_decl (
    id              VARCHAR(36) PRIMARY KEY,
    agent_id        VARCHAR(36) NOT NULL REFERENCES agent_definition(id) ON DELETE CASCADE,
    name            VARCHAR(128) NOT NULL,
    description     VARCHAR(512),
    model_override  VARCHAR(256),                        -- 可选模型覆盖（如 "openai:gpt-4o-mini"）
    workspace_mode  VARCHAR(16) NOT NULL DEFAULT 'ISOLATED', -- SHARED | ISOLATED
    workspace_path  VARCHAR(512),                        -- ISOLATED 模式下的独立工作区路径
    max_iters       INT NOT NULL DEFAULT 10,
    tools_allowlist TEXT,                                -- JSON array，允许的工具列表（空=全部）
    sort_order      INT NOT NULL DEFAULT 0
);

-- Agent 长期记忆配置表 (1:1, 可选)
CREATE TABLE agent_long_term_memory (
    id              VARCHAR(36) PRIMARY KEY,
    agent_id        VARCHAR(36) NOT NULL UNIQUE REFERENCES agent_definition(id) ON DELETE CASCADE,
    enabled         BOOLEAN NOT NULL DEFAULT TRUE,
    mode            VARCHAR(16) NOT NULL DEFAULT 'BOTH',  -- AUTO | AGENT | BOTH
    async_record    BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Agent 上下文文件表 (1:N)
CREATE TABLE agent_context_file (
    id              VARCHAR(36) PRIMARY KEY,
    agent_id        VARCHAR(36) NOT NULL REFERENCES agent_definition(id) ON DELETE CASCADE,
    file_path       VARCHAR(256) NOT NULL,                -- 工作区相对路径（如 "SOUL.md"）
    sort_order      INT NOT NULL DEFAULT 0
);

-- Agent 版本快照表（YAML 仅在此处用于版本快照和导出）
CREATE TABLE agent_version (
    id              VARCHAR(36) PRIMARY KEY,
    agent_id        VARCHAR(36) NOT NULL REFERENCES agent_definition(id),
    version         VARCHAR(32) NOT NULL,
    snapshot_yaml   TEXT NOT NULL,                        -- 完整 YAML 快照（用于回滚和审计）
    changelog       VARCHAR(2048),
    publish_status  VARCHAR(16) NOT NULL DEFAULT 'draft', -- draft | published | rollback
    published_at    TIMESTAMP,
    published_by    VARCHAR(36) REFERENCES sys_user(id),
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE(agent_id, version)
);

-- Agent 运行实例表（可选，用于跟踪活跃 Agent）
CREATE TABLE agent_instance (
    id              VARCHAR(36) PRIMARY KEY,
    agent_id        VARCHAR(36) NOT NULL REFERENCES agent_definition(id),
    session_id      VARCHAR(36) NOT NULL,
    status          VARCHAR(16) NOT NULL DEFAULT 'idle',  -- idle | running | error | stopped
    last_active_at  TIMESTAMP,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW()
);
```

### 工具

```sql
-- 工具定义表
CREATE TABLE tool_definition (
    id              VARCHAR(36) PRIMARY KEY,
    name            VARCHAR(128) NOT NULL,
    display_name    VARCHAR(128) NOT NULL,
    description     VARCHAR(1024),
    type            VARCHAR(32) NOT NULL,                    -- function | http | mcp | subagent
    category        VARCHAR(64),                             -- 分类标签
    schema_json     TEXT,                                     -- 工具 JSON Schema
    config_json     TEXT,                                     -- 工具配置（endpoint、headers 等）
    status          VARCHAR(16) NOT NULL DEFAULT 'active',   -- active | disabled | pending_review
    is_builtin      BOOLEAN NOT NULL DEFAULT FALSE,
    created_by      VARCHAR(36) NOT NULL REFERENCES sys_user(id),
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

-- 工具组表
CREATE TABLE tool_group (
    id              VARCHAR(36) PRIMARY KEY,
    name            VARCHAR(128) NOT NULL,
    description     VARCHAR(512),
    created_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

-- 工具组-工具关联表
CREATE TABLE tool_group_item (
    group_id        VARCHAR(36) NOT NULL REFERENCES tool_group(id),
    tool_id         VARCHAR(36) NOT NULL REFERENCES tool_definition(id),
    sort_order      INT NOT NULL DEFAULT 0,
    PRIMARY KEY (group_id, tool_id)
);

-- MCP Server 配置表
CREATE TABLE mcp_server (
    id              VARCHAR(36) PRIMARY KEY,
    name            VARCHAR(128) NOT NULL,
    url             VARCHAR(512) NOT NULL,
    transport_type  VARCHAR(32) NOT NULL DEFAULT 'sse',      -- sse | stdio | streamable_http
    config_json     TEXT,                                     -- 额外配置
    status          VARCHAR(16) NOT NULL DEFAULT 'active',
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);
```

### 技能（Skill）

```sql
-- Skill 定义表（元数据存储，定义文件存文件系统）
CREATE TABLE skill_definition (
    id              VARCHAR(36) PRIMARY KEY,
    name            VARCHAR(128) NOT NULL,                    -- Skill 唯一标识（同时也是文件系统目录名）
    display_name    VARCHAR(128) NOT NULL,
    description     VARCHAR(1024),
    category        VARCHAR(64),                              -- 分类标签
    version         VARCHAR(32) NOT NULL DEFAULT '0.1.0',
    file_path       VARCHAR(512) NOT NULL,                    -- 定义文件在文件系统中的路径（如 /data/skills/{name}/SKILL.md）
    repository_type VARCHAR(32) NOT NULL DEFAULT 'file',      -- file | git | database
    config_json     TEXT,                                     -- 额外配置（触发条件、工具依赖声明等）
    status          VARCHAR(16) NOT NULL DEFAULT 'active',    -- active | disabled | pending_review
    is_builtin      BOOLEAN NOT NULL DEFAULT FALSE,
    created_by      VARCHAR(36) NOT NULL REFERENCES sys_user(id),
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Skill 版本表（版本历史 + 文件快照）
CREATE TABLE skill_version (
    id              VARCHAR(36) PRIMARY KEY,
    skill_id        VARCHAR(36) NOT NULL REFERENCES skill_definition(id) ON DELETE CASCADE,
    version         VARCHAR(32) NOT NULL,
    file_path       VARCHAR(512) NOT NULL,                    -- 该版本对应的定义文件路径
    changelog       VARCHAR(2048),
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE(skill_id, version)
);

-- Agent-Skill 引用表 (N:M)
CREATE TABLE agent_skill_ref (
    id              VARCHAR(36) PRIMARY KEY,
    agent_id        VARCHAR(36) NOT NULL REFERENCES agent_definition(id) ON DELETE CASCADE,
    skill_id        VARCHAR(36) NOT NULL REFERENCES skill_definition(id) ON DELETE CASCADE,
    sort_order      INT NOT NULL DEFAULT 0,
    UNIQUE(agent_id, skill_id)
);
```

### 知识库

```sql
-- 知识库表
CREATE TABLE knowledge_base (
    id              VARCHAR(36) PRIMARY KEY,
    name            VARCHAR(128) NOT NULL,
    description     VARCHAR(512),
    embedding_model VARCHAR(128) NOT NULL DEFAULT 'text-embedding-v3',
    vector_store_type VARCHAR(32) NOT NULL DEFAULT 'chromadb', -- chromadb | milvus
    rag_mode        VARCHAR(32) NOT NULL DEFAULT 'GENERIC',   -- GENERIC | KNOWLEDGE_TOOLS
    config_json     TEXT,                                     -- 向量库连接配置等
    document_count  INT NOT NULL DEFAULT 0,
    chunk_count     INT NOT NULL DEFAULT 0,
    status          VARCHAR(16) NOT NULL DEFAULT 'active',    -- active | building | error | deleted
    created_by      VARCHAR(36) NOT NULL REFERENCES sys_user(id),
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

-- 知识库文档表
CREATE TABLE knowledge_document (
    id              VARCHAR(36) PRIMARY KEY,
    kb_id           VARCHAR(36) NOT NULL REFERENCES knowledge_base(id),
    title           VARCHAR(256) NOT NULL,
    file_name       VARCHAR(256) NOT NULL,
    file_path       VARCHAR(512) NOT NULL,                    -- 本地文件系统路径
    file_size       BIGINT NOT NULL DEFAULT 0,
    file_type       VARCHAR(16) NOT NULL,                     -- pdf | docx | txt | md
    chunk_count     INT NOT NULL DEFAULT 0,
    parse_status    VARCHAR(16) NOT NULL DEFAULT 'pending',   -- pending | parsing | done | error
    error_message   VARCHAR(1024),
    created_by      VARCHAR(36) NOT NULL REFERENCES sys_user(id),
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

-- 文档分块表
CREATE TABLE knowledge_chunk (
    id              VARCHAR(36) PRIMARY KEY,
    doc_id          VARCHAR(36) NOT NULL REFERENCES knowledge_document(id),
    kb_id           VARCHAR(36) NOT NULL REFERENCES knowledge_base(id),
    content         TEXT NOT NULL,
    content_hash    VARCHAR(64) NOT NULL,
    chunk_index     INT NOT NULL,
    token_count     INT NOT NULL DEFAULT 0,
    embedding_id    VARCHAR(128),                             -- 向量库中的 ID
    metadata_json   TEXT,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW()
);
```

### 对话与会话

> **说明**：chat_session 和 message_record 表在阶段一 POC 时不需要，阶段四迭代 4.2 才实现（内存存储过渡）

```sql
-- 会话表（阶段四实现，阶段一用内存 Map 替代）
CREATE TABLE chat_session (
    id                  VARCHAR(36) PRIMARY KEY,
    agent_id            VARCHAR(36) NOT NULL REFERENCES agent_definition(id),
    agent_version_id    VARCHAR(36) NOT NULL REFERENCES agent_version(id), -- 加载时的 Agent 版本 ID
    user_id             VARCHAR(36) NOT NULL REFERENCES sys_user(id),
    title               VARCHAR(256),
    status              VARCHAR(16) NOT NULL DEFAULT 'active',    -- active | closed | archived
    message_count       INT NOT NULL DEFAULT 0,
    total_tokens        BIGINT NOT NULL DEFAULT 0,
    summary             TEXT,                                       -- 对话摘要（摘要压缩后存储）
    summary_tokens      INT DEFAULT 0,                              -- 摘要 Token 数
    last_summary_at     TIMESTAMP,                                  -- 上次生成摘要时间
    context_token_limit INT DEFAULT 16384,                          -- 上下文 Token 限制
    created_at          TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP NOT NULL DEFAULT NOW()
);

-- 消息记录表（阶段四实现）
CREATE TABLE message_record (
    id              VARCHAR(36) PRIMARY KEY,
    session_id      VARCHAR(36) NOT NULL REFERENCES chat_session(id),
    trace_id        VARCHAR(64),
    role            VARCHAR(16) NOT NULL,                     -- system | user | assistant | tool
    content         TEXT,
    tool_calls_json TEXT,                                     -- 工具调用请求 JSON
    tool_results_json TEXT,                                   -- 工具执行结果 JSON
    thinking_content TEXT,                                    -- 思考过程
    input_tokens    INT NOT NULL DEFAULT 0,
    output_tokens   INT NOT NULL DEFAULT 0,
    latency_ms      BIGINT NOT NULL DEFAULT 0,
    model_name      VARCHAR(128),
    iteration       INT NOT NULL DEFAULT 0,                   -- ReAct 迭代轮次
    status          VARCHAR(16) NOT NULL DEFAULT 'success',   -- success | error | interrupted
    error_message   VARCHAR(1024),
    created_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

-- 存储分层设计 --
-- 热存储（Redis）：活跃会话的最近消息缓存，TTL 7 天
-- 冷存储（PostgreSQL）：完整会话历史和消息记录
-- 归档触发条件：Session 超过 7 天未活动 或 消息数超过 1000 条
-- 大字段优化：超过阈值（如 100KB）的工具结果存文件系统（后期优化阶段实现）

-- 上下文窗口管理：摘要压缩（Summary + Compression）--
-- 触发条件：Session 的消息 Token 数超过 context_token_limit 的 80%
-- 压缩流程：
--   1. 截断旧消息（保留最近 4K tokens）
--   2. LLM 生成摘要（约 200 字）
--   3. 摘要存入 chat_session.summary
--   4. 后续对话传递：System: "以下是之前的对话摘要：{Summary}" + 最近消息
-- Session 与 Agent 版本策略：Agent 需要发布后才能用于对话
-- 1. Agent 有两种状态：draft（草稿）vs published（已发布）
-- 2. 对话加载 Agent 时，读取 agent_definition.current_version_id 指向的 agent_version 快照
-- 3. Session 创建时记录 agent_version_id，用于追溯「该会话使用了哪个版本」

### 工作流

```sql
-- 工作流定义表
CREATE TABLE workflow_definition (
    id              VARCHAR(36) PRIMARY KEY,
    name            VARCHAR(128) NOT NULL,
    description     VARCHAR(512),
    dag_json        TEXT NOT NULL,                            -- DAG 定义 JSON
    version         VARCHAR(32) NOT NULL DEFAULT '0.1.0',
    status          VARCHAR(16) NOT NULL DEFAULT 'draft',     -- draft | active | disabled | archived
    cron_expression VARCHAR(64),                              -- 定时调度表达式
    created_by      VARCHAR(36) NOT NULL REFERENCES sys_user(id),
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

-- 工作流节点表
CREATE TABLE workflow_node (
    id              VARCHAR(36) PRIMARY KEY,
    workflow_id     VARCHAR(36) NOT NULL REFERENCES workflow_definition(id),
    name            VARCHAR(128) NOT NULL,
    type            VARCHAR(32) NOT NULL,                     -- agent | condition | loop | parallel | hitl | http
    config_json     TEXT,                                     -- 节点配置
    agent_id        VARCHAR(36) REFERENCES agent_definition(id),
    position_x      DOUBLE PRECISION NOT NULL DEFAULT 0,
    position_y      DOUBLE PRECISION NOT NULL DEFAULT 0,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

-- 工作流边表
CREATE TABLE workflow_edge (
    id              VARCHAR(36) PRIMARY KEY,
    workflow_id     VARCHAR(36) NOT NULL REFERENCES workflow_definition(id),
    source_node_id  VARCHAR(36) NOT NULL REFERENCES workflow_node(id),
    target_node_id  VARCHAR(36) NOT NULL REFERENCES workflow_node(id),
    condition_expr  VARCHAR(512),                             -- 条件表达式（用于条件分支）
    label           VARCHAR(128),
    created_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

-- 工作流执行记录表
CREATE TABLE workflow_execution (
    id              VARCHAR(36) PRIMARY KEY,
    workflow_id     VARCHAR(36) NOT NULL REFERENCES workflow_definition(id),
    trigger_type    VARCHAR(32) NOT NULL,                     -- manual | scheduled | api
    status          VARCHAR(16) NOT NULL DEFAULT 'running',   -- running | success | failed | cancelled
    input_json      TEXT,
    output_json     TEXT,
    started_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    finished_at     TIMESTAMP,
    duration_ms     BIGINT,
    created_by      VARCHAR(36) REFERENCES sys_user(id)
);
```

### 模型管理

```sql
-- 模型提供商表
CREATE TABLE model_provider (
    id              VARCHAR(36) PRIMARY KEY,
    name            VARCHAR(64) NOT NULL,                     -- dashscope | openai | anthropic | ollama | gemini
    display_name    VARCHAR(128) NOT NULL,
    api_key         VARCHAR(512),                             -- 加密存储
    base_url        VARCHAR(512),
    config_json     TEXT,                                     -- 额外配置（代理等）
    status          VARCHAR(16) NOT NULL DEFAULT 'active',
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

-- 模型配置表
CREATE TABLE model_config (
    id              VARCHAR(36) PRIMARY KEY,
    provider_id     VARCHAR(36) NOT NULL REFERENCES model_provider(id),
    model_name      VARCHAR(128) NOT NULL,                    -- qwen-plus, gpt-4o, claude-sonnet 等
    display_name    VARCHAR(128),
    max_tokens      INT NOT NULL DEFAULT 4096,
    cost_per_1k_input   DECIMAL(10,6) NOT NULL DEFAULT 0,    -- 输入 Token 单价（元/千Token）
    cost_per_1k_output  DECIMAL(10,6) NOT NULL DEFAULT 0,    -- 输出 Token 单价
    is_default      BOOLEAN NOT NULL DEFAULT FALSE,
    status          VARCHAR(16) NOT NULL DEFAULT 'active',
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);
```

### 可观测性

```sql
-- 追踪记录表
CREATE TABLE trace_record (
    id              VARCHAR(36) PRIMARY KEY,
    trace_id        VARCHAR(64) NOT NULL UNIQUE,
    agent_id        VARCHAR(36),
    session_id      VARCHAR(36),
    workflow_exec_id VARCHAR(36),
    total_latency_ms BIGINT NOT NULL DEFAULT 0,
    total_input_tokens  INT NOT NULL DEFAULT 0,
    total_output_tokens INT NOT NULL DEFAULT 0,
    iteration_count INT NOT NULL DEFAULT 0,
    status          VARCHAR(16) NOT NULL DEFAULT 'success',   -- success | error | timeout
    created_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

-- 追踪跨度表（每个 Reasoning/Acting 步骤一条记录）
CREATE TABLE trace_span (
    id              VARCHAR(36) PRIMARY KEY,
    trace_id        VARCHAR(64) NOT NULL,
    parent_span_id  VARCHAR(36),
    span_type       VARCHAR(32) NOT NULL,                     -- reasoning | acting | tool_call | model_call | summary
    tool_name       VARCHAR(128),
    model_name      VARCHAR(128),
    input_tokens    INT NOT NULL DEFAULT 0,
    output_tokens   INT NOT NULL DEFAULT 0,
    latency_ms      BIGINT NOT NULL DEFAULT 0,
    status          VARCHAR(16) NOT NULL DEFAULT 'success',
    detail_json     TEXT,                                     -- 详细信息
    start_time      TIMESTAMP NOT NULL,
    end_time        TIMESTAMP,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Token 用量统计表（按日聚合）
CREATE TABLE token_usage_stat (
    id              VARCHAR(36) PRIMARY KEY,
    agent_id        VARCHAR(36),
    model_name      VARCHAR(128) NOT NULL,
    stat_date       DATE NOT NULL,
    input_tokens    BIGINT NOT NULL DEFAULT 0,
    output_tokens   BIGINT NOT NULL DEFAULT 0,
    total_cost      DECIMAL(12,4) NOT NULL DEFAULT 0,
    call_count      INT NOT NULL DEFAULT 0,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE(agent_id, model_name, stat_date)
);
```

### 运维

```sql
-- API Key 管理表
CREATE TABLE api_key (
    id              VARCHAR(36) PRIMARY KEY,
    name            VARCHAR(128) NOT NULL,
    key_hash        VARCHAR(256) NOT NULL UNIQUE,             -- API Key 的哈希值
    key_prefix      VARCHAR(16) NOT NULL,                     -- Key 前缀（用于展示）
    agent_id        VARCHAR(36) REFERENCES agent_definition(id), -- 绑定的 Agent（可选）
    permissions     VARCHAR(512),                             -- 权限列表 JSON
    expires_at      TIMESTAMP,
    last_used_at    TIMESTAMP,
    status          VARCHAR(16) NOT NULL DEFAULT 'active',    -- active | revoked | expired
    created_by      VARCHAR(36) NOT NULL REFERENCES sys_user(id),
    created_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

-- 审计日志表
CREATE TABLE audit_log (
    id              VARCHAR(36) PRIMARY KEY,
    user_id         VARCHAR(36),
    username        VARCHAR(64),
    action          VARCHAR(64) NOT NULL,                     -- create | update | delete | publish | login 等
    target_type     VARCHAR(64),                              -- agent | tool | knowledge | user 等
    target_id       VARCHAR(36),
    detail_json     TEXT,                                     -- 操作详情
    ip_address      VARCHAR(64),
    user_agent      VARCHAR(256),
    created_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

-- 系统配置表
CREATE TABLE system_config (
    id              VARCHAR(36) PRIMARY KEY,
    config_key      VARCHAR(128) NOT NULL UNIQUE,
    config_value    TEXT,
    config_type     VARCHAR(32) NOT NULL DEFAULT 'string',    -- string | number | boolean | json
    description     VARCHAR(256),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);
```

***

## 六、前端架构（Vue 3）

```
agent-platform-web/
├── src/
│   ├── views/
│   │   ├── login/              ← 登录/注册/SSO
│   │   ├── dashboard/          ← 数据概览大屏
│   │   ├── chat/               ← 对话 Playground（SSE 流式）
│   │   ├── agent/
│   │   │   ├── list/           ← Agent 列表（卡片/表格视图）
│   │   │   ├── designer/       ← Agent 设计器（分步表单：基础信息 → 模型 → 工具 → 知识库 → SubAgent → 高级配置）
│   │   │   ├── debug/          ← 调试面板（逐步 Reasoning 可视化）
│   │   │   └── version/        ← 版本管理
│   │   ├── tool/
│   │   │   ├── list/           ← 工具列表
│   │   │   ├── editor/         ← 工具编辑器（Schema 可视化编辑）
│   │   │   └── marketplace/    ← 工具市场
│   │   ├── knowledge/
│   │   │   ├── list/           ← 知识库列表
│   │   │   ├── document/       ← 文档管理（上传、解析状态）
│   │   │   └── search-test/    ← 检索测试
│   │   ├── workflow/
│   │   │   ├── designer/       ← DAG 可视化设计器（VueFlow/x6）
│   │   │   └── execution/      ← 执行历史
│   │   ├── monitor/
│   │   │   ├── trace/          ← 追踪详情（瀑布图）
│   │   │   ├── metrics/        ← 指标图表（ECharts）
│   │   │   └── alert/          ← 告警管理
│   │   ├── admin/
│   │   │   ├── user/           ← 用户管理
│   │   │   ├── model/          ← 模型管理
│   │   │   └── config/         ← 系统配置
│   │   └── settings/           ← 个人设置
│   ├── components/             ← 通用组件（Markdown 渲染、代码编辑器、图表）
│   ├── stores/                 ← Pinia 状态管理
│   ├── api/                    ← API 封装层（Axios + SSE）
│   ├── router/                 ← 路由配置
│   └── utils/                  ← 工具函数
├── package.json
└── vite.config.ts
```

**前端技术选型：**

| 技术       | 选择                            | 理由             |
| -------- | ----------------------------- | -------------- |
| 框架       | Vue 3 + TypeScript            | 响应式、组合式 API    |
| 构建       | Vite 6                        | 快速开发体验         |
| UI 库     | Element Plus / Ant Design Vue | 企业级组件          |
| 状态管理     | Pinia                         | Vue 官方推荐       |
| HTTP     | Axios + EventSource (SSE)     | 支持流式对话         |
| 图表       | ECharts                       | 监控数据可视化        |
| DAG 编辑器  | VueFlow / AntV X6             | 工作流可视化         |
| 代码编辑器    | Monaco Editor                 | Prompt/JSON 编辑 |
| Markdown | markdown-it + highlight.js    | 对话消息渲染         |

***

## 七、API 设计（核心接口）

### 认证

```
POST   /api/v1/auth/login                   ← 登录（返回 JWT）
POST   /api/v1/auth/refresh                 ← 刷新 Token
POST   /api/v1/auth/logout                  ← 登出
GET    /api/v1/auth/me                      ← 获取当前用户信息
```

### 对话

```
POST   /api/v1/chat/{agentId}/send          ← 同步对话
POST   /api/v1/chat/{agentId}/stream        ← SSE 流式对话
GET    /api/v1/chat/sessions                 ← 会话列表
DELETE /api/v1/chat/sessions/{sessionId}     ← 结束会话
GET    /api/v1/chat/sessions/{sessionId}/messages ← 历史消息
POST   /api/v1/chat/sessions/{sessionId}/interrupt ← 中断对话
```

### Agent 管理

```
GET    /api/v1/agents                       ← Agent 列表（分页）
POST   /api/v1/agents                       ← 创建 Agent
GET    /api/v1/agents/{id}                  ← Agent 详情
PUT    /api/v1/agents/{id}                  ← 更新 Agent
DELETE /api/v1/agents/{id}                  ← 删除 Agent
POST   /api/v1/agents/{id}/publish          ← 发布版本
POST   /api/v1/agents/{id}/rollback/{versionId} ← 回滚版本
POST   /api/v1/agents/{id}/debug            ← 调试模式运行
GET    /api/v1/agents/{id}/versions         ← 版本历史
POST   /api/v1/agents/{id}/clone            ← 克隆 Agent
```

### 工具管理

```
GET    /api/v1/tools                        ← 工具列表
POST   /api/v1/tools                        ← 注册工具
PUT    /api/v1/tools/{id}                   ← 更新工具
DELETE /api/v1/tools/{id}                   ← 删除工具
POST   /api/v1/tools/test                   ← 工具测试调用
GET    /api/v1/tools/categories             ← 工具分类
GET    /api/v1/tools/marketplace            ← 工具市场列表
POST   /api/v1/tools/{id}/install           ← 安装工具
POST   /api/v1/tools/{id}/uninstall         ← 卸载工具
```

### 知识库

```
GET    /api/v1/knowledge-bases              ← 知识库列表
POST   /api/v1/knowledge-bases              ← 创建知识库
PUT    /api/v1/knowledge-bases/{id}         ← 更新知识库
DELETE /api/v1/knowledge-bases/{id}         ← 删除知识库
POST   /api/v1/knowledge-bases/{id}/documents/upload ← 上传文档
GET    /api/v1/knowledge-bases/{id}/documents ← 文档列表
DELETE /api/v1/knowledge-bases/{id}/documents/{docId} ← 删除文档
POST   /api/v1/knowledge-bases/{id}/search  ← 测试检索
```

### 工作流

```
GET    /api/v1/workflows                    ← 工作流列表
POST   /api/v1/workflows                    ← 创建工作流
GET    /api/v1/workflows/{id}               ← 工作流详情
PUT    /api/v1/workflows/{id}               ← 更新工作流
DELETE /api/v1/workflows/{id}               ← 删除工作流
POST   /api/v1/workflows/{id}/execute       ← 执行工作流
GET    /api/v1/workflows/{id}/executions    ← 执行历史
```

### 监控

```
GET    /api/v1/monitor/traces               ← 追踪列表（分页）
GET    /api/v1/monitor/traces/{traceId}     ← 追踪详情（含 spans）
GET    /api/v1/monitor/metrics              ← 聚合指标
GET    /api/v1/monitor/metrics/agents/{id}  ← 单 Agent 指标
GET    /api/v1/monitor/token-usage          ← Token 用量统计
GET    /api/v1/monitor/token-usage/cost     ← 成本统计
POST   /api/v1/monitor/alerts/rules         ← 创建告警规则
GET    /api/v1/monitor/alerts               ← 告警列表
```

### 管理

```
GET    /api/v1/admin/users                  ← 用户列表
POST   /api/v1/admin/users                  ← 创建用户
PUT    /api/v1/admin/users/{id}             ← 更新用户
POST   /api/v1/admin/models/providers       ← 注册模型提供商
GET    /api/v1/admin/models/providers       ← 模型提供商列表
PUT    /api/v1/admin/models/providers/{id}  ← 更新提供商配置
GET    /api/v1/admin/configs                ← 系统配置列表
PUT    /api/v1/admin/configs/{key}          ← 更新配置
```

### Agent API（对外暴露，兼容 OpenAI 格式）

```
POST   /api/v1/agent-api/{apiKey}/v1/chat/completions    ← OpenAI 兼容接口
```

***

## 八、与 AgentScope-Java 的集成映射

这是规划中最关键的部分——平台的每个功能如何映射到 AgentScope-Java 的能力：

```
平台功能                    →  AgentScope-Java API
─────────────────────────────────────────────────────────────────

⭐ Agent 核心引擎           →  HarnessAgent (agentscope-harness)
                              HarnessAgent.builder()
                                .name() / .sysPrompt() / .model()
                                .toolkit() / .maxIters()
                                .workspace() / .filesystem()
                                .compaction() / .toolResultEviction()
                                .subagents() / .session()
                                .build()
                              内部委托 ReActAgent，自动装配全部生产级 Hook

多模型支持                  →  ModelRegistry.resolve("provider:model")
                              DashScopeChatModel / OpenAIChatModel /
                              AnthropicChatModel / OllamaChatModel /
                              GeminiChatModel

工作区管理                  →  WorkspaceManager               ← AGENTS.md / MEMORY.md / KNOWLEDGE.md
                              WorkspaceContextHook            ← 自动注入工作区上下文
                              workspace/user/{uid}/agent/{aid} ← 用户隔离

文件系统                    →  LocalFilesystemSpec            ← 单实例本地模式
                              RemoteFilesystemSpec           ← 多实例共享模式
                              SandboxFilesystemSpec          ← 沙箱隔离模式
                              FilesystemTool / ShellExecuteTool ← 内置文件/Shell 工具

上下文管理                  →  CompactionHook                 ← 上下文自动压缩
                              ToolResultEvictionHook          ← 大结果驱逐到文件
                              recoverFromOverflow()           ← 溢出自动恢复

SubAgent 编排               →  SubagentsHook                  ← SubAgent 生命周期管理
                              DefaultAgentManager             ← SubAgent 工厂与调度
                              AgentSpawnTool                  ← agent_spawn / task 工具
                              SubagentDeclaration             ← 声明式 SubAgent 定义
                              WorkspaceTaskRepository         ← 后台任务持久化

工具注册与管理              →  Toolkit.registerObject()       ← @Tool 注解
                              Toolkit.registerMcpClient()    ← MCP 协议
                              Toolkit.addToolGroup()         ← 工具组
                              MemorySearchTool / MemoryGetTool ← 内置记忆工具
                              SessionSearchTool              ← 内置会话搜索

对话执行                    →  harnessAgent.call(msgs, ctx)   ← 同步（带 RuntimeContext）
                              harnessAgent.stream(msgs, options, ctx) ← SSE 流式
                              agent.interrupt()              ← 中断

记忆管理                    →  InMemoryMemory                 ← 短期（HarnessAgent 默认）
                              MemoryFlushHook                ← 记忆定期刷盘到工作区
                              MemoryMaintenanceHook          ← 记忆整理与维护
                              LongTermMemory                 ← 长期记忆
                              extensions-memory-bailian      ← 百炼记忆
                              extensions-mem0                ← Mem0

会话持久化                  →  SessionPersistenceHook         ← 自动保存/加载
                              WorkspaceSession               ← JSON 文件持久化
                              extensions-session-redis       ← Redis 会话
                              extensions-session-mysql       ← MySQL 会话

RAG 知识库                  →  Knowledge 接口
                              GenericRAGHook
                              KnowledgeRetrievalTools
                              extensions-rag-bailian/dify/haystack/ragflow

工作流编排                  →  SequentialPipeline             ← 串行
                              FanoutPipeline                 ← 并行
                              MsgHub                         ← 多 Agent 通信
                              PlanNotebook                   ← 任务规划

技能管理                    →  SkillBox / AgentSkill
                              FileSystemSkillRepository      ← HarnessAgent 自动加载
                              GitSkillRepository / MySQLSkillRepository
                              SkillHook

可观测性                    →  AgentTraceHook                 ← HarnessAgent 内置追踪
                              Hook 体系 (Pre/Post Reasoning/Acting)
                              ChatUsage (Token 统计)
                              JsonlTraceExporter

状态管理                    →  StateModule / StatePersistence
                              AgentMetaState / ToolkitState

外部集成                    →  extensions-a2a                 ← Agent-to-Agent
                              extensions-nacos               ← 服务发现
                              extensions-rocketmq            ← 消息队列
                              extensions-scheduler           ← 定时调度
                              extensions-agui                ← Agent UI 协议
```

***

## 九、分阶段实施路线图

> **阅读指南：** 每个迭代的目标产出为一个**可演示、可验证的交付物**，周期控制在 **1 周左右**（5 个工作日）。同一大阶段下的迭代用编号串联（如 1.1 → 1.5），前一迭代是后一迭代的前置依赖。

---

### 阶段一：最小可验证 POC（迭代 1.1 ~ 1.5，共 5 周）

**阶段目标：** 跑通「创建最简 Agent → 纯对话」完整链路，验证核心架构可行性

**POC 范围说明：**
- ✅ 只包含最简 Agent（名称 + 描述 + 系统提示词，模型硬编码）
- ✅ 只支持纯对话（无 Tool/Skill/Knowledge/SubAgent）
- ✅ 包含最简前端（Agent 列表 + 配置 + 对话界面）
- ✅ Session 内存存储（服务重启丢失，阶段四实现持久化）
- ❌ 不包含 Tool/Skill/Knowledge/SubAgent 相关表和工厂
- ❌ 不包含 agent_model_config 表（阶段三才需要）
- ❌ 不包含 chat_session/message_record 表（阶段四才需要）
- ❌ 不包含用户认证（阶段二实现）

```
agent-platform-common     ← 基础 DTO、Entity、异常
agent-platform-runtime    ← 核心运行时
   ├── AgentDefinitionService (最简 Agent CRUD)
   ├── AgentLoaderService (数据库 → HarnessAgent 构建)
   ├── ChatService (同步 + SSE 流式，对话上下文内存存储)
   └── 对话上下文管理 (内存 Map)
agent-platform-web        ← 最简前端
   ├── Agent 列表页
   ├── Agent 配置页（名称/描述/Prompt，模型硬编码）
   └── 对话页
```

---

#### 迭代 1.1：项目骨架搭建（~1 周）

**目标：** 项目可编译、可启动，基础设施就绪

**交付物：** `java -jar` 一键启动，`/actuator/health` 返回 200，PostgreSQL/Redis 连通

**详细任务：**

- [ ] 搭建 Maven 多模块项目骨架（parent pom + 9 个子模块，参考第三节模块划分）
- [ ] 实现 agent-platform-common：`ApiResponse<T>`、`PageResult<T>`、`BizException`、`ErrorCode`、`BaseEntity`
- [ ] 实现全局异常处理器 `GlobalExceptionHandler`（`@RestControllerAdvice`）
- [ ] 编写开发环境配置说明（外部 PostgreSQL + Redis + ChromaDB 连接配置，application.yml 中配置连接信息）
- [ ] runtime 模块 Spring Boot 启动类 `AgentVerseApplication` + application.yml 配置
- [ ] MyBatis-Plus 配置（分页插件、自动填充、逻辑删除）
- [ ] WebFlux 基础配置（CORS、请求日志 Filter、TraceId Filter）

**验证标准：**
- `mvn clean package -DskipTests` 编译通过
- `java -jar` 启动后 `/actuator/health` 返回 UP
- 数据库连接和 Redis 连接正常

---

#### 迭代 1.2：最简 Agent 定义与 CRUD（~1 周）

**目标：** 最简 Agent（仅名称 + 描述 + 系统提示词）可通过 API 进行增删改查

**交付物：** 可通过 curl/Postman 完成最简 Agent 的 CRUD 操作

**详细任务：**

- [ ] 设计并执行数据库 schema：`agent_definition` 主表 + `agent_version` 表（仅这两张表，阶段一模型硬编码，不创建 agent_model_config 表）
- [ ] 实现 Entity 层：`AgentDefinition`、`AgentVersion`（MyBatis-Plus `@TableEntity` 注解）
- [ ] 实现 Mapper 层：`AgentDefinitionMapper`、`AgentVersionMapper`（`BaseMapper<T>` 扩展）
- [ ] 实现 DTO 层（`AgentCreateRequest`、`AgentUpdateRequest`、`AgentDetailResponse`、`AgentListResponse`）
- [ ] 实现 `AgentDefinitionService`（CRUD + 事务性创建 Agent 及 version 记录）
- [ ] 实现 Agent 发布 API（`POST /agents/{id}/publish` → 创建 agent_version 快照 + 更新 status=published + 记录 published_at/published_by）
- [ ] 实现 `AgentController`（RESTful API：`GET/POST/PUT/DELETE /api/v1/agents` + 发布 API）
- [ ] 实现分页查询 + 按状态筛选（draft/published/archived）

**验证标准：**
- `POST /api/v1/agents` 创建 Agent
- `GET /api/v1/agents/{id}` 返回 Agent 详情
- `PUT /api/v1/agents/{id}` 更新 Agent 信息
- `DELETE /api/v1/agents/{id}` 删除 Agent
- `GET /api/v1/agents` 分页查询 Agent 列表
- `POST /api/v1/agents/{id}/publish` 发布 Agent → 创建 agent_version 快照 → status 变为 published

---

#### 迭代 1.3：最简 HarnessAgent 引擎构建（~1 周）

**目标：** 从数据库配置构建出可执行的最简 HarnessAgent 实例（仅支持纯对话，模型硬编码）

**交付物：** `AgentLoaderService.loadAgent(agentId)` 返回可用的 HarnessAgent

**详细任务：**

- [ ] 实现 `AgentLoaderService`（从数据库读取配置 → 调用 `HarnessAgent.Builder` 构建）
  - 加载逻辑：读取 agent_definition.current_version_id 指向的 agent_version 快照
  - 只配置：`name()`, `description()`, `sysPrompt()`, `maxIters()`
  - 模型硬编码：阶段一只支持单一模型（如 OpenAI GPT-4o），后续阶段三可配置
  - 不配置：toolkit, skillBox, knowledges, subagents
- [ ] 实现 `ModelFactory`（阶段一返回硬编码的 Model 实例，如 OpenAI Model）
- [ ] 实现 Agent 生命周期管理（创建 → 加载 → 缓存 → 销毁）
- [ ] 实现 HarnessAgent 实例缓存（内存缓存，Key=agent_version_id，TTL 可配置）

**验证标准：**
- `AgentLoaderService.loadAgent(id)` 返回配置完整的 HarnessAgent 实例
- HarnessAgent 可以执行纯对话（无工具调用）

---

#### 迭代 1.4：对话服务实现（~1 周）

**目标：** 可通过 API 与 Agent 进行同步对话和 SSE 流式对话

**交付物：** `POST /api/v1/chat/{agentId}/send` 同步对话、`POST /api/v1/chat/{agentId}/stream` SSE 流式对话均可用

**详细任务：**

- [ ] 实现 `ChatService`（同步对话：`harnessAgent.call(msgs, ctx)`）
- [ ] 实现 `ChatService`（SSE 流式对话：`harnessAgent.stream(msgs, options, ctx)`）
- [ ] 实现 `ChatController`（REST API + SSE endpoint，`text/event-stream`）
- [ ] 实现对话上下文管理（内存 Map 存储当前会话消息，Key=sessionId，服务重启丢失）
- [ ] 实现对话中断（`POST /chat/sessions/{id}/interrupt` → `agent.interrupt()`）
- [ ] 前端：对话页支持 SSE 流式接收（EventSource）

**验证标准：**
- curl 发送同步对话请求，返回 Agent 完整回复
- SSE 流式对话：前端可通过 EventSource 接收逐 token 推送

---

#### 迭代 1.5：最简前端实现（~1 周）

**目标：** 提供浏览器可访问的最简前端，支持 Agent 配置和对话

**交付物：** 浏览器可访问的 Web 界面，包含 Agent 列表、配置、对话三个页面

**详细任务：**

- [ ] 实现 Agent 列表页（表格展示 Agent 名称、描述、状态、创建时间，支持新建/编辑/删除操作）
- [ ] 实现 Agent 配置页（表单：名称、描述、系统提示词，模型硬编码不暴露给用户）
- [ ] 实现对话页（左右布局：左侧会话列表 + 右侧对话界面）
  - 支持新建会话、切换会话、删除会话
  - 支持 SSE 流式接收消息（EventSource）
  - 支持 Markdown 渲染（markdown-it）
  - 支持对话中断按钮
- [ ] 实现基础路由和导航（顶部导航栏：Agent 管理 / 对话）

**验证标准：**
- 浏览器访问前端页面，可看到 Agent 列表
- 点击"新建 Agent"，填写配置后保存成功
- 点击"对话"，选择 Agent 后可进行 SSE 流式对话
- 对话消息正确渲染 Markdown 格式

**POC 完成标志：**
- ✅ 可通过前端创建最简 Agent（仅配置名称/Prompt）
- ✅ 可通过前端与 Agent 进行纯对话
- ✅ 对话支持 SSE 流式输出和 Markdown 渲染
- ✅ 对话历史可通过 API 查询，Redis 和 PG 数据一致
- ✅ 中断正在进行的对话，Agent 停止生成
- ✅ 核心链路验证通过，可以进入阶段二添加更多能力

---

### 阶段二：安全与权限（迭代 2.1 ~ 2.3，共 3 周）

**阶段目标：** 用户认证、RBAC 权限控制、前端登录适配

**新增能力：** 从阶段一的匿名访问 → 带用户身份的多用户平台

```
agent-platform-security
   ├── JWT 认证 + Spring Security
   ├── RBAC 权限模型
   ├── 用户上下文拦截器
   └── 操作审计日志

agent-platform-web 增强
   ├── 登录/注册页面
   ├── 路由守卫 + Token 管理
   └── 用户状态管理
```

---

#### 迭代 2.1：用户体系与 JWT 认证（~1 周）

**目标：** 用户可注册/登录，获取 JWT Token

**交付物：** 完整的认证 API + 前端登录/注册页面

**详细任务：**

- [ ] 设计并执行数据库 schema：`sys_user`、`sys_role`、`sys_permission`、`sys_role_permission` 表
- [ ] 实现 Entity、Mapper、Service 层
- [ ] 实现用户注册 API（密码 BCrypt 加密存储）
- [ ] 实现 JWT 工具类（Token 生成、签名、解析、过期判断）
- [ ] 实现登录 API（校验密码 → 返回 access_token + refresh_token）
- [ ] 实现 Token 刷新 API（refresh_token 换新 access_token）
- [ ] 实现登出 API（Token 加入 Redis 黑名单）
- [ ] 实现 `GET /api/v1/auth/me` 获取当前用户信息
- [ ] 前端：搭建 Vue 3 + Vite 6 + TypeScript + Element Plus 项目骨架
- [ ] 前端：实现登录/注册页面（JWT 认证，Token 存储到 localStorage）
- [ ] 前端：配置 Pinia 状态管理 + Vue Router 路由 + Axios 封装（请求拦截注入 Token）
- [ ] 前端：实现主布局框架（侧边栏导航 + 顶部栏 + 内容区）
- [ ] 前端：实现路由守卫（未登录重定向登录页，Token 过期自动刷新）

**验证标准：**
- 前端：注册 → 登录 → 跳转到主布局页面 → 侧边栏可见
- 后端：携带 Token 访问 `/auth/me` 返回用户信息 → 刷新 Token 成功 → 登出后 Token 失效
- 未登录访问任何页面自动跳转登录页

---

#### 迭代 2.2：RBAC 权限与数据隔离（~1 周）

**目标：** 不同角色只能访问授权范围内的资源

**交付物：** 权限校验注解 `@RequirePermission` 生效 + 前端按角色控制菜单/按钮可见性

**详细任务：**

- [ ] 实现 `JwtAuthFilter`（解析 Token → 校验签名 → 提取用户信息）
- [ ] 实现 `UserContextFilter`（从请求中提取 user_id → 注入 ThreadLocal）
- [ ] 实现 `@RequirePermission` 注解 + AOP 拦截器（基于 `sys_role_permission` 校验）
- [ ] 实现 `RbacPermissionFilter`（WebFilter 层统一鉴权）
- [ ] 实现用户数据隔离（MyBatis-Plus `TenantLineInnerInterceptor` 自动注入 user_id）
- [ ] 初始化 RBAC 数据：4 个预设角色（admin/developer/operator/viewer）+ 各模块权限
- [ ] 为所有已有 Controller 接口添加 `@RequirePermission` 注解
- [ ] 前端：实现用户状态管理（Pinia store：用户信息、权限列表）
- [ ] 前端：侧边栏菜单按角色动态显示/隐藏
- [ ] 前端：按钮级权限控制（v-permission 指令）

**验证标准：**
- viewer 角色无法创建 Agent（返回 403），前端"新建 Agent"按钮不可见
- developer 角色只能操作自己创建的 Agent（数据隔离）
- admin 角色拥有全部权限
- 未携带 Token 的请求返回 401

---

#### 迭代 2.3：审计日志（~1 周）

**目标：** 所有写操作有审计记录，可追溯操作历史

**交付物：** 审计日志切面 + 审计日志查询 API + 前端审计日志页面

**详细任务：**

- [ ] 实现 `audit_log` 表的 Schema、Entity、Mapper
- [ ] 实现审计日志切面（AOP 拦截所有写操作 → 记录用户/操作/目标/详情/IP）
- [ ] 实现审计日志查询 API（`GET /api/v1/admin/audit-logs`，分页 + 时间/操作类型筛选）
- [ ] 实现 `AccessLogFilter`（记录请求方法、路径、耗时、状态码）
- [ ] 前端：实现审计日志页面（表格展示 + 时间范围/操作类型/用户筛选）
- [ ] 将阶段一创建的 Agent 补充 `created_by` 字段，与用户表关联

**验证标准：**
- 创建/修改/删除 Agent 后，`audit_log` 表中有对应记录
- 审计日志页面可按时间、操作类型、用户查询
- 操作详情中可看到变更前后的数据差异

---

### 阶段三：模型管理（迭代 3.1 ~ 3.3，共 3 周）

**阶段目标：** 全局模型提供商可管理，创建 Agent 时可选择不同 Provider/Model，支持成本核算

**新增能力：** 从硬编码单模型 → 多模型动态管理；建立成本核算基础

```
agent-platform-admin (部分)
   ├── 模型提供商管理
   ├── 模型实例管理
   └── 成本核算

agent-platform-runtime 增强
   ├── ModelRegistry 多 Provider 注册
   └── ModelFactory 动态加载
```

---

#### 迭代 3.1：Provider 管理（~1 周）

**目标：** 模型提供商可注册、管理，API Key 安全存储

**交付物：** Provider CRUD API + 前端 Provider 管理页面

**详细任务：**

- [ ] 设计并执行 `model_provider` 表 Schema、Entity、Mapper、Service
- [ ] 实现 Provider CRUD API（`GET/POST/PUT/DELETE /api/v1/model-providers`）
- [ ] 实现 API Key 加密存储（数据库中存储加密后的密钥）
- [ ] 实现 Provider 连接测试 API（验证 API Key 有效性）
- [ ] 前端：实现 Provider 列表页（Provider 卡片 + 状态指示）
- [ ] 前端：实现 Provider 创建/编辑弹窗（名称、API Key、Base URL 配置）

**验证标准：**
- 注册 DashScope Provider（填入 API Key，保存后加密存储）
- 注册 OpenAI Provider（填入 API Key 和 Base URL）
- 连接测试：填写正确 Key → 显示"连接成功"；填写错误 Key → 显示"连接失败"

---

#### 迭代 3.2：模型配置管理（~1 周）

**目标：** 模型实例可配置，支持多模型注册

**交付物：** 模型实例 CRUD API + 前端模型列表页面

**详细任务：**

- [ ] 设计并执行 `model_config` 表 Schema、Entity、Mapper、Service
- [ ] 实现模型实例 CRUD API（关联到 Provider，支持 cost_per_1k_input/output）
- [ ] 实现 `ModelRegistry` 多 Provider 注册与解析（`ModelRegistry.resolve("provider:model")`）
- [ ] 实现 `ModelFactory` 从 `model_provider` + `model_config` 表动态加载模型
- [ ] 前端：实现模型列表页（按 Provider 分组，展示模型名、最大 Token、成本）
- [ ] 前端：实现模型创建/编辑弹窗（选择 Provider、填写模型名、配置成本）

**验证标准：**
- 在 DashScope Provider 下添加 qwen-plus、qwen-turbo 等模型
- 在 OpenAI Provider 下添加 gpt-4o、gpt-4o-mini 等模型
- 模型列表按 Provider 分组展示

---

#### 迭代 3.3：模型选择与成本核算（~1 周）

**目标：** Agent 可选择 Provider/Model，支持成本统计

**交付物：** Agent 模型选择界面 + 成本统计概览

**详细任务：**

- [ ] 扩展 `agent_model_config` 表（关联 model_config.id）
- [ ] Agent 创建/编辑时支持选择 Provider 和 Model（替代硬编码）
- [ ] 实现成本计算逻辑（Token 用量 × cost_per_1k_input/output）
- [ ] 前端：Agent 配置页模型选择器升级（Provider 分组下拉框）
- [ ] 前端：实现模型统计概览页面（各模型调用次数、总 Token 消耗、总成本）
- [ ] 前端：Agent 创建页默认选择模型（从 model_config.is_default 读取）

**验证标准：**
- 创建 Agent 时可从下拉框选择 Provider，再选择该 Provider 下的 Model
- 模型统计页面显示：模型名、调用次数、输入 Token、输出 Token、总成本
- 禁用某个 Provider 后，关联该 Provider 的 Agent 对话报错提示

---

### 阶段四：生产运行时（迭代 4.1 ~ 4.4，共 4 周）

**阶段目标：** Agent 引擎从 POC 升级为生产级 — Workspace 隔离、记忆管理、会话增强、循环增强

**新增能力：** 从无状态对话 → 带记忆的持久化对话；从简单 ReAct 循环 → 具备上下文压缩、工具结果驱逐、长期记忆、计划执行的生产级循环

```
agent-platform-runtime 增强
   ├── WorkspaceManager (用户工作区隔离)
   ├── FilesystemFactory (文件系统配置)
   ├── MemoryFlushHook + MemoryMaintenanceHook
   ├── CompactionHook (上下文压缩)
   ├── ToolResultEvictionHook (工具结果驱逐)
   ├── LongTermMemory (长期记忆)
   ├── PlanNotebook (任务规划)
   ├── SessionPersistenceHook → extensions-session-redis
   ├── Token 用量记录 Hook
   └── 用户上下文 Hook
```

---

#### 迭代 4.1：Workspace + 记忆管理（~1 周）

**目标：** Agent 拥有独立工作区，具备记忆刷盘和维护能力

**交付物：** Workspace 隔离验证通过 + Memory Hook 生效

**详细任务：**

- [ ] 实现 `WorkspaceManager` 用户隔离（`workspace/user/{userId}/agent/{agentId}` 目录结构）
- [ ] 实现 `FilesystemFactory`（根据 `agent_definition.filesystem_type` 选择 local 模式）
- [ ] 集成 `MemoryFlushHook`（对话结束时自动将记忆刷盘到工作区 MEMORY.md）
- [ ] 集成 `MemoryMaintenanceHook`（定期整理记忆：去重、归档、摘要）
- [ ] 实现用户上下文 Hook（注入 user_id 到 HarnessAgent `RuntimeContext`）
- [ ] 扩展 `AgentLoaderService`：添加 workspace + filesystem + memory hook 配置
- [ ] 前端：Agent 配置页增加"高级配置"区域（工作区模式、文件系统、记忆开关）

**验证标准：**
- 不同用户/Agent 的工作区目录互相隔离
- 多轮对话后，工作区 MEMORY.md 中有记忆内容
- 服务重启后，Agent 可加载之前的记忆继续对话

---

#### 迭代 4.2：会话增强与 Token 计量（~1 周）

**目标：** 会话持久化切换到 Redis，Token 用量自动记录

**交付物：** Redis 会话持久化验证通过 + Token 用量记录

**详细任务：**

- [ ] 集成 `extensions-session-redis`（SessionPersistenceHook 切换到 Redis 后端）
- [ ] 实现 Token 用量记录 Hook（通过 `ChatUsage` 统计每次对话的 input/output tokens → 写入 `message_record`）
- [ ] 实现 `agent_instance` 表 + 活跃 Agent 实例追踪
- [ ] 前端：对话页面增加 Token 消耗实时统计（当前会话累计 input/output tokens）
- [ ] 前端：仪表盘首页（Agent 数量、活跃会话数、今日对话量、Token 用量概览卡片）
- [ ] 实现仪表盘统计 API（后端聚合查询：Agent 数、会话数、Token 用量按日统计）

**验证标准：**
- 对话会话存储在 Redis 中 → 服务重启后会话不丢失，历史消息可恢复
- 每次对话后 `message_record` 表正确记录 `input_tokens` 和 `output_tokens`
- 仪表盘数据与实际一致

---

#### 迭代 4.3：智能体循环增强（~1 周）

**目标：** 启用 HarnessAgent 的生产级循环增强能力，提升长对话、大工具结果、复杂任务的稳定性

**交付物：** 上下文压缩、工具结果驱逐、长期记忆、计划执行功能可用

**详细任务：**

- [ ] 实现 `agent_long_term_memory` 表的 Schema、Entity、Mapper
- [ ] 集成 `CompactionHook`（上下文自动压缩：配置 `compaction_trigger_pct`、`compaction_keep_recent`）
- [ ] 集成 `ToolResultEvictionHook`（工具结果过大时驱逐到文件：配置 `tool_result_eviction_max_chars`）
- [ ] 集成 `recoverFromOverflow()`（上下文溢出自动恢复机制）
- [ ] 集成 `LongTermMemory`（跨会话长期记忆：配置 `agent_long_term_memory` 表）
- [ ] 集成 `PlanNotebook`（任务规划能力：配置 `enable_plan` 字段）
- [ ] 扩展 `AgentLoaderService`：添加 `.compaction()`、`.toolResultEviction()`、`.longTermMemory()`、`.planNotebook()` 配置
- [ ] 前端：Agent 设计器增加"循环增强"配置区域（上下文压缩开关/阈值、工具结果驱逐阈值、长期记忆开关、计划执行开关）

**验证标准：**
- 长对话场景（50+ 轮）：CompactionHook 自动压缩上下文，不出现 Token 溢出
- 大工具结果场景（单工具返回 >10KB）：ToolResultEvictionHook 自动驱逐到文件，Agent 可继续执行
- 跨会话记忆：Agent A 在话 1 中记住用户偏好 → 会话 2 中仍可访问该记忆
- 任务规划：Agent 启用 `enable_plan` 后，可拆解复杂任务并逐步执行

---

#### 迭代 4.4：Agent 生命周期完善（~1 周）

**目标：** AgentLoaderService 对接模型管理，生命周期状态机完善

**交付物：** Agent 完整生命周期 + 对接模型管理基础设施

**详细任务：**

- [ ] 重构 `AgentLoaderService`：从硬编码模型 → 从 `agent_model_config` 表读取模型配置
- [ ] 实现 Agent 生命周期状态机（draft → active → archived）
- [ ] 实现 `agent_definition` 表的 status 字段管理
- [ ] Agent 创建时默认状态为 draft，发布后变为 active
- [ ] 前端：Agent 列表页增加状态筛选（draft / active / archived）
- [ ] 前端：Agent 详情页增加生命周期状态展示

**验证标准：**
- 新创建的 Agent 状态为 draft，不会被其他用户看到
- Agent 发布后状态变为 active，对话时使用该配置
- Agent 归档后不再出现在默认列表中

---

### 阶段五：工具能力（迭代 5.1 ~ 5.3，共 3 周）

**阶段目标：** Agent 可挂载工具，从纯对话 → 可调用外部工具的智能体

**新增能力：** 工具注册/管理 + MCP 协议接入 + Agent 运行时工具调用

```
agent-platform-tool
   ├── 工具 CRUD + 注册
   ├── MCP Server 接入
   ├── ToolGroup 管理
   └── 工具在线测试

agent-platform-runtime 增强
   ├── ToolkitFactory (agent_tool_ref → Toolkit)
   └── agent_tool_ref 表

数据库新增表
   ├── tool_definition
   ├── tool_group + tool_group_item
   ├── mcp_server
   └── agent_tool_ref
```

---

#### 迭代 4.1：工具 CRUD + 注册（~1 周）

**目标：** 工具可通过 API 和前端注册、管理、测试

**交付物：** 工具管理 CRUD API + 前端工具管理页面

**详细任务：**

- [ ] 实现 `tool_definition` 表 + `tool_group` 表 + `tool_group_item` 表的 Schema、Entity、Mapper、Service
- [ ] 实现工具 CRUD API（`GET/POST/PUT/DELETE /api/v1/tools`）
- [ ] 实现工具测试 API（`POST /api/v1/tools/test`，传入参数 → 调用 → 返回结果）
- [ ] 实现工具组管理 API（创建/编辑/删除工具组 + 添加/移除工具）
- [ ] 前端：实现工具列表页（表格视图、按类型/分类筛选、搜索）
- [ ] 前端：实现工具注册/编辑页（基础信息 + JSON Schema 可视化编辑 + 配置参数）
- [ ] 前端：实现工具在线测试（输入参数 → 调用 → 展示返回结果）
- [ ] 前端：实现工具组管理（创建工具组 → 添加/移除工具）

**验证标准：**
- 注册一个 HTTP 工具 → 在线测试调用成功
- 工具列表按分类筛选正常
- 工具组创建后可添加/移除工具

---

#### 迭代 4.2：MCP 接入 + ToolkitFactory（~1 周）

**目标：** 可通过 MCP 协议接入外部工具服务器，Agent 运行时可加载工具

**交付物：** MCP Server 管理 + Agent 运行时工具加载

**详细任务：**

- [ ] 实现 `mcp_server` 表的 Schema、Entity、Mapper、Service
- [ ] 实现 MCP Server 管理 API（CRUD + 连接测试）
- [ ] 集成 `McpClientBuilder`（根据 `mcp_server` 配置创建 MCP Client → 注册工具到 Toolkit）
- [ ] 实现 `agent_tool_ref` 表（Agent-工具关联）的 Schema、Entity、Mapper
- [ ] 实现 `ToolkitFactory`（从 `agent_tool_ref` 表 + `tool_definition` 表 → 构建 Toolkit → 注册到 HarnessAgent）
- [ ] 扩展 `AgentLoaderService`：添加 `.toolkit(toolkitFactory.createFromRefs(toolRefs))` 配置
- [ ] 前端：实现 MCP Server 管理页面（Server 列表 + 添加/编辑 + 连接状态指示）

**验证标准：**
- 添加 MCP Server → 连接成功 → Server 提供的工具出现在工具列表
- 创建 Agent 并挂载工具 → Agent 对话时可调用工具
- Agent 调用工具 → 工具执行 → 结果返回给 Agent → Agent 基于结果继续回复

---

#### 迭代 4.3：Agent 挂载工具 + 前端集成（~1 周）

**目标：** Agent 设计器中可选择/移除工具，对话中可查看工具调用过程

**交付物：** Agent 设计器工具选择面板 + 对话中工具调用可视化

**详细任务：**

- [ ] Agent CRUD 增强：创建/更新 Agent 时支持同时管理 `agent_tool_ref` 关联
- [ ] Agent 详情 API 增强：返回关联的工具列表（JOIN `tool_definition`）
- [ ] 前端：Agent 设计器增加"工具选择"步骤（工具列表勾选 + 拖拽排序）
- [ ] 前端：对话消息中增加工具调用可视化（展示 tool_calls 和 tool_results 的折叠面板）
- [ ] 前端：调试面板显示工具调用的参数、返回值、耗时
- [ ] 实现工具使用统计（调用次数、成功率、平均延迟 → 工具列表展示）

**验证标准：**
- Agent 设计器中勾选工具 → 保存 → Agent 对话时可调用所选工具
- 对话中工具调用过程可视化展示（折叠面板）
- 调试面板可看到每步工具调用的详细信息

---

### 阶段六：技能 Skill（迭代 6.1 ~ 6.2，共 2 周）

**阶段目标：** Agent 可挂载 Skill（比 Tool 更高阶的可插拔能力单元）

**新增能力：** 从单一工具调用 → 完整能力方案（Prompt 模板 + 工具组合 + 执行生命周期）

```
agent-platform-skill
   ├── Skill 元数据 CRUD (数据库)
   ├── Skill 定义文件管理 (文件系统)
   ├── Skill 解析 (MarkdownSkillParser)
   └── Skill 版本管理

agent-platform-runtime 增强
   └── SkillBoxFactory (agent_skill_ref → SkillBox)

数据库新增表
   ├── skill_definition
   ├── skill_version
   └── agent_skill_ref
```

---

#### 迭代 6.1：Skill CRUD（~1 周）

**目标：** Skill 元数据可通过 API 管理，定义文件存储在文件系统

**交付物：** Skill CRUD API + 前端 Skill 管理页面

**详细任务：**

- [ ] 实现 `skill_definition` 表 + `skill_version` 表的 Schema、Entity、Mapper、Service
- [ ] 实现 Skill CRUD API（`GET/POST/PUT/DELETE /api/v1/skills`）
- [ ] 实现 Skill 文件管理（创建/编辑 Skill 时 → 元数据写入数据库 + 定义文件写入 `/data/skills/{name}/SKILL.md`）
- [ ] 实现 `MarkdownSkillParser`（解析 Skill 定义文件 → 提取 Prompt、工具声明等）
- [ ] 实现 Skill 版本管理（发布新版本 → `skill_version` 表记录历史 + 文件快照）
- [ ] 前端：实现 Skill 管理页面（Skill 列表 + 创建/编辑 + Markdown 定义文件编辑器）
- [ ] 前端：实现 Skill 版本历史页面（版本列表 + 回滚操作）

**验证标准：**
- 创建 Skill → 数据库有元数据 + 文件系统有 SKILL.md 文件
- 编辑 Skill → 数据库和文件系统同步更新
- 发布新版本 → skill_version 表新增记录 → 可回滚到历史版本

---

#### 迭代 6.2：SkillBoxFactory + Agent 挂载 Skill（~1 周）

**目标：** Agent 可挂载 Skill，运行时 Skill 自动激活

**交付物：** SkillBoxFactory + Agent 挂载 Skill 功能 + 前端集成

**详细任务：**

- [ ] 实现 `agent_skill_ref` 表的 Schema、Entity、Mapper
- [ ] 实现 `SkillBoxFactory`（从 `agent_skill_ref` 表读取引用 → 从文件系统加载定义文件 → 构建 SkillBox）
- [ ] 扩展 `AgentLoaderService`：添加 `.skillBox(skillBoxFactory.createFromRefs(skillRefs))` 配置
- [ ] Agent CRUD 增强：创建/更新 Agent 时支持管理 `agent_skill_ref` 关联
- [ ] 前端：Agent 设计器增加"技能选择"步骤（Skill 列表勾选）
- [ ] 前端：对话中 Skill 激活状态可视化（展示当前激活的 Skill 列表）

**验证标准：**
- Agent 挂载 Skill → 对话时 Skill 自动激活（SkillHook 生效）
- Skill 中包含的工具自动注册到 Agent 的 Toolkit
- 前端可查看 Agent 关联的 Skill 列表

---

### 阶段七：知识库 RAG（迭代 7.1 ~ 7.3，共 3 周）

**阶段目标：** Agent 可挂载知识库，对话时自动注入检索上下文（RAG）

**新增能力：** 从通用对话 → 基于私有知识的精准问答

```
agent-platform-knowledge
   ├── 知识库 CRUD
   ├── 文档上传/解析/分块
   ├── Embedding + ChromaDB 向量存储
   ├── RAG 集成 (GenericRAGHook)
   └── 检索效果测试

数据库新增表
   ├── knowledge_base
   ├── knowledge_document
   ├── knowledge_chunk
   └── agent_knowledge_ref
```

---

#### 迭代 7.1：知识库 CRUD + 文档上传（~1 周）

**目标：** 知识库可创建，文档可上传到本地文件系统

**交付物：** 知识库管理页面 + 文档上传功能

**详细任务：**

- [ ] 实现 `knowledge_base` 表 + `knowledge_document` 表的 Schema、Entity、Mapper、Service
- [ ] 实现知识库 CRUD API（`GET/POST/PUT/DELETE /api/v1/knowledge-bases`）
- [ ] 实现本地文件存储 Service（文件上传、目录管理、路径映射：`/data/knowledge/{kbId}/{docId}/`）
- [ ] 实现文档上传 API（`POST /knowledge-bases/{id}/documents/upload`，支持 PDF/Word/TXT/Markdown）
- [ ] 实现文档列表/删除 API
- [ ] 前端：实现知识库管理页面（知识库列表 + 创建/编辑弹窗）
- [ ] 前端：实现文档管理页面（文档列表 + 上传组件 + 解析状态展示）

**验证标准：**
- 创建知识库 → 上传 PDF/Word/TXT 文档 → 文件存入本地文件系统 → 文档列表展示上传状态

---

#### 迭代 7.2：文档解析、分块与向量化（~1 周）

**目标：** 上传的文档自动解析、分块、Embedding 并存入 ChromaDB

**交付物：** 文档上传后自动完成解析→分块→向量化，`knowledge_chunk` 表有数据

**详细任务：**

- [ ] 实现 `knowledge_chunk` 表的 Schema、Entity、Mapper
- [ ] 实现文档解析器（Apache Tika 统一解析 PDF/Word/TXT/Markdown → 纯文本）
- [ ] 实现文本分块器（按 token 数分块 + 重叠窗口，可配置 chunk_size 和 overlap）
- [ ] 集成 ChromaDB（HTTP Client 连接 → 创建 Collection → 向量写入与检索）
- [ ] 实现 Embedding Service（调用 Embedding 模型生成向量 → 写入 ChromaDB Collection）
- [ ] 实现异步文档处理流水线（上传 → 解析 → 分块 → Embedding → 更新状态，使用 `@Async` + `CompletableFuture`）
- [ ] 前端：文档处理进度展示（轮询 `parse_status`：pending → parsing → done/error）

**验证标准：**
- 上传 PDF → 状态变为 parsing → 解析完成后 knowledge_chunk 表有分块数据 → 状态变为 done
- 上传多个文档可并行处理
- 解析失败的文档状态变为 error，错误信息可查看

---

#### 迭代 7.3：RAG 集成 + Agent 挂载知识库（~1 周）

**目标：** Agent 可挂载知识库，对话时自动注入检索上下文

**交付物：** RAG 对话可用 + 检索测试 Playground + 前端知识库选择面板

**详细任务：**

- [ ] 实现 `agent_knowledge_ref` 表（Agent-知识库关联）的 Schema、Entity、Mapper
- [ ] 集成 AgentScope RAG（`GenericRAGHook` 自动注入检索上下文到 System Prompt）
- [ ] 实现 `KnowledgeRetrievalTools`（Agent 可主动调用知识库检索工具，`KNOWLEDGE_TOOLS` 模式）
- [ ] Agent CRUD 增强：创建/更新 Agent 时支持管理 `agent_knowledge_ref` 关联
- [ ] 扩展 `AgentLoaderService`：添加 `.knowledges(knowledgeFactory.createFromRefs(kbRefs))` 配置
- [ ] 前端：Agent 设计器增加"知识库选择"步骤（知识库列表勾选 + 检索参数配置）
- [ ] 前端：实现检索测试 Playground（输入查询 → 展示 Top-K 结果 + 相似度分数 + 参数调优）
- [ ] 实现知识库权限控制（用户只能访问自己创建的知识库）

**验证标准：**
- Agent 挂载知识库 → 发送相关问题 → Agent 回答引用了知识库内容
- 检索 Playground：查询返回相关文档块，分数排序正确
- 调整 score_threshold 可过滤低相关度结果

---

### 阶段八：版本管理 + SubAgent（迭代 8.1 ~ 8.3，共 3 周）

**阶段目标：** Agent 具备版本管理能力，支持 SubAgent 编排和调试

**新增能力：** 从草稿态 Agent → 可发布、可回滚、可灰度的生产级 Agent + 多 Agent 协作

```
agent-platform-studio
   ├── Agent 发布/版本/回滚
   ├── 在线调试面板
   └── 灰度发布

agent-platform-runtime 增强
   ├── SubagentFactory (agent_subagent_decl → SubAgent)
   ├── SubagentsHook + AgentSpawnTool
   └── 版本快照与恢复

数据库新增表
   ├── agent_version
   ├── agent_subagent_decl
   └── api_key
```

---

#### 迭代 8.1：Agent 发布/版本/回滚（~1 周）

**目标：** Agent 可发布版本快照，支持回滚

**交付物：** 版本管理 API + 前端版本管理页面

**详细任务：**

- [ ] 实现 `agent_version` 表的 Schema、Entity、Mapper、Service
- [ ] 实现 Agent 发布 API（`POST /agents/{id}/publish` → 序列化完整配置为 YAML 快照 → 写入 `agent_version` 表）
- [ ] 实现 Agent 版本回滚 API（`POST /agents/{id}/rollback/{versionId}` → 从快照恢复配置到各子表）
- [ ] 实现 Agent 克隆 API（`POST /agents/{id}/clone` → 复制 Agent 及所有子表数据）
- [ ] 实现 Agent YAML 导入/导出 API
- [ ] 前端：Agent 版本历史页面（版本列表 + 发布时间 + 变更说明）
- [ ] 前端：版本对比功能（Monaco Editor Diff 模式，左右对比 YAML 快照差异）
- [ ] 前端：Agent 导入/导出功能（上传 YAML / 下载 YAML）

**验证标准：**
- 发布 Agent → 版本历史中出现新版本 → 修改 Agent → 回滚到旧版本 → 配置恢复
- 克隆 Agent → 新 Agent 包含所有子表数据
- 导出的 YAML 可重新导入创建 Agent

---

#### 迭代 8.2：SubAgent 声明与编排（~1 周）

**目标：** Agent 可声明 SubAgent，运行时 SubAgent 自动编排

**交付物：** SubAgent 编排功能可用 + 前端 SubAgent 配置面板

**详细任务：**

- [ ] 实现 `agent_subagent_decl` 表的 Schema、Entity、Mapper
- [ ] 实现 `SubagentFactory`（从 `agent_subagent_decl` 表创建 SubAgent 声明）
- [ ] 集成 `SubagentsHook`（SubAgent 生命周期管理 + 注册到 HarnessAgent）
- [ ] 集成 `AgentSpawnTool`（SubAgent 异步后台执行 → `WorkspaceTaskRepository` 持久化）
- [ ] 扩展 `AgentLoaderService`：添加 `.subagents(subagentFactory.createFromDeclarations(subagents))` 配置
- [ ] Agent CRUD 增强：创建/更新 Agent 时支持管理 `agent_subagent_decl` 子表
- [ ] 前端：Agent 设计器增加"SubAgent 配置"步骤（声明 SubAgent → 指定描述 → 选择模型 → 配置工具白名单）

**验证标准：**
- 父 Agent 声明 SubAgent → 对话时通过 `agent_spawn` 工具委派任务
- SubAgent 独立执行任务 → 结果返回给父 Agent → 父 Agent 基于结果继续对话
- SubAgent 的工作区与父 Agent 隔离

---

#### 迭代 8.3：调试面板 + 灰度发布（~1 周）

**目标：** 可视化调试 Agent 执行过程，支持版本灰度发布

**交付物：** 调试面板 + API Key 管理 + 灰度发布

**详细任务：**

- [ ] 实现调试模式 API（在对话请求中标记 `debug=true`，返回完整的 ReAct 中间步骤数据）
- [ ] 前端：实现调试面板页面（左右分栏：左侧对话 + 右侧调试信息）
- [ ] 前端：ReAct 迭代步骤可视化（每轮：Thinking → Tool Call → Observation）
- [ ] 前端：工具调用详情展示（工具名称、参数 JSON、返回结果、耗时）
- [ ] 前端：模型调用详情展示（完整 Prompt、模型返回、Token 消耗、延迟）
- [ ] 实现 `api_key` 表的 Schema、Entity、Mapper
- [ ] 实现 API Key 管理（生成/校验/吊销 + 绑定 Agent）
- [ ] 实现 Agent 灰度发布（按流量比例分流：如 v1.2 承载 80%，v1.3 承载 20%）
- [ ] 前端：API Key 管理页面（Key 列表 + 生成 + 吊销）

**验证标准：**
- 开启调试模式对话 → 右侧面板实时展示每一步的 Thinking/Acting/Observation
- 生成 API Key → 使用该 Key 调用 Agent API → 吊销后失效
- 灰度发布：配置 50/50 分流 → 请求约各半分配到两个版本

---

### 阶段九：工作流引擎（迭代 9.1 ~ 9.3，共 3 周）

**阶段目标：** 可视化 DAG 工作流编排，支持多 Agent 协作

**新增能力：** 从单 Agent 对话 → 多 Agent 协作工作流

```
agent-platform-workflow
   ├── 工作流 CRUD
   ├── DAG 引擎 (SequentialPipeline / FanoutPipeline)
   ├── 前端 VueFlow 设计器
   ├── MsgHub 多 Agent 通信
   ├── 定时调度 (extensions-scheduler)
   └── 执行记录与回放

数据库新增表
   ├── workflow_definition
   ├── workflow_node + workflow_edge
   └── workflow_execution
```

---

#### 迭代 9.1：工作流后端引擎（~1 周）

**目标：** 工作流可通过 API 定义，DAG 引擎可执行串行/条件流程

**交付物：** 工作流 CRUD API + DAG 引擎

**详细任务：**

- [ ] 实现 `workflow_definition` + `workflow_node` + `workflow_edge` 表的 Schema、Entity、Mapper
- [ ] 实现工作流定义 CRUD API（`GET/POST/PUT/DELETE /api/v1/workflows`）
- [ ] 实现 DAG 工作流引擎（基于 AgentScope `SequentialPipeline` 执行串行节点）
- [ ] 实现 Agent 节点类型（节点执行时加载 Agent → 调用 `harnessAgent.call()` → 输出传递给下一节点）
- [ ] 实现条件分支节点（`condition_expr` 表达式求值 → 选择不同分支）
- [ ] 实现工作流执行 API（`POST /workflows/{id}/execute`，手动触发执行）

**验证标准：**
- 创建包含 2 个 Agent 节点的工作流 → 手动触发 → 两个 Agent 依次执行 → 返回最终结果
- 条件分支：根据输入数据走不同分支

---

#### 迭代 9.2：前端 DAG 设计器 + 多 Agent 协作（~1 周）

**目标：** 可视化拖拽编排工作流 + 支持并行和多 Agent 通信

**交付物：** VueFlow DAG 设计器 + FanoutPipeline + MsgHub

**详细任务：**

- [ ] 前端：实现 DAG 设计器（VueFlow 画布 + 节点拖拽 + 连线）
- [ ] 前端：实现节点类型面板（Agent 节点、条件分支、并行、人工审批 → 拖拽到画布）
- [ ] 前端：实现节点配置面板（点击节点 → 右侧弹出配置表单）
- [ ] 前端：实现画布操作（缩放、平移、自动布局、撤销/重做）
- [ ] 实现并行节点（AgentScope `FanoutPipeline` → 多节点并行执行 → 结果汇聚）
- [ ] 实现 MsgHub 多 Agent 通信（广播消息 → 多个 Agent 接收并各自处理）
- [ ] 实现人工审批节点（工作流暂停 → 等待用户审批 → 继续/拒绝）

**验证标准：**
- 拖拽创建 DAG → 连线 → 配置节点 → 保存 → 重新打开正确回显
- 并行节点：3 个 Agent 同时执行 → 总耗时 ≈ 最慢节点耗时
- 触发执行后，各节点按 DAG 拓扑顺序执行

---

#### 迭代 9.3：工作流调度 + 执行回放（~1 周）

**目标：** 工作流可定时调度，执行历史可查看和回放

**交付物：** Cron 定时调度 + 执行历史页面 + 工作流版本管理

**详细任务：**

- [ ] 实现 `workflow_execution` 表的 Schema、Entity、Mapper
- [ ] 集成 `extensions-scheduler`（Cron 表达式 → 定时触发工作流执行）
- [ ] 实现工作流执行记录 API（`GET /workflows/{id}/executions`，分页 + 状态筛选）
- [ ] 实现执行详情 API（输入数据 → 每个节点的执行状态/耗时/输出 → 最终结果）
- [ ] 实现工作流版本管理（版本号 + 启用/禁用 + 草稿/活跃/归档状态）
- [ ] 前端：实现工作流列表页（工作流列表 + 状态管理 + 手动触发执行）
- [ ] 前端：实现执行历史页面（执行列表 + 详情 + 状态标签：running/success/failed）
- [ ] 前端：实现执行回放（按时间线展示各节点的执行顺序和数据流转）

**验证标准：**
- 配置 Cron `*/5 * * * *` → 每 5 分钟自动执行 → 执行历史中出现记录
- 执行详情：每个节点的状态、耗时、输入输出可查看
- 失败的工作流可查看详细错误信息

---

### 阶段十：可观测性（迭代 10.1 ~ 10.3，共 3 周）

**阶段目标：** 全链路追踪、指标可视化、告警通知

**新增能力：** 从黑盒运行 → 全链路可观测的生产级平台

```
agent-platform-observability
   ├── AgentTraceHook (HarnessAgent 内置追踪)
   ├── Token 用量统计 + 成本核算
   ├── 性能指标仪表盘
   └── 告警规则引擎

数据库新增表
   ├── trace_record + trace_span
   ├── token_usage_stat
   └── alert_rule + alert_history
```

---

#### 迭代 10.1：Trace 追踪（~1 周）

**目标：** 每次对话生成完整 Trace，可追溯每一步执行细节

**交付物：** Trace 追踪页面（列表 + 瀑布图）

**详细任务：**

- [ ] 实现 `trace_record` + `trace_span` 表的 Schema、Entity、Mapper
- [ ] 集成 `AgentTraceHook`（HarnessAgent 内置追踪 → 每次对话生成 Trace + 多个 Span）
- [ ] 实现 Trace 列表 API（`GET /monitor/traces`，分页 + 按 Agent/状态/时间筛选）
- [ ] 实现 Trace 详情 API（`GET /monitor/traces/{traceId}`，返回 Trace + 所有 Span）
- [ ] 前端：实现 Trace 列表页面（表格 + 筛选条件）
- [ ] 前端：实现 Trace 详情瀑布图（每个 Span 的时间条 + 类型标识：reasoning/acting/tool_call/model_call）

**验证标准：**
- 进行对话 → Trace 列表中出现记录 → 详情瀑布图展示 Reasoning/Acting/ToolCall 各 Span
- Span 的耗时、Token 消耗、状态正确显示

---

#### 迭代 10.2：指标仪表盘 + 成本核算（~1 周）

**目标：** 核心指标可视化，Token 用量和成本可统计

**交付物：** 指标仪表盘（ECharts）+ Token 用量统计 + 成本核算

**详细任务：**

- [ ] 实现 `token_usage_stat` 表 + 定时聚合任务（按 Agent/模型/日 维度聚合）
- [ ] 实现成本核算（Token 用量 × `model_config.cost_per_1k_*` 单价 = 成本）
- [ ] 实现性能指标 API（`GET /monitor/metrics`：聚合查询延迟/错误率/Token 用量）
- [ ] 实现单 Agent 指标 API（`GET /monitor/metrics/agents/{id}`）
- [ ] 前端：实现指标仪表盘（ECharts：延迟 P50/P95/P99、错误率、Token 用量趋势、成本趋势）
- [ ] 前端：实现单 Agent 指标详情页

**验证标准：**
- 指标仪表盘：延迟、错误率、Token 用量、成本数据与实际一致
- 按 Agent/模型/日维度统计 Token 用量正确
- 成本核算金额与模型单价匹配

---

#### 迭代 10.3：告警规则（~1 周）

**目标：** 指标超阈值自动告警

**交付物：** 告警规则管理 + 告警通知 + 前端告警页面

**详细任务：**

- [ ] 实现 `alert_rule` 表 + `alert_history` 表的 Schema、Entity、Mapper
- [ ] 实现告警规则管理 API（CRUD + 启用/禁用）
- [ ] 实现告警引擎（定时轮询指标 → 超阈值触发告警 → 通知：站内信/Webhook）
- [ ] 实现 Hook 审计日志（通过 Hook 机制记录每个 Reasoning/Acting 步骤到审计表）
- [ ] 前端：实现告警管理页面（规则列表 + 创建/编辑规则表单 + 告警历史列表）
- [ ] 前端：仪表盘增加告警状态指示（当前活跃告警数量 + 最近告警记录）

**验证标准：**
- 创建告警规则（延迟 P95 > 10s）→ 模拟高延迟对话 → 告警触发 → 告警历史中出现记录
- 告警通知（站内信/Webhook）正确发送
- Hook 审计：每轮 ReAct 迭代的 Thinking/Acting/Observation 均有审计记录

---

### 阶段十一：运维管理 + 开放 API（迭代 11.1 ~ 11.2，共 2 周）

**阶段目标：** 完整的管理后台 + 对外暴露 OpenAI 兼容 API

**新增能力：** 从开发者自用 → 可运维、可对外开放的企业级平台

```
agent-platform-admin
   ├── 用户管理
   ├── 角色权限管理
   ├── 系统配置中心
   └── 资源监控

runtime 增强
   ├── OpenAI 兼容 API
   └── 限流增强 (Redis + Lua)
```

---

#### 迭代 11.1：运维管理后台（~1 周）

**目标：** 用户管理、角色权限管理、系统配置中心可用

**交付物：** 完整的管理后台页面

**详细任务：**

- [ ] 实现用户管理页面（用户列表 + 创建/编辑/禁用用户 + 分配角色）
- [ ] 实现角色权限管理页面（角色列表 + 编辑角色权限矩阵）
- [ ] 实现 `system_config` 表的 Schema、Entity、Mapper、Service
- [ ] 实现系统配置中心页面（全局参数管理：默认 maxIters、Token 上限、存储配置等）
- [ ] 实现资源监控面板（Agent 实例数、活跃会话数、Redis/PG 使用率）
- [ ] 前端：管理后台入口（Admin 侧边栏：用户管理 / 角色管理 / 系统配置 / 资源监控）

**验证标准：**
- 管理员创建新用户 → 分配 developer 角色 → 新用户登录后只能访问 developer 权限的功能
- 修改系统配置（默认 maxIters = 20）→ 新创建的 Agent 默认值为 20
- 资源监控面板数据与实际一致

---

#### 迭代 11.2：OpenAI 兼容 API + 限流（~1 周）

**目标：** 对外暴露标准 OpenAI 格式 API，可按 API Key/用户/IP 限流

**交付物：** `/v1/chat/completions` 兼容接口 + 限流能力

**详细任务：**

- [ ] 实现 OpenAI 兼容 API（`POST /api/v1/agent-api/{apiKey}/v1/chat/completions`，兼容 OpenAI 请求/响应格式）
- [ ] 实现 SSE 流式兼容（`stream: true` 时返回 `data: {...}\n\n` 格式，兼容 OpenAI SDK）
- [ ] 实现限流增强（Redis + Lua 滑动窗口限流，按 API Key / 用户 / IP 维度）
- [ ] 实现 `RateLimitFilter`（WebFilter 层统一限流）
- [ ] 实现 API 文档页面（OpenAI 兼容 API 使用说明 + 代码示例）

**验证标准：**
- 使用 OpenAI Python SDK 调用 AgentVerse API → 对话成功（同步 + 流式）
- 限流：超过阈值后返回 429 Too Many Requests
- 不同 API Key 的限流配额独立计算

---

### 阶段十二：评测 + 模板市场（迭代 12.1 ~ 12.2，共 2 周）

**阶段目标：** Agent 可自动化评测，模板市场可复用

**新增能力：** 从手动验证 → 自动化评测体系 + 可复用的 Agent 模板

```
agent-platform-studio 增强
   ├── 评测框架（测试集 + 自动化评测 + 报告）
   └── Agent 模板市场

agent-platform-admin 增强
   └── 模板管理
```

---

#### 迭代 12.1：Agent 评测框架（~1 周）

**目标：** Agent 可批量自动化评测，生成评测报告

**交付物：** 评测框架（测试集管理 + 自动化评测 + 报告生成）

**详细任务：**

- [ ] 实现评测相关表（测试集、测试用例、评测任务、评测结果）的 Schema、Entity、Mapper
- [ ] 实现测试集管理 API（创建测试集 → 添加测试用例：输入 + 期望输出/评分标准）
- [ ] 实现自动化评测引擎（批量执行测试用例 → 调用 Agent → 对比实际输出与期望输出 → 计算准确率/相关性分数）
- [ ] 实现评测报告生成（总分 + 各用例详情 + 失败用例分析 + 趋势对比）
- [ ] 前端：实现评测管理页面（测试集列表 + 创建/编辑测试集 + 触发评测 + 查看报告）

**验证标准：**
- 创建测试集（10 个用例）→ 执行评测 → 生成报告（准确率 + 各用例评分）
- 修改 Agent 后重新评测 → 可查看两次评测结果的对比

---

#### 迭代 12.2：Agent 模板市场（~1 周）

**目标：** 预置 Agent 模板，一键创建 Agent

**交付物：** 模板市场后端 + 前端模板浏览页面

**详细任务：**

- [ ] 实现 Agent 模板后端（模板 CRUD + 分类 + 标签 + 使用次数统计）
- [ ] 实现模板一键创建 API（从模板克隆 Agent + 所有子表数据）
- [ ] 预置 5-10 个常用 Agent 模板（客服助手、代码审查、文档摘要、数据分析等）
- [ ] 前端：实现模板市场页面（模板卡片列表 + 分类筛选 + 详情预览 + 一键创建按钮）
- [ ] 前端：Agent 创建流程增加"从模板创建"入口

**验证标准：**
- 模板市场：浏览模板 → 查看详情 → 一键创建 → Agent 列表中出现新 Agent
- 从模板创建的 Agent 配置正确（Prompt、模型、工具等）

---

### 阶段十三：A2A + 国际化 + 部署（迭代 13.1 ~ 13.2，共 2 周）

**阶段目标：** 跨平台 Agent 通信、前端国际化、生产部署方案就绪

**新增能力：** 从单机平台 → 可互联、可国际化、可生产部署的完整平台

```
runtime 增强
   ├── A2A 协议 (extensions-a2a)
   └── 限流熔断

agent-platform-web 增强
   ├── 国际化 (i18n)
   └── 前端最终打磨

部署方案
   ├── 部署脚本 + Nginx 配置
   ├── K8s Helm Chart
   └── 用户文档 + 开发者文档
```

---

#### 迭代 13.1：A2A 协议 + 国际化（~1 周）

**目标：** Agent 可与外部 A2A Agent 通信，前端支持中英文切换

**交付物：** A2A 集成 + i18n 支持

**详细任务：**

- [ ] 集成 `extensions-a2a`（Agent-to-Agent 跨平台通信协议 → Agent 可调用外部 A2A Agent）
- [ ] 前端：实现国际化 i18n（vue-i18n 集成 → 中文/英文双语 → 语言切换）
- [ ] 前端：全量文案国际化（所有页面、组件、提示信息）
- [ ] 前端：响应式适配最终检查（最小支持 1280px 宽度）
- [ ] 前端：加载状态优化（骨架屏、Loading 动画、错误提示统一）

**验证标准：**
- 跨平台 A2A：AgentVerse Agent 可调用外部 A2A Agent 并获取结果
- 切换语言 → 前端所有文案中英文切换正确
- 主流浏览器兼容（Chrome、Firefox、Edge 最新版）

---

#### 迭代 13.2：部署方案 + 文档 + 回归测试（~1 周）

**目标：** 生产部署方案就绪，文档完整，全链路回归验证通过

**交付物：** 部署脚本 + K8s Helm Chart + 用户/开发者文档

**详细任务：**

- [ ] 编写部署脚本（fat jar 启动脚本 + Nginx 配置 + 外部组件连接配置模板）
- [ ] 编写 K8s Helm Chart（Deployment + Service + ConfigMap + Secret + Ingress + PVC）
- [ ] 编写用户文档（快速开始 / Agent 创建指南 / 工具开发指南 / API 参考）
- [ ] 编写开发者文档（架构说明 / 模块开发指南 / 贡献指南）
- [ ] 全链路回归测试（阶段一到阶段十二核心功能端到端验证）
- [ ] 性能基准测试（并发对话、流式响应延迟、工具调用吞吐量）

**验证标准：**
- 部署脚本 `./start.sh` 启动 → 服务正常运行 → 浏览器访问可用
- `helm install agentverse ./chart` → K8s 集群中部署成功 → Pod 全部 Running
- 全链路回归：创建 Agent → 挂载工具/知识库/Skill → 发布 → 对话 → 调试 → 工作流 → 监控 → 全部正常

***

### 阶段十四：性能优化与稳定（迭代 14.1 ~ 14.2，共 2 周）

**阶段目标：** 系统性能优化、大字段存储优化、生产稳定性保障

**新增能力：** 从能用 → 好用（性能提升 + 稳定性加强）

```
存储优化
   └── 大字段工具结果 → 文件系统存储

性能优化
   ├── Session 缓存命中率优化
   └── Token 计算异步化

稳定性
   └── 熔断降级增强
```

---

#### 迭代 14.1：大字段优化 + 缓存优化（~1 周）

**目标：** 超过阈值（如 100KB）的工具结果存文件系统，Session 缓存命中率提升

**交付物：** 文件系统存储适配器 + 缓存命中率统计

**详细任务：**

- [ ] 设计并实现大字段工具结果文件系统存储（超过阈值存文件，数据库存路径引用）
- [ ] 文件系统存储路径设计：`/data/sessions/{session_id}/tool_results/`
- [ ] 文件生命周期管理（归档、删除策略）
- [ ] Session Redis 缓存命中率监控与优化
- [ ] Token 计算异步化（避免阻塞对话响应）

**验证标准：**
- 大字段工具结果（>100KB）自动存文件系统，数据库只存路径
- 读取时自动从文件系统加载，对上层透明
- Session 缓存命中率 > 80%

---

#### 迭代 14.2：熔断降级 + 回归验证（~1 周）

**目标：** 增强系统稳定性，全链路回归验证

**交付物：** 熔断降级策略 + 回归测试报告

**详细任务：**

- [ ] 增强熔断降级策略（模型调用超时、API Key 配额耗尽、工具调用失败）
- [ ] 限流规则优化（按用户/Agent/IP 分级限流）
- [ ] 全链路回归测试（阶段一到阶段十三核心功能端到端验证）
- [ ] 性能基准测试（并发对话、流式响应延迟、工具调用吞吐量）
- [ ] 监控告警最终检查与调优

**验证标准：**
- 熔断触发条件正确，降级策略生效
- 并发 50 用户同时对话 → 系统响应正常，无 OOM
- 流式响应延迟 < 2s（排除模型响应时间）
- 全链路回归全部通过

***

## 十、关键技术决策点

| 决策项            | 推荐方案                     | 备选方案                 | 理由                                            |
| -------------- | ------------------------ | -------------------- | --------------------------------------------- |
| **部署架构**       | 多模块单进程（fat jar）          | 微服务                  | 简化运维，降低复杂度，模块间直接方法调用                          |
| **网关能力**       | WebFilter 层（runtime 内置）  | Spring Cloud Gateway | 单进程无需独立网关，Filter 足够                           |
| **向量数据库**      | ChromaDB                 | pgvector / Milvus / Weaviate | 独立部署、API 友好、专为 LLM 应用设计，后期可迁 Milvus |
| **Agent 配置存储** | 规范化数据库表（YAML 仅用于版本快照和导入导出） | YAML Blob / JSON Blob | 关系查询、外键约束、字段级 CRUD、统计分析，YAML 保留为派生格式 |
| **会话存储**       | Redis + PG + 文件系统            | 仅 Redis              | Redis 热数据 + PG 冷数据 + 文件系统存大字段，`extensions-session-redis`，阶段十四优化大字段存储 |
| **流式对话**       | SSE (Server-Sent Events) | WebSocket            | SSE 更简单，单向推送够用                                |
| **前端 DAG 编辑器** | VueFlow                  | AntV X6              | VueFlow 更轻量、文档友好                              |
| **认证方案**       | JWT + Spring Security    | Keycloak             | 初期轻量，后期可对接 Keycloak                           |
| **文件存储**       | 本地文件系统                | MinIO / 阿里云 OSS / AWS S3 | 初期简单，后续可扩展为对象存储                    |
| **消息队列**       | RocketMQ (可选)            | Kafka                | `extensions-rocketmq` 原生支持                    |

***

## 十一、技术栈总览

### 后端

| 技术              | 版本        | 用途      |
| --------------- | --------- | ------- |
| Java            | 17        | 开发语言    |
| Spring Boot     | 3.3.x     | 应用框架    |
| Spring WebFlux  | -         | 响应式 Web |
| Spring Security | -         | 认证鉴权    |
| AgentScope-Java | 1.1.0-RC1 | 智能体引擎   |
| MyBatis-Plus    | 3.5.x     | ORM     |
| PostgreSQL      | 15+       | 主数据库    |
| ChromaDB        | -         | 向量存储    |
| Redis           | 7+        | 缓存 / 会话 |
| Lombok          | -         | 代码简化    |
| Jackson         | -         | JSON 处理 |
| SnakeYAML       | -         | YAML 解析 |
| JUnit 5         | -         | 单元测试    |
| Testcontainers  | -         | 集成测试    |

### 前端

| 技术            | 版本  | 用途          |
| ------------- | --- | ----------- |
| Vue           | 3.x | 前端框架        |
| TypeScript    | 5.x | 类型安全        |
| Vite          | 6.x | 构建工具        |
| Element Plus  | 2.x | UI 组件库      |
| Pinia         | 2.x | 状态管理        |
| Vue Router    | 4.x | 路由          |
| Axios         | 1.x | HTTP 请求     |
| ECharts       | 5.x | 数据图表        |
| VueFlow       | 1.x | DAG 编辑器     |
| Monaco Editor | -   | 代码编辑器       |
| markdown-it   | -   | Markdown 渲染 |

### 基础设施

| 技术                      | 用途          | 说明 |
| ----------------------- | ----------- | --- |
| Kubernetes + Helm       | 生产部署        | 可选 |
| Nginx                   | 反向代理 / 静态资源 | 生产环境 |
| Prometheus + Grafana    | 监控（可选）      | 可选 |

**基础设施决策说明：**

- **不使用 Docker 容器化**：PostgreSQL、Redis、ChromaDB 均作为外部已有组件，通过连接配置接入，不在项目内容器化部署
- **向量存储使用 ChromaDB**：独立部署的向量数据库，通过 HTTP API 访问，不再使用 PostgreSQL 的 pgvector 扩展
- **文件存储使用本地文件系统**：文档上传存储到本地磁盘目录，不使用 MinIO 等对象存储服务，后续可按需扩展为对象存储
- **不引入 Spring Cloud Gateway**：网关能力（认证、限流、CORS、日志）通过 runtime 内置的 WebFilter 层实现，保持单进程架构的简洁性
