package org.sideprj.weatherrecommendationservice.llm;

import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = OpenAiEmbeddingApiClient.NAME, url = "${spring.ai.openai.embedding.base-url}")
public interface OpenAiEmbeddingApiClient {

    String NAME = "OpenAiEmbeddingApiClient";

    @PostMapping("/v1/embeddings")
    <T> OpenAiApi.EmbeddingList<OpenAiApi.Embedding> getEmbedding(CustomEmbeddingModel.CustomEmbeddingRequest<T> request);
}
