package org.an.springai.util;

import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentTransformer;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;


import java.util.List;

public class VectorUtil {
    public static void ingestDocToVectorStore(VectorStore vectorStore, DocumentTransformer documentTransformer, Resource resource){
        TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(resource);
        List<Document> documents = tikaDocumentReader.get();
        List<Document> transformedDocs = documentTransformer.apply(documents);
        vectorStore.accept(transformedDocs);
        System.out.println(resource.getFilename() +" is successfully ingested to " + vectorStore.getName());
    }

}
