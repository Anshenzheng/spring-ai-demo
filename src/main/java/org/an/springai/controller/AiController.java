package org.an.springai.controller;


import jakarta.annotation.PostConstruct;
import org.an.springai.agents.SupervisorAgent;
import org.an.springai.pojo.User;
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
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.DocumentTransformer;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static org.an.springai.config.Constant.AGENT_SYSTEM_PROMPT;

@RestController
@CrossOrigin(
        origins = "http://localhost:4200", // 允许的源
        allowedHeaders = "*", // 允许的请求头
        methods = {RequestMethod.GET, RequestMethod.POST}, // 允许的方法
        allowCredentials = "true", // 是否允许Cookie
        maxAge = 3600 // 预检有效期
)
public class AiController {
    @Autowired
    private ChatModel zhiPuAiChatModel;

    @Autowired
    private ChatClient zpChatGlm4vFlashClient;

    @Autowired
    private ChatClient zpChatGlm4PlusClient;

    @Autowired
    private DocumentTransformer documentTransformer;

    @Autowired
    private ChatMemory chatMemory;

    @Autowired
    private ToolCallbackProvider toolCallbackProvider;

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
     * Sample call to communicate with LLM
     * @param userInput
     * @return
     */
    @GetMapping("/ai")
    public String generation(@RequestParam(defaultValue  = "你相信知识改变命运吗") String userInput){

        ChatClient chatClient = ChatClient.create(zhiPuAiChatModel);
        String content = chatClient.prompt(userInput).call().content();
        return content;
    }

    /**
     * Sample call to communicate with LLM to feedback with JSON Object
     * @param userInput
     * @return
     */
    @GetMapping("/ai/json")
    public Object generationJson(@RequestParam(defaultValue  = "随机生成一份用户信息") String userInput){

        ChatClient chatClient = ChatClient.create(zhiPuAiChatModel);
        User zpContent = chatClient.prompt(userInput).call().entity(User.class);
        return zpContent;
    }

    /**
     * Sample call to communicate with LLM to feedback with JSON Array
     * @param userInput
     * @return
     */
    @GetMapping("/ai/json/list")
    public Object generationJsonList(@RequestParam(defaultValue  = "随机生成六份用户信息") String userInput){

        ChatClient chatClient = ChatClient.create(zhiPuAiChatModel);
        List<User> zpContent = chatClient.prompt(userInput).call().entity(new ParameterizedTypeReference<>(){});
        return zpContent;
    }

    /**
     * Sample call to communicate with LLM to feedback via stream
     * @param userInput
     * @return
     */
    @GetMapping(value="/ai/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE+";charset=UTF-8")
    public Flux<String> streamContent(@RequestParam(defaultValue  = "和我说一个长笑话") String userInput){

        ChatClient chatClient = ChatClient.create(zhiPuAiChatModel);
        return chatClient.prompt(userInput).advisors(new SimpleLoggerAdvisor()).stream().content();
    }

    /**
     * Sample call to communicate with LLM to based on templated prompt
     * @param userName
     * @return
     */
    @GetMapping(value="/ai/promote/template")
    public Object promoteTemplate(@RequestParam(defaultValue  = "安果果") String userName){
        String template = "请帮我写一首诗，作者是{userName}, 内容围绕作者的名字去写, 且主题要围绕:{subject}";
        ChatClient chatClient = ChatClient.create(zhiPuAiChatModel);
        return chatClient.prompt().user(u->u.text(template).param("userName", userName)
                        .params(Map.of("subject","love")))
                .advisors(new SimpleLoggerAdvisor()).call().content();
    }

    /**
     * Sample call to communicate with different LLM models
     * @param
     * @return
     */
    @GetMapping(value="/ai/client/configuration/model1")
    public Object clientConfiguration1(){
        return zpChatGlm4vFlashClient.prompt("你最新的训练数据是什么时间？你使用了哪种大模型？").call().content();
    }

    /**
     * Sample call to communicate with different LLM models
     * @param
     * @return
     */
    @GetMapping(value="/ai/client/configuration/model2")
    public Object clientConfiguration2(){
        return zpChatGlm4PlusClient.prompt("你最新的训练数据是什么时间？你使用了哪种大模型？").call().content();
    }

    /**
     * Sample call to communicate with LLM (with memory)
     * @param userInput
     * @return
     */
    @GetMapping(value="/ai/chat/memory")
    public Object chatMemory(String userInput){
        ChatClient chatClient = ChatClient.create(zhiPuAiChatModel);
        return chatClient.prompt(userInput)
                .advisors(new SimpleLoggerAdvisor(), MessageChatMemoryAdvisor.builder(chatMemory).build())
                .advisors( a-> a.param(ChatMemory.CONVERSATION_ID, "conversationId"))
                .call().content();
    }
    /**
     * Sample call to communicate with LLM (with functioning call/tools)
     * @param userInput
     * @return
     */
    @GetMapping(value="/ai/tools")
    public Object tools(String userInput){
        return zpChatGlm4PlusClient.prompt(userInput)
                .advisors(new SimpleLoggerAdvisor(), MessageChatMemoryAdvisor.builder(chatMemory).build())
                .advisors( a-> a.param(ChatMemory.CONVERSATION_ID, "conversationId"))
                .tools(new DateTimeTools())
                .call().content();
    }


    /**
     * Sample call to communicate with LLM (with MCP)
     * @param userInput
     * @return
     */
    @GetMapping(value="/ai/mcp")
    public Object mcp(String userInput){
        return zpChatGlm4PlusClient.prompt(userInput)
                .advisors(new SimpleLoggerAdvisor(), MessageChatMemoryAdvisor.builder(chatMemory).build())
                .advisors( a-> a.param(ChatMemory.CONVERSATION_ID, "conversationId"))
                .toolCallbacks(toolCallbackProvider)
                .call().content();
    }



    /**
     * Sample call to communicate with LLM (with RAG)
     * @param userInput
     * @return
     */
    @GetMapping(value="/ai/rag")
    public Object rag(@RequestParam(defaultValue = "我想新增一个配置到DIET Framework，请问该如何开始") String userInput){
        return zpChatGlm4PlusClient.prompt(userInput)
                .advisors(new SimpleLoggerAdvisor(), MessageChatMemoryAdvisor.builder(chatMemory).build())
                .advisors( a-> a.param(ChatMemory.CONVERSATION_ID, "conversationId"))
                .advisors(new SimpleLoggerAdvisor(), new QuestionAnswerAdvisor(dietGuideStore))
                .toolCallbacks(toolCallbackProvider)
                .call().content();
    }


    /**
     * Agent used for frontend streamed chat, single Agent to provide all services
     * @param userInput
     * @return
     */
    @GetMapping(value="/ai/agent/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE+";charset=UTF-8")
    public Flux<String> agentStream(String userInput){
        Flux<String>  content = zpChatGlm4PlusClient.prompt(userInput).system(AGENT_SYSTEM_PROMPT)
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
        String  response = zpChatGlm4PlusClient.prompt(userInput).system(AGENT_SYSTEM_PROMPT)
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
