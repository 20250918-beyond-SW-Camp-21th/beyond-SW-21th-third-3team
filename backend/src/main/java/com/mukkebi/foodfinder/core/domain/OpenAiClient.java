package com.mukkebi.foodfinder.core.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mukkebi.foodfinder.core.api.config.OpenAiProperties;
import com.mukkebi.foodfinder.core.api.controller.v1.response.AiRecommendResponse;
import com.mukkebi.foodfinder.core.support.error.CoreException;
import com.mukkebi.foodfinder.core.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenAiClient {

    private final WebClient openAiWebClient;
    private final OpenAiProperties openAiProperties;
    private final ObjectMapper objectMapper;

    private static final int MAX_RETRY = 2;

    public AiRecommendResponse requestRecommendation(String prompt) {
        for (int attempt = 1; attempt <= MAX_RETRY; attempt++) {
            try {
                String response = callOpenAi(prompt);
                return parseResponse(response);
            } catch (CoreException e) {
                if (attempt == MAX_RETRY) {
                    throw e;
                }
                log.warn("AI 응답 파싱 실패, 재시도 중... (시도 {}/{})", attempt, MAX_RETRY);
            }
        }
        throw new CoreException(ErrorType.DEFAULT_ERROR, "AI 응답 파싱에 실패했습니다.");
    }

    private String callOpenAi(String prompt) {
        Map<String, Object> requestBody = Map.of(
                "model", openAiProperties.model(),
                "messages", List.of(
                        Map.of("role", "system", "content", "당신은 음식점 추천 전문가입니다. 반드시 JSON 형식으로만 응답하세요."),
                        Map.of("role", "user", "content", prompt)
                ),
                "temperature", 0.7,
                "max_tokens", 500
        );

        try {
            String response = openAiWebClient.post()
                    .uri("/v1/chat/completions")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode root = objectMapper.readTree(response);
            return root.path("choices").get(0).path("message").path("content").asText();
        } catch (Exception e) {
            log.error("OpenAI API 호출 실패: {}", e.getMessage());
            throw new CoreException(ErrorType.DEFAULT_ERROR, "AI 서비스 호출에 실패했습니다.");
        }
    }

    private AiRecommendResponse parseResponse(String content) {
        try {
            String jsonContent = extractJson(content);
            return objectMapper.readValue(jsonContent, AiRecommendResponse.class);
        } catch (Exception e) {
            log.error("AI 응답 파싱 실패: {}", content);
            throw new CoreException(ErrorType.DEFAULT_ERROR, "AI 응답 파싱에 실패했습니다.");
        }
    }

    private String extractJson(String content) {
        content = content.trim();
        if (content.startsWith("```json")) {
            content = content.substring(7);
        }
        if (content.startsWith("```")) {
            content = content.substring(3);
        }
        if (content.endsWith("```")) {
            content = content.substring(0, content.length() - 3);
        }
        return content.trim();
    }
}
