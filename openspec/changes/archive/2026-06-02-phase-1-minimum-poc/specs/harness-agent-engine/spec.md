## ADDED Requirements

### Requirement: HarnessAgent 构建
系统 SHALL 提供 HarnessAgentEngine 组件，能够根据 agent_definition 表的配置构建可执行的 HarnessAgent 实例。

#### Scenario: 成功构建 HarnessAgent
- **WHEN** 系统调用 buildAgent(agentId) 方法
- **THEN** 系统从数据库读取 agent_definition 配置（name、sys_prompt、max_iterations、workspace_mode 等），硬编码模型配置（使用预定义的 LLM），构建并返回 HarnessAgent 实例

#### Scenario: Agent 不存在
- **WHEN** 系统尝试构建一个不存在的 agentId
- **THEN** 系统抛出 AgentNotFoundException 异常

### Requirement: Agent 执行对话
系统 SHALL 提供 executeChat(agentId, sessionId, userMessage) 方法，执行 Agent 对话。

#### Scenario: 执行对话并获取响应
- **WHEN** 系统调用 executeChat 方法，传入有效的 agentId、sessionId 和用户消息
- **THEN** 系统构建 HarnessAgent 实例，调用其 chat 方法，返回 Agent 的响应内容

#### Scenario: 对话执行失败
- **WHEN** HarnessAgent 执行过程中发生异常（如 LLM 调用失败）
- **THEN** 系统捕获异常并抛出 ChatExecutionException，包含错误详情

### Requirement: Agent 中断对话
系统 SHALL 提供 interruptChat(sessionId) 方法，中断正在进行的对话。

#### Scenario: 成功中断对话
- **WHEN** 系统调用 interruptChat 方法，传入正在执行的 sessionId
- **THEN** 系统中断该会话的 HarnessAgent 执行，返回成功标志

#### Scenario: 中断不存在的会话
- **WHEN** 系统尝试中断一个不存在或未执行的 sessionId
- **THEN** 系统返回 false 或抛出 SessionNotFoundException
