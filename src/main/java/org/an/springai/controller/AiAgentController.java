package org.an.springai.controller;


import jakarta.annotation.PostConstruct;
import org.an.springai.agents.SupervisorAgent;
import org.an.springai.service.SafeSqlExecutor;
import org.an.springai.tools.DateTimeTools;
import org.an.springai.tools.FeedInfoQueryTools;
import org.an.springai.tools.LogTools;
import org.an.springai.util.VectorUtil;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.document.DocumentTransformer;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.nio.file.Paths;

import static org.an.springai.config.Constant.AGENT_SYSTEM_PROMPT;

@RestController
@CrossOrigin(
        origins = "http://localhost:4200", // 允许的源
        allowedHeaders = "*", // 允许的请求头
        methods = {RequestMethod.GET, RequestMethod.POST}, // 允许的方法
        allowCredentials = "true", // 是否允许Cookie
        maxAge = 3600 // 预检有效期
)
public class AiAgentController {

    @Autowired
    private ChatClient chatClient;

    @Autowired
    private DocumentTransformer documentTransformer;

    @Autowired
    private ChatMemory chatMemory;


    @Autowired
    private VectorStore dietGuideStore;

    @Autowired
    private VectorStore dietBatchIssueStore;

    @Autowired
    private SafeSqlExecutor safeSqlExecutor;

    @Autowired
    SupervisorAgent supervisorAgent;




    /**
     * 用于将文档注入向量数据库，首次使用时将需 IS_THE_FIRST_TIME_TO_USE_THIS_APP 变量值置为true
     */

    private final boolean IS_THE_FIRST_TIME_TO_USE_THIS_APP = false;

    @PostConstruct
    public void init(){

        if(IS_THE_FIRST_TIME_TO_USE_THIS_APP){
            Resource resourceDIETGuide = new PathResource(Paths.get("src/main/resources/DIET_Guide.pdf").toAbsolutePath());
            VectorUtil.ingestDocToVectorStore(dietGuideStore,documentTransformer, resourceDIETGuide);

            Resource resourceDIETBatch = new PathResource(Paths.get("src/main/resources/DIET_Batch_Issue_Tracker.pdf").toAbsolutePath());
            VectorUtil.ingestDocToVectorStore(dietBatchIssueStore,documentTransformer, resourceDIETBatch);
        }

    }


    /**
     * Agent used for frontend streamed chat, single Agent to provide all services
     * @param userInput
     * @return
     */
    @GetMapping(value="/ai/agent/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE+";charset=UTF-8")
    public Flux<String> agentStream(String userInput){
        Flux<String>  content = chatClient.prompt(userInput).system(AGENT_SYSTEM_PROMPT)
                .advisors(new SimpleLoggerAdvisor(), MessageChatMemoryAdvisor.builder(chatMemory).build()
                )
                .advisors( a-> a.param(ChatMemory.CONVERSATION_ID, "agent-conversationId"))
                .tools(new FeedInfoQueryTools(safeSqlExecutor), new LogTools())
                .tools(new DateTimeTools())
                .stream().content();
        return content.concatWith(Flux.just("[complete]"));
    }

    /**
     * Agent used for frontend chat, single Agent to provide all services
     * @param userInput
     * @return
     */
    @GetMapping(value="/ai/agent")
    public String agent(String userInput){
        String  response = chatClient.prompt(userInput).system(AGENT_SYSTEM_PROMPT)
                .advisors(new SimpleLoggerAdvisor(), MessageChatMemoryAdvisor.builder(chatMemory).build()
                        ,new QuestionAnswerAdvisor(dietGuideStore),new QuestionAnswerAdvisor(dietBatchIssueStore)
                )
                .advisors( a-> a.param(ChatMemory.CONVERSATION_ID, "agent-conversationId"))
                .tools(new FeedInfoQueryTools(safeSqlExecutor), new LogTools())
                .tools(new DateTimeTools())
                .call().content();
        System.out.println(response);
        return response;
    }


    /**
     * Sample call to communicate with LLM (based on multiple agents), used for frontend unstreamed chat to provided service based on multiple agents
     * @param userInput
     * @return
     */
    @GetMapping(value="/ai/agent/graph")
    public String agentGraph(String userInput) {
        return supervisorAgent.execute(userInput);
    }
}
