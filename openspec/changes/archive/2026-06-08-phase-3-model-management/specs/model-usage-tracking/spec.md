# model-usage-tracking

## 概述

Token 用量按会话记录、按模型聚合统计。只记录 Token 数量，不计算成本。

## 数据模型

### chat_usage 表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | VARCHAR(64) PK | 雪花算法生成 |
| session_id | VARCHAR(64) NOT NULL | 关联会话 |
| model_config_id | VARCHAR(64) NOT NULL FK | 关联 model_config.id |
| input_tokens | BIGINT DEFAULT 0 | 输入 Token 数 |
| output_tokens | BIGINT DEFAULT 0 | 输出 Token 数 |
| created_time | TIMESTAMP NOT NULL | 记录时间 |

## API

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | /v1/model-stats | Token 用量统计（按模型聚合） | model:read |

## 记录时机

ChatService 对话完成后（streamChat 的 doOnComplete），从模型响应提取 usage 写入 chat_usage。

## 统计维度

按 model_config_id 聚合：
- 调用次数（COUNT）
- 总输入 Token（SUM input_tokens）
- 总输出 Token（SUM output_tokens）
- 关联 Provider 名称、模型名称

## 错误码

无新增，复用 model:read 权限。
