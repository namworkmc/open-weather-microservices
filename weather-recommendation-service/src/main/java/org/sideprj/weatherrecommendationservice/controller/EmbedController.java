package org.sideprj.weatherrecommendationservice.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.ai.chat.client.ChatClient;
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
        String poem = String.format("Create a poem with this male name %s", message);
        Optional<String> content = Optional.ofNullable(chatClient.prompt(poem)
                .call()
                .content());

        content.ifPresent(c -> vectorStore.add(List.of(new Document(c))));
        return ResponseEntity.of(content);
    }

    @GetMapping
    public List<String> search(@RequestParam String query) {
        SearchRequest request = SearchRequest.builder()
                .query(query)
                .build();
        return vectorStore.similaritySearch(request)
                .stream()
                .map(Document::getText)
                .toList();
    }
}
