package com.example.ticketdemo.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenService {

    private final Map<String, Long> tokens = new ConcurrentHashMap<>();

    public String create(Long userId) {
        String token = UUID.randomUUID().toString().replace("-", "");
        tokens.put(token, userId);
        return token;
    }

    public Long getUserId(String token) {
        return tokens.get(token);
    }
}