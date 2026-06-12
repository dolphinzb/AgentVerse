/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.runtime;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages={"com.agentverse"})
@EnableScheduling
public class AgentVerseApplication {
    public static void main(String[] args) {
        SpringApplication.run(AgentVerseApplication.class, (String[])args);
    }
}

