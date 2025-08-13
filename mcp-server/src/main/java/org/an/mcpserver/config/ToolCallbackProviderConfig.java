package org.an.mcpserver.config;

import org.an.mcpserver.service.JavaLearningService;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallback;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ToolCallbackProviderConfig {

    @Bean
    public ToolCallbackProvider toolCallbackProvider(JavaLearningService learningService) {
        return MethodToolCallbackProvider.builder().toolObjects(learningService).build();
    }
}
