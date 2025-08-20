package org.an.springai.agents;

import org.an.springai.tools.DateTimeTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class DIETUsageAgent extends Agent{

    @Autowired
    @Qualifier("dietGuideStore")
    private VectorStore vectorStore;

    public DIETUsageAgent(ChatClient chatClient, ChatMemory chatMemory) {
        this.chatMemory = chatMemory;
        this.chatClient = chatClient;
        this.conversationIdPrefix = "agent-diet-usage-conversationId";
    }

    private final String SYSTEM_PROMPT = """
        你是DIET ETL framework的严谨的智能助手, 仅能基于已知信息回答问题。
        回答规则：
        1. 所有数据/事实性描述必须明确标注来源
        2. 禁止编造任何新的信息和数据
        
        你仅可以给用户提供智能答疑服务，解析用户关于DIET框架的技术问题（如配置语法、错误代码调试），提供配置示例（如Autosys作业定义、验证规则JSON模板）等。
        如果回复内容中有代码相关的内容，请用markdown的格式回复以便于前端进行格式化显示。
        任何以上问题当你处理不了的时候，请让用户联系DIET 开发团队，联系方式 support@diet.com .
        """;

    public String execute(String userPrompt){
        String  response = chatClient.prompt(userPrompt).system(SYSTEM_PROMPT)
                .advisors(new SimpleLoggerAdvisor(), MessageChatMemoryAdvisor.builder(chatMemory).build()
                )
                .advisors( a-> a.param(ChatMemory.CONVERSATION_ID, conversationIdPrefix))
                .advisors(new QuestionAnswerAdvisor(vectorStore))
                .tools(new DateTimeTools())
                .call().content();
        System.out.println(response);
        return response;
    }
}
