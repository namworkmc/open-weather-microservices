package org.sideprj.weatherrecommendationservice.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/embed")
public class EmbedController {

    private final ChatClient chatClient;

    private final VectorStore vectorStore;

    public EmbedController(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) {
        chatClient = chatClientBuilder.build();
        this.vectorStore = vectorStore;
    }

    @PostMapping
    public ResponseEntity<String> test(@RequestBody String message) {
        Optional<String> content = Optional.ofNullable(chatClient.prompt(new Prompt(
                        new SystemPromptTemplate(
                                """
                                        Create a text for me to test embedding model, query feature.
                                        Write a marketing description for the content i provide you.
                                        You should only response your result without some thing like "here is the result: ".
                                        I want you to response as plain text so i can store in my vector database for retrieval purposes later
                                        Here is the content i want you to generate: {message}
                                        """
                        )
                                .createMessage(Map.of("message", message))
                ))
                .call()
                .content());

        content.ifPresent(c -> vectorStore.add(List.of(new Document(c))));
        return ResponseEntity.of(content);
    }

    @GetMapping
    public List<String> search(@RequestParam String query) {
        SearchRequest request = SearchRequest.builder()
                .query(query)
                .topK(3)
                .build();
        return vectorStore.similaritySearch(request)
                .stream()
                .map(Document::getText)
                .toList();
    }
}
