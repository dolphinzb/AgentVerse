## ADDED Requirements

### Requirement: 创建会话
系统 SHALL 提供 `POST /api/v1/chat/sessions` API，创建新的对话会话。

#### Scenario: 成功创建会话
- **WHEN** 客户端发送 POST 请求，包含 agentId
- **THEN** 系统生成唯一的 sessionId，在内存中创建会话记录（Map 存储），返回 200 和 sessionId

#### Scenario: Agent 不存在
- **WHEN** 客户端请求创建会话时传入不存在的 agentId
- **THEN** 系统返回 404 错误

### Requirement: 发送消息（同步）
系统 SHALL 提供 `POST /api/v1/chat/sessions/{sessionId}/messages` API，发送用户消息并同步返回 Agent 响应。

#### Scenario: 成功发送消息并获取响应
- **WHEN** 客户端发送包含 content 的消息到有效的 sessionId
- **THEN** 系统调用 HarnessAgentEngine.executeChat，将用户消息和 Agent 响应存储到内存，返回 200 和 Agent 响应内容

#### Scenario: 会话不存在
- **WHEN** 客户端向不存在的 sessionId 发送消息
- **THEN** 系统返回 404 错误

### Requirement: 流式对话（SSE）
系统 SHALL 提供 `GET /api/v1/chat/sessions/{sessionId}/stream` API，使用 Server-Sent Events 流式返回 Agent 响应。

#### Scenario: 成功流式对话
- **WHEN** 客户端建立 SSE 连接并发送用户消息
- **THEN** 系统调用 HarnessAgentEngine.streamChat，通过 SSE 逐步返回 Agent 响应片段，最后发送完成事件

#### Scenario: 流式对话中断
- **WHEN** 客户端在流式对话过程中断开连接或调用中断 API
- **THEN** 系统停止生成响应，清理会话状态

### Requirement: 查询会话历史
系统 SHALL 提供 `GET /api/v1/chat/sessions/{sessionId}/messages` API，查询会话的历史消息。

#### Scenario: 成功查询历史
- **WHEN** 客户端请求有效 sessionId 的历史消息
- **THEN** 系统从内存中读取该会话的所有消息记录，返回 200 和消息列表

#### Scenario: 会话不存在
- **WHEN** 客户端请求不存在的 sessionId
- **THEN** 系统返回 404 错误

### Requirement: 删除会话
系统 SHALL 提供 `DELETE /api/v1/chat/sessions/{sessionId}` API，删除指定会话。

#### Scenario: 成功删除会话
- **WHEN** 客户端删除有效的 sessionId
- **THEN** 系统从内存中移除该会话及其所有消息记录，返回 200

#### Scenario: 删除不存在的会话
- **WHEN** 客户端尝试删除不存在的 sessionId
- **THEN** 系统返回 404 错误
