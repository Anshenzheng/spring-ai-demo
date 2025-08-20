package org.an.springai.agents;


import org.an.springai.service.SafeSqlExecutor;
import org.an.springai.tools.DateTimeTools;
import org.an.springai.tools.FeedInfoQueryTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MetaDataQueryAgent extends Agent{

    @Autowired
    private SafeSqlExecutor safeSqlExecutor;

    public MetaDataQueryAgent(ChatClient chatClient, ChatMemory chatMemory) {
        this.chatMemory = chatMemory;
        this.chatClient = chatClient;
        this.conversationIdPrefix = "agent-meta-data-conversationId";
    }

    private final String SYSTEM_PROMPT = """
        你是一个SQL专家,根据用户提出的查询需求查询元数据(feed inventory)，组装安全的SQL查询语句查询出结果并以JSON的格式返回给用户。
        查询内容只限于以下tbl_feed_basic_info这一张表，查询条件只限于feed_name, feed_type, feed_key, status,
         source_app_name , destination_app_name, workstream (alias name is ws).
         所有查询结果请基于数据库表内的真实内容回复，不要瞎编乱造，如果查询不到内容就回复查询不到结果。
         每次生成SQL语句之后请先检查其是否足够安全，只有确定安全之后才能去执行查询操作。
         切记你只能生成查询语句，不允许生成任何的创建/修改/删除等操作的语句，以确保安全。
         执行查询时请根据工具箱中的查询工具去数据库做真实的查询操作以给用户返回真实的结果，切记不要模拟查询结果。
         如果数据库连接不可用，则回复数据库连接暂时不可用，建议用户稍后尝试。
        
         请务必将SQL的查询结果以标准的JSON格式返回给用户。
        """;

    public String execute(String userPrompt){
        String  response = chatClient.prompt(userPrompt).system(SYSTEM_PROMPT)
                .advisors(new SimpleLoggerAdvisor(), MessageChatMemoryAdvisor.builder(chatMemory).build()
                )
                .advisors( a-> a.param(ChatMemory.CONVERSATION_ID, conversationIdPrefix))
                .tools(new DateTimeTools())
                .tools(new FeedInfoQueryTools(safeSqlExecutor))
                .call().content();
        System.out.println(response);
        return response;
    }
}
