/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.common.dto;

import com.agentverse.common.dto.BlockDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.Instant;
import java.util.List;

public record MessageResponse(String id, String role, List<BlockDto> blocks, @JsonFormat(shape=JsonFormat.Shape.STRING) Instant createdAt) {
    public MessageResponse {
        if (blocks == null) {
            blocks = List.of();
        }
    }
}

