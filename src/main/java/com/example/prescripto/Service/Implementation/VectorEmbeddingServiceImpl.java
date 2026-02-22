package com.example.prescripto.Service.Implementation;


import com.example.prescripto.Repo.VectorStoreRepository;
import com.example.prescripto.Service.EmbeddableDocument;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.UUID;

@Service
public class VectorEmbeddingServiceImpl {

    private final EmbeddingModel embeddingClient;
    private final VectorStoreRepository vectorRepo;
    private final ObjectMapper objectMapper;

    public VectorEmbeddingServiceImpl(
            EmbeddingModel embeddingClient,
            VectorStoreRepository vectorRepo,
            ObjectMapper objectMapper) {
        this.embeddingClient = embeddingClient;
        this.vectorRepo = vectorRepo;
        this.objectMapper = objectMapper;
    }

    public void embed(EmbeddableDocument document) {

        String content = document.buildEmbeddingText();

        float[] embedding = embeddingClient.embed(content);

        String vectorString = Arrays.toString(embedding);

        String metadataJson = "{}";
        try {
            metadataJson = objectMapper.writeValueAsString(document.buildMetadata());
        } catch (Exception ignored) {}

        vectorRepo.insertVector(
                UUID.randomUUID().toString(),
                document.getSourceType(),
                document.getSourceId(),
                content,
                metadataJson,
                vectorString
        );
    }

    public void updateMetadata(EmbeddableDocument document) {
        String metadataJson = "{}";
        try {
            metadataJson = objectMapper.writeValueAsString(document.buildMetadata());
        } catch (Exception e) {
            // log or ignore; keep existing metadata if serialization fails
            System.err.println("Failed to serialize metadata for update: " + e.getMessage());
            return;
        }

        vectorRepo.updateMetadataBySource(
                document.getSourceType(),
                document.getSourceId(),
                metadataJson
        );
    }

    public void deleteEmbedding(String appointmentId) {
        vectorRepo.deleteBySource(appointmentId);
    }
}
