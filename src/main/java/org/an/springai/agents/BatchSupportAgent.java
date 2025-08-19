package org.an.springai.agents;

import org.an.springai.tools.DateTimeTools;
import org.an.springai.tools.LogTools;
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
public class BatchSupportAgent extends Agent{

    @Autowired
    @Qualifier("dietBatchIssueStore")
    private VectorStore vectorStore;

    public BatchSupportAgent(@Qualifier("zpChatGlm4PlusClient") ChatClient chatClient, ChatMemory chatMemory) {
        this.chatMemory = chatMemory;
        this.chatClient = chatClient;
        this.conversationIdPrefix = "agent-batch-support-conversationId";
    }

    private final String SYSTEM_PROMPT = """
            你是一个运维支持专家, 分析用户提供的ETL执行日志，定位失败步骤并提供修复建议.
            操作步骤如下:
            1. 根据用户的提供的日志或错误描述, 分析出潜在的问题, 并要求用户提供feed的job信息
            或者根据用户提供的feed相关信息,去数据库中查出feed的job name
            2. 所有的日志文件都存放在E:\\Annan\\practise\\logs目录下, 文件名为格式为{job_name}.err.{timestamp} 和 
            {job_name}.out.{timestamp}. 请查看最新的日志文件,分析错误原因,并找出可能的root cause
            3. 根据错误信息去查找过往的issue tracker 
            4. 综合以上,将你获取到的所有信息进行分析,并返回相关且可行的方案/建议给用户
            
            如果回复内容中有代码相关的内容，请用markdown的格式回复以便于前端进行格式化显示。
            任何以上问题当你处理不了的时候，请让用户联系DIET 开发团队，联系方式 support@diet.com . 
            """;

    public String execute(String userPrompt){
        String  response = this.chatClient.prompt(userPrompt).system(SYSTEM_PROMPT)
                .advisors(new SimpleLoggerAdvisor(), MessageChatMemoryAdvisor.builder(chatMemory).build()
                )
                .advisors( a-> a.param(ChatMemory.CONVERSATION_ID, conversationIdPrefix))
                .advisors(new QuestionAnswerAdvisor(vectorStore))
                .tools(new DateTimeTools())
                .tools(new LogTools())
                .call().content();
        System.out.println(response);
        return response;
    }
}
