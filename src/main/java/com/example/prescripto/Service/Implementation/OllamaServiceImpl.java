package com.example.prescripto.Service.Implementation;


import com.example.prescripto.DTO.DoctorAboutDTO;
import com.example.prescripto.Service.EmbeddableDocument;
import jakarta.annotation.PostConstruct;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

@Service
public class OllamaServiceImpl {

     @Autowired
     private ChatClient chatClient;

     @Autowired
     private EmbeddingModel embeddingClient;

     @Autowired
     private ResourceLoader resourceLoader;

     @Autowired
     private VectorStore vectorStore;


    @Autowired
        public OllamaServiceImpl(ChatClient chatClient) {
            this.chatClient = chatClient;
        }

     public String getAbout(DoctorAboutDTO doctorAboutDTO) {

           String template = "Generate a brief about for a doctor with the following details:\n" +
                     "Name: {name}\n" +
                        "Speciality: {speciality}\n" +
                        "Experience: {experience}\n" +
                        "Degree: {degree}\n" +
                        "The about should be concise limit to 200 characters and keep it informative.";

         PromptTemplate promptTemplate = new PromptTemplate(template);

         Map<String,Object> variables = Map.of(
                    "name", doctorAboutDTO.getName(),
                    "speciality", doctorAboutDTO.getSpeciality(),
                    "experience", doctorAboutDTO.getExperience(),
                    "degree", doctorAboutDTO.getDegree()
            );

         Prompt prompt = promptTemplate.create(variables);

         return chatClient.prompt(prompt).call().content();
     }

    private String loadPrompt(String path) throws IOException {
        try (InputStream inputStream =
                     resourceLoader.getResource("classpath:" + path).getInputStream()) {

            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }



    public String chat(String question) throws IOException {


        String template = loadPrompt("prompts/chatbot-rag-prompt.st");

//        String template = Files.readString(
//                resourceLoader.getResource("classpath:prompts/chatbot-rag-prompt.st")
//                        .getFile()
//                        .toPath()
//        );

        System.out.println("Received question: " + question);
        String context = fetchSemanticContext(question);

        PromptTemplate promptTemplate = new PromptTemplate(template);

        Map<String, Object> variables = Map.of(
                "userQuery", question,
                "context", context
        );

        Prompt prompt = promptTemplate.create(variables);

            return chatClient.prompt(prompt).call().content();
    }


    private String fetchSemanticContext(String question) {

          List<Document> documents= vectorStore.similaritySearch(SearchRequest.builder()
                  .topK(2)
                  .similarityThreshold(0.3)
                  .query(question)
                  .build());
        System.out.println("RAG found " + documents.size() + " documents for query: " + question);

        StringBuilder contextBuilder = new StringBuilder();

            for(Document doc : documents){
                    contextBuilder.append(doc.getFormattedContent()).append("\n");
            }

          return contextBuilder.toString();
    }
}
