package org.an.springai.controller;

import jakarta.annotation.PostConstruct;
import org.an.springai.pojo.User;
import org.an.springai.tools.DateTimeTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentTransformer;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

@RestController
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
    private VectorStore vectorStore;

    @GetMapping("/ai")
    public String generation(@RequestParam(defaultValue  = "你相信知识改变命运吗") String userInput){

        ChatClient chatClient = ChatClient.create(zhiPuAiChatModel);
        String content = chatClient.prompt(userInput).call().content();
        return content;
    }

    @GetMapping("/ai/json")
    public Object generationJson(@RequestParam(defaultValue  = "随机生成一份用户信息") String userInput){

        ChatClient chatClient = ChatClient.create(zhiPuAiChatModel);
        String content = chatClient.prompt(userInput).call().content();
        User zpContent = chatClient.prompt(userInput).call().entity(User.class);
        return zpContent;
    }

    @GetMapping("/ai/json/list")
    public Object generationJsonList(@RequestParam(defaultValue  = "随机生成六份用户信息") String userInput){

        ChatClient chatClient = ChatClient.create(zhiPuAiChatModel);
        List<User> zpContent = chatClient.prompt(userInput).call().entity(new ParameterizedTypeReference<List<User>>(){});
        return zpContent;
    }

    @GetMapping(value="/ai/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE+";charset=UTF-8")
    public Flux<String> streamContent(@RequestParam(defaultValue  = "和我说一个长笑话") String userInput){

        ChatClient chatClient = ChatClient.create(zhiPuAiChatModel);
        return chatClient.prompt(userInput).advisors(new SimpleLoggerAdvisor()).stream().content();
    }

    @GetMapping(value="/ai/promote/template")
    public Object promoteTemplate(@RequestParam(defaultValue  = "安果果") String userName){
        String template = "请帮我写一首诗，作者是{userName}, 内容围绕作者的名字去写, 且主题要围绕:{subject}";
        ChatClient chatClient = ChatClient.create(zhiPuAiChatModel);
        return chatClient.prompt().user(u->u.text(template).param("userName", userName)
                        .params(Map.of("subject","love")))
                .advisors(new SimpleLoggerAdvisor()).call().content();
    }

    @GetMapping(value="/ai/client/configuration/model1")
    public Object clientConfiguration1(){
        return zpChatGlm4vFlashClient.prompt("你最新的训练数据是什么时间？你使用了哪种大模型？").call().content();
    }

    @GetMapping(value="/ai/client/configuration/model2")
    public Object clientConfiguration2(){
        return zpChatGlm4PlusClient.prompt("你最新的训练数据是什么时间？你使用了哪种大模型？").call().content();
    }

    @GetMapping(value="/ai/chat/memory")
    public Object chatMemory(String userInput){
        ChatClient chatClient = ChatClient.create(zhiPuAiChatModel);
        return chatClient.prompt(userInput)
                .advisors(new SimpleLoggerAdvisor(), MessageChatMemoryAdvisor.builder(chatMemory).build())
                .advisors( a-> a.param(ChatMemory.CONVERSATION_ID, "conversationId"))
                .call().content();
    }

    @GetMapping(value="/ai/tools")
    public Object tools(String userInput){
        return zpChatGlm4PlusClient.prompt(userInput)
                .advisors(new SimpleLoggerAdvisor(), MessageChatMemoryAdvisor.builder(chatMemory).build())
                .advisors( a-> a.param(ChatMemory.CONVERSATION_ID, "conversationId"))
                .tools(new DateTimeTools())
                .call().content();
    }



    @GetMapping(value="/ai/mcp")
    public Object mcp(String userInput){
        return zpChatGlm4PlusClient.prompt(userInput)
                .advisors(new SimpleLoggerAdvisor(), MessageChatMemoryAdvisor.builder(chatMemory).build())
                .advisors( a-> a.param(ChatMemory.CONVERSATION_ID, "conversationId"))
                .toolCallbacks(toolCallbackProvider)
                .call().content();
    }

//    @PostConstruct
//    public void init(){
//        Resource resource = new PathResource("C:\\Users\\Administrator\\Desktop\\DIET.pdf");
//        TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(resource);
//        List<Document> documents = tikaDocumentReader.get();
//        List<Document> transformedDocs = documentTransformer.apply(documents);
//        vectorStore.accept(transformedDocs);
//        System.out.println("init is done");
//    }

    @GetMapping(value="/ai/rag")
    public Object rag(@RequestParam(defaultValue = "我想新增一个配置到DIET Framework，请问该如何开始") String userInput){
        return zpChatGlm4PlusClient.prompt(userInput)
                .advisors(new SimpleLoggerAdvisor(), MessageChatMemoryAdvisor.builder(chatMemory).build())
                .advisors( a-> a.param(ChatMemory.CONVERSATION_ID, "conversationId"))
                .advisors(new SimpleLoggerAdvisor(), new QuestionAnswerAdvisor(vectorStore))
                .toolCallbacks(toolCallbackProvider)
                .call().content();
    }


}
