package com.agentverse.runtime;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * AgentVerse 平台启动类
 */
@SpringBootApplication
@EnableScheduling
public class AgentVerseApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgentVerseApplication.class, args);
    }
}
