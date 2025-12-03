package org.sideprj.weatherrecommendationservice.llm;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class VectorStoreAspect {

    private final VectorStore vectorStore;

    @AfterReturning(
            pointcut = "@annotation(org.sideprj.weatherrecommendationservice.llm.EnabledStoreVector)",
            returning = "result"
    )
    public void storeVector(JoinPoint jp, Object result) {
        if (result == null) {
            return;
        }

        String text = result.toString();

        // Find annotation
        MethodSignature signature = (MethodSignature) jp.getSignature();
        Method method = signature.getMethod();
        EnabledStoreVector annotation = method.getAnnotation(EnabledStoreVector.class);

        // Build metadata from method arguments based on annotation metaKeys
        Map<String, Object> metadata = extractMetadata(annotation, jp.getArgs());

        vectorStore.add(List.of(new Document(text, metadata)));
        log.debug("Added document to vector store: {}", text);
    }

    private Map<String, Object> extractMetadata(EnabledStoreVector annotation, Object[] args) {
        Map<String, Object> metadata = new HashMap<>();

        String[] metaKeys = annotation.metadata();
        if (metaKeys.length == 0) {
            return metadata; // no metadata mapping requested
        }

        for (Object arg : args) {
            if (arg == null) {
                continue;
            }

            // Look for getters matching the metaKeys
            for (String key : metaKeys) {
                try {
                    Method getter = arg.getClass()
                            .getMethod("get" + Character.toUpperCase(key.charAt(0)) + key.substring(1));
                    Object value = getter.invoke(arg);
                    if (value != null) {
                        metadata.put(key, value);
                    }
                } catch (Exception ignored) {
                    log.warn("Failed to extract metadata from argument: {}", arg);
                }
            }
        }

        return metadata;
    }
}
