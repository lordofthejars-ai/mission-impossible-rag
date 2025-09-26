package org.acme.rag;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import io.quarkiverse.langchain4j.redis.RedisEmbeddingStore;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.json.JsonCommands;
import io.quarkus.redis.datasource.keys.KeyCommands;
import io.smallrye.common.annotation.Identifier;
import io.vertx.core.json.JsonArray;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;


import java.util.List;

import static dev.langchain4j.data.document.splitter.DocumentSplitters.recursive;

@ApplicationScoped
public class MissionIngestor {

    @ConfigProperty(name = "rag.anonymize", defaultValue = "false")
    boolean anonymize;

    @Inject
    //@Identifier("encryption")
    EmbeddingModel embeddingModel;

    @Inject
    RedisEmbeddingStore store;

    @Inject
    PIIDocumentTransformer transformer;

    @Inject
    Logger logger;
    
    public void ingest(Document documents) {
        
        logger.info("Ingesting Document");

        EmbeddingStoreIngestor.Builder builder = EmbeddingStoreIngestor.builder()
                .embeddingStore(store)
                .embeddingModel(embeddingModel)
                .documentSplitter(recursive(500, 50));

        if (anonymize) {
            
            logger.info("Anonymizing Document");

            builder.documentTransformer(transformer);
        }

        EmbeddingStoreIngestor ingestor = builder.build();

        logger.info("Ingesting Document");

        ingestor.ingest(documents);

        printEmbeddings();

    }

    public MissionIngestor(RedisDataSource ds) {
        docs = ds.json();
        keys = ds.key();
    }

    JsonCommands<String> docs;
    KeyCommands<String> keys;

    private void printEmbeddings() {
        keys.keys("embedding*")
                .stream()
                .map(k -> docs.jsonGetObject(k))
                .forEach(o -> {
                    System.out.println("*********************************");
                    System.out.println(o.getString("scalar"));
                    System.out.println("Metadata: id: " + o.getString("id"));
                    JsonArray vector = o.getJsonArray("vector");
                    List<Object> vectors = vector.stream()
                            .limit(5)
                            .toList();
                    System.out.println(vectors);

                    System.out.println("*********************************");
                    System.out.println("*********************************");
                });
    }

}
