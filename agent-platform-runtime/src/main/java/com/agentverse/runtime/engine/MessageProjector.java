/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.runtime.engine;

import com.agentverse.common.dto.BlockDto;
import com.agentverse.common.dto.MessageResponse;
import io.agentscope.core.message.ImageBlock;
import io.agentscope.core.message.Msg;
import io.agentscope.core.message.Source;
import io.agentscope.core.message.TextBlock;
import io.agentscope.core.message.ThinkingBlock;
import io.agentscope.core.message.ToolResultBlock;
import io.agentscope.core.message.ToolUseBlock;
import io.agentscope.core.message.URLSource;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class MessageProjector {
    private static final DateTimeFormatter TIMESTAMP_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    public MessageResponse project(Msg msg) {
        ArrayList<BlockDto> blocks = new ArrayList<BlockDto>();
        for (Object block : msg.getContent()) {
            this.mapBlock(blocks, block);
        }
        return new MessageResponse(msg.getId(), msg.getRole().name(), blocks, this.parseTimestamp(msg.getTimestamp()));
    }

    public List<MessageResponse> projectAll(List<Msg> msgs) {
        ArrayList<MessageResponse> out = new ArrayList<MessageResponse>(msgs.size());
        for (Msg m : msgs) {
            out.add(this.project(m));
        }
        return out;
    }

    private void mapBlock(List<BlockDto> out, Object block) {
        if (block instanceof TextBlock) {
            TextBlock t = (TextBlock)block;
            out.add(new BlockDto.Text(t.getText()));
        } else if (block instanceof ThinkingBlock) {
            ThinkingBlock th = (ThinkingBlock)block;
            out.add(new BlockDto.Reasoning(th.getThinking()));
        } else if (block instanceof ToolUseBlock) {
            ToolUseBlock tu = (ToolUseBlock)block;
            out.add(new BlockDto.ToolUse(tu.getName(), tu.getInput()));
        } else if (block instanceof ToolResultBlock) {
            ToolResultBlock tr = (ToolResultBlock)block;
            Object result = this.extractResult(tr.getOutput());
            boolean isError = this.extractIsError(tr);
            out.add(new BlockDto.ToolResult(tr.getName(), result, isError));
        } else if (block instanceof ImageBlock) {
            ImageBlock img = (ImageBlock)block;
            String url = this.extractImageUrl(img);
            String mime = "image";
            out.add(new BlockDto.Image(url, mime));
        }
    }

    private Object extractResult(List<?> output) {
        if (output == null || output.isEmpty()) {
            return "";
        }
        Object first = output.get(0);
        if (first instanceof TextBlock) {
            TextBlock t = (TextBlock)first;
            return t.getText();
        }
        return output.toString();
    }

    private boolean extractIsError(ToolResultBlock tr) {
        return false;
    }

    private String extractImageUrl(ImageBlock img) {
        Source source = img.getSource();
        if (source instanceof URLSource) {
            URLSource u = (URLSource)source;
            return u.getUrl();
        }
        return "";
    }

    private Instant parseTimestamp(String ts) {
        if (ts == null || ts.isEmpty()) {
            return Instant.now();
        }
        try {
            LocalDateTime ldt = LocalDateTime.parse(ts, TIMESTAMP_FMT);
            return ldt.atZone(ZoneId.systemDefault()).toInstant();
        }
        catch (Exception e) {
            return Instant.now();
        }
    }
}

