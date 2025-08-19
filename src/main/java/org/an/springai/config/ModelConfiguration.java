package org.an.springai.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chroma.vectorstore.ChromaApi;
import org.springframework.ai.chroma.vectorstore.ChromaVectorStore;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.deepseek.api.DeepSeekApi;
import org.springframework.ai.document.DocumentTransformer;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.zhipuai.ZhiPuAiChatModel;
import org.springframework.ai.zhipuai.ZhiPuAiChatOptions;
import org.springframework.ai.zhipuai.api.ZhiPuAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.client.RestClient;


@Configuration
@PropertySource("classpath:config.properties")
public class ModelConfiguration {

    //访问 https://bigmodel.cn/usercenter/proj-mgmt/apikeys 生成新的API key
    @Value("${spring.ai.zhipuai.api-key}")
    private String zpAPIKey;

    @Value("${spring.ai.deepseek.api-key}")
    private String deepSeekAPIKey;


    @Value("https://api.trychroma.com")
    private String chromaDBHost;

    // 访问 https://www.trychroma.com/， 创建数据库，以获取DB Token和Tenant
    @Value("${spring.ai.vectorstore.chroma.chromaDBToken}")
    private String chromaDBToken;

    @Value("${spring.ai.vectorstore.chroma.tenant}")
    private String tenantName;

    @Bean
    public ChatClient zpChatGlm4vFlashClient(){
        ZhiPuAiApi zhiPuAiApi = new ZhiPuAiApi(zpAPIKey);
        ZhiPuAiChatOptions options = ZhiPuAiChatOptions.builder().model("glm-4v-flash").build();
        ChatModel zhiPuAiChatModel = new ZhiPuAiChatModel(zhiPuAiApi, options);

        return ChatClient.builder(zhiPuAiChatModel).build();
    }

    @Bean
    public ChatClient zpChatGlm4PlusClient(){
        ZhiPuAiApi zhiPuAiApi = new ZhiPuAiApi(zpAPIKey);
        ZhiPuAiChatOptions options = ZhiPuAiChatOptions.builder().model("glm-4-plus").temperature(0.2).build();
        ChatModel zhiPuAiChatModel = new ZhiPuAiChatModel(zhiPuAiApi, options);

        return ChatClient.builder(zhiPuAiChatModel).build();
    }



    @Bean
    public DocumentTransformer documentTransformer(){
        return new TokenTextSplitter();
    }

    @Bean
    public ChromaApi chromaApi(RestClient.Builder restClientBuilder){
        restClientBuilder.defaultHeader("x-chroma-token", chromaDBToken)  // 你的 API 令牌
                .defaultHeader("tenant", tenantName) ; // 租户ID
        ChromaApi chromaApi = new ChromaApi(chromaDBHost, restClientBuilder, new ObjectMapper())//.withKeyToken(chromaDBKey)
                ;


        return chromaApi;
    }


    @Bean
    @Lazy
    public VectorStore dietGuideStore(EmbeddingModel embeddingModel, ChromaApi chromaApi){
        return ChromaVectorStore.builder(chromaApi, embeddingModel).tenantName(tenantName)
                .collectionName("DIET-GUIDE")
                .initializeSchema(true)
                .build();
    }

    @Bean
    @Lazy
    public VectorStore dietBatchIssueStore(EmbeddingModel embeddingModel, ChromaApi chromaApi){
        return ChromaVectorStore.builder(chromaApi, embeddingModel).tenantName(tenantName)
                .collectionName("DIET-BATCH-ISSUE")
                .initializeSchema(true)
                .build();
    }


    @Bean
    public ChatClient deepSeekClient(){
        DeepSeekApi deepSeekApi = DeepSeekApi.builder().apiKey(deepSeekAPIKey).build();
        ChatModel deepSeekChatModel = DeepSeekChatModel.builder().deepSeekApi(deepSeekApi).build();
        return ChatClient.builder(deepSeekChatModel).build();
    }

}
