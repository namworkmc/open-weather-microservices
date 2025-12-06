package org.sideprj.weatherrecommendationservice.controller;

import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/embed")
@RequiredArgsConstructor
public class EmbedController {

    private final VectorStore vectorStore;

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
