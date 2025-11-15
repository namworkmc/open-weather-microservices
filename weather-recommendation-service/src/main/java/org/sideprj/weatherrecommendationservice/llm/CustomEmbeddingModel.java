package org.sideprj.weatherrecommendationservice.llm;

import java.util.List;
import java.util.Optional;

import org.springframework.ai.chat.metadata.DefaultUsage;
import org.springframework.ai.chat.metadata.EmptyUsage;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.embedding.EmbeddingResponseMetadata;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Primary
@Slf4j
@Component
@RequiredArgsConstructor
// TODO: to be removed once OpenAI support asymmetric embedding model
public class CustomEmbeddingModel implements EmbeddingModel {

    @Value("${spring.ai.openai.embedding.options.model}")
    private String model;

    private final OpenAiEmbeddingApiClient openAiEmbeddingApiClient;

    @Override
    public EmbeddingResponse call(EmbeddingRequest request) {
        var embeddingRequest = createRequest(request);
        return Optional.ofNullable(openAiEmbeddingApiClient.getEmbedding(embeddingRequest))
                .map(apiEmbeddingResponse -> {
                    OpenAiApi.Usage usage = apiEmbeddingResponse.usage();
                    Usage embeddingResponseUsage = usage != null ? getDefaultUsage(usage) : new EmptyUsage();
                    var metadata = new EmbeddingResponseMetadata(apiEmbeddingResponse.model(), embeddingResponseUsage);

                    List<Embedding> embeddings = apiEmbeddingResponse.data()
                            .stream()
                            .map(e -> new Embedding(e.embedding(), e.index()))
                            .toList();

                    return new EmbeddingResponse(embeddings, metadata);
                })
                .orElseGet(() -> {
                    log.warn("No embeddings returned for request: {}", embeddingRequest);
                    return new EmbeddingResponse(List.of());
                });
    }

    @Override
    public float[] embed(Document document) {
        return embed(document.getFormattedContent(MetadataMode.EMBED));
    }

    private CustomEmbeddingRequest<List<String>> createRequest(EmbeddingRequest request) {
        return new CustomEmbeddingRequest<>(
                request.getInstructions(),
                model,
                null,
                null,
                null,
                CustomEmbeddingRequest.EmbeddingInputType.QUERY.getValue()
        );
    }

    private DefaultUsage getDefaultUsage(OpenAiApi.Usage usage) {
        return new DefaultUsage(usage.promptTokens(), usage.completionTokens(), usage.totalTokens(), usage);
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record CustomEmbeddingRequest<T>(
            @JsonProperty("input") T input,
            @JsonProperty("model") String model,
            @JsonProperty("encoding_format") String encodingFormat,
            @JsonProperty("dimensions") Integer dimensions,
            @JsonProperty("user") String user,
            @JsonProperty("input_type") String inputType
    ) {
        @AllArgsConstructor
        @Getter
        public enum EmbeddingInputType {
            QUERY("query"),
            PASSAGE("passage");

            private final String value;
        }
    }
}
