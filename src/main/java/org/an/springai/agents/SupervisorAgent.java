package org.an.springai.agents;

import org.an.springai.tools.DateTimeTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SupervisorAgent {
    @Autowired
    ChatClient zpChatGlm4PlusClient;

    @Autowired
    private ChatMemory chatMemory;

    @Autowired
    private BatchSupportAgent batchSupportAgent;
    @Autowired
    private DIETUsageAgent dietUsageAgent;
    @Autowired
    private FeedInventoryAgent feedInventoryAgent;

    private final String SYSTEM_PROMPT = """
            你是一个专业的DIET Framework的客服助手Dietary，负责对用户的问题进行分类，并将任务分给其它Agent执行。
            如果用户的问题是和DIET ETL框架的使用/配置相关的，那就返回DIET.
            如果用户的问题是和Metadata/Feed Inventory/Feed信息/数据库查询相关的, 那就返回INVENTORY.
            如果用户的问题是和job issue/batch issue相关的，那就返回ISSUE.
            如果是其它的问题，回复的内容仅能围绕你是DIET的智能助手，只提供 DIET ETL Framewrok/Feed Inventory/Framework batch issue有关的服务，不提供其它服务。
            有其他任何你处理不了的关于DIET Framework的问题请让用户联系DIET开发团队，联系方式 support@diet.com
            """;

    public String execute(String userPrompt){
        String  response = zpChatGlm4PlusClient.prompt(userPrompt).system(SYSTEM_PROMPT)
                .advisors(new SimpleLoggerAdvisor(), MessageChatMemoryAdvisor.builder(chatMemory).build()
                )
                .advisors( a-> a.param(ChatMemory.CONVERSATION_ID, "agent-supervisor-conversationId"))
                .tools(new DateTimeTools())
                .call().content();

        System.out.println(response);

        if("DIET".equalsIgnoreCase(response)){
            return dietUsageAgent.execute(userPrompt);
        }else  if("INVENTORY".equalsIgnoreCase(response)){
            return feedInventoryAgent.execute(userPrompt);
        }else if("ISSUE".equalsIgnoreCase(response)){
            return batchSupportAgent.execute(userPrompt);
        }else
            return response;
    }
}
