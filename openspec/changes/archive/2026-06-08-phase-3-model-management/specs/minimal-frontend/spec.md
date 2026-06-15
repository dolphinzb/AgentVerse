# minimal-frontend（修改）

## 变更说明

Agent 创建/编辑弹窗增加模型选择器；新增模型管理页、Token 统计页；流式聊天请求携带 Authorization header。

## 新增页面

| 路由 | 页面 | 说明 |
|------|------|------|
| /models | ModelManagement.vue | 模型管理主页（预设供应商卡片 + 自定义模型卡片 + 已添加模型列表） |
| /models/stats | ModelStatsView.vue | Token 用量统计 |

## 新增组件

| 组件 | 说明 |
|------|------|
| AddModelDialog.vue | 预设供应商添加模型弹窗（API Key + Base URL 自动填充 + 模型名称下拉框 + 参数滑块 + 连接测试按钮） |
| AddCustomModelDialog.vue | 自定义模型添加弹窗（API 类型下拉框 + API Key + Base URL + 模型名称 + 自定义 Header + 参数滑块 + 连接测试按钮） |

## 修改页面

### AgentList.vue

- 创建/编辑弹窗新增模型选择器：先选 Provider（下拉框），再选该 Provider 下的 Model（二级下拉框）
- modelConfigId 必填

### ModelManagement.vue

- 预设供应商卡片（DashScope/OpenAI/DeepSeek），点击弹出 AddModelDialog
- 自定义模型卡片，点击弹出 AddCustomModelDialog
- 已添加模型列表（按 Provider 分组），支持编辑/删除/禁用/连接测试
- 按供应商筛选

## 新增 API 封装

`src/api/model.ts` — Provider、ModelConfig、ModelAdd、ModelStats、Presets、Types、ConnectionTest API

## 新增 Store

`src/stores/model.ts` — Pinia store（providers、modelConfigs、stats、presets、providerTypes）

## Bug 修复

- 流式聊天 fetch 请求携带 Authorization header（从 localStorage 读取 token）
- 列表数据字段从 `configs`/`providers` 修正为 `records`（匹配 MyBatis-Plus Page 对象）
