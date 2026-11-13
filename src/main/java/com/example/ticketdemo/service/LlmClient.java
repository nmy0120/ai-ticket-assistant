package com.example.ticketdemo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LlmClient {

    @Value("${llm.url}")
    private String url;

    @Value("${llm.model}")
    private String model;

    @Value("${llm.api-key:}")
    private String apiKey;

    private final RestClient restClient = RestClient.create();

    @SuppressWarnings("unchecked")
    public String chat(String prompt) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("LLM api-key 未配置");
        }

        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("temperature", 0.1);
        body.put("messages", List.of(
                Map.of("role", "system", "content", "你是工单分类器，只输出 JSON，不要解释。"),
                Map.of("role", "user", "content", prompt)
        ));

        Map<String, Object> response = restClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + apiKey)
                .body(body)
                .retrieve()
                .body(Map.class);

        List<Map<String, Object>> choices =
                (List<Map<String, Object>>) response.get("choices");
        Map<String, Object> message =
                (Map<String, Object>) choices.get(0).get("message");
        return (String) message.get("content");
    }
}