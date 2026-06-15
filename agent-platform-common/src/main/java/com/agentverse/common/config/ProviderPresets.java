/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.common.config;

import com.agentverse.common.enums.ProviderType;
import java.util.List;
import lombok.Generated;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProviderPresets {
    public static final List<ProviderPreset> PRESETS = List.of(ProviderPreset.builder().providerType(ProviderType.DASHSCOPE).displayName("\u963f\u91cc\u4e91 DashScope").description("\u901a\u4e49\u5343\u95ee\u7cfb\u5217\u6a21\u578b").icon("dashscope").build(), ProviderPreset.builder().providerType(ProviderType.OPENAI).displayName("OpenAI").description("GPT \u7cfb\u5217\u6a21\u578b").icon("openai").build(), ProviderPreset.builder().providerType(ProviderType.DEEPSEEK).displayName("DeepSeek").description("DeepSeek \u7cfb\u5217\u6a21\u578b").icon("deepseek").build());

    public static class ProviderPreset {
        private ProviderType providerType;
        private String displayName;
        private String description;
        private String icon;

        @Generated
        ProviderPreset(ProviderType providerType, String displayName, String description, String icon) {
            this.providerType = providerType;
            this.displayName = displayName;
            this.description = description;
            this.icon = icon;
        }

        @Generated
        public static ProviderPresetBuilder builder() {
            return new ProviderPresetBuilder();
        }

        @Generated
        public ProviderType getProviderType() {
            return this.providerType;
        }

        @Generated
        public String getDisplayName() {
            return this.displayName;
        }

        @Generated
        public String getDescription() {
            return this.description;
        }

        @Generated
        public String getIcon() {
            return this.icon;
        }

        @Generated
        public static class ProviderPresetBuilder {
            @Generated
            private ProviderType providerType;
            @Generated
            private String displayName;
            @Generated
            private String description;
            @Generated
            private String icon;

            @Generated
            ProviderPresetBuilder() {
            }

            @Generated
            public ProviderPresetBuilder providerType(ProviderType providerType) {
                this.providerType = providerType;
                return this;
            }

            @Generated
            public ProviderPresetBuilder displayName(String displayName) {
                this.displayName = displayName;
                return this;
            }

            @Generated
            public ProviderPresetBuilder description(String description) {
                this.description = description;
                return this;
            }

            @Generated
            public ProviderPresetBuilder icon(String icon) {
                this.icon = icon;
                return this;
            }

            @Generated
            public ProviderPreset build() {
                return new ProviderPreset(this.providerType, this.displayName, this.description, this.icon);
            }

            @Generated
            public String toString() {
                return "ProviderPresets.ProviderPreset.ProviderPresetBuilder(providerType=" + String.valueOf((Object)this.providerType) + ", displayName=" + this.displayName + ", description=" + this.description + ", icon=" + this.icon + ")";
            }
        }
    }
}

