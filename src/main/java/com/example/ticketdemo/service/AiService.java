package com.example.ticketdemo.service;

import com.example.ticketdemo.common.BizException;
import com.example.ticketdemo.entity.AiResult;
import com.example.ticketdemo.entity.Ticket;
import com.example.ticketdemo.mapper.TicketMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.util.Set;

@Service
public class AiService {

    @Autowired
    private TicketMapper ticketMapper;

    @Autowired
    private LlmClient llmClient;

    private final ObjectMapper objectMapper = JsonMapper.builder().build();

    private static final Set<String> CATEGORIES =
            Set.of("HARDWARE", "NETWORK", "SOFTWARE", "OTHER");

    public AiResult classify(Long ticketId) {
        Ticket ticket = ticketMapper.selectById(ticketId);
        if (ticket == null) {
            throw new BizException("工单不存在");
        }

        String prompt = "标题：" + ticket.getTitle()
                + "\n描述：" + ticket.getDescription()
                + "\n只输出 JSON：{\"category\":\"HARDWARE|NETWORK|SOFTWARE|OTHER\",\"summary\":\"50字以内\"}";

        for (int i = 0; i < 2; i++) {
            try {
                String text = llmClient.chat(prompt);
                String json = extractJson(text);
                AiResult result = objectMapper.readValue(json, AiResult.class);
                if (result.getCategory() != null && CATEGORIES.contains(result.getCategory())) {
                    return result;
                }
            } catch (Exception e) {
                // 重试一次；仍失败走兜底
            }
        }
        return fallback(ticket);
    }

    private String extractJson(String text) {
        int left = text.indexOf('{');
        int right = text.lastIndexOf('}');
        if (left < 0 || right <= left) {
            throw new RuntimeException("no json");
        }
        return text.substring(left, right + 1);
    }

    private AiResult fallback(Ticket ticket) {
        String text = (ticket.getTitle() + " " + ticket.getDescription()).toLowerCase();
        AiResult result = new AiResult();
        if (text.contains("hdmi") || text.contains("投影") || text.contains("黑屏")) {
            result.setCategory("HARDWARE");
        } else if (text.contains("网") || text.contains("wifi") || text.contains("连接")) {
            result.setCategory("NETWORK");
        } else if (text.contains("软件") || text.contains("系统") || text.contains("驱动")) {
            result.setCategory("SOFTWARE");
        } else {
            result.setCategory("OTHER");
        }
        result.setSummary(ticket.getTitle() + "（规则兜底）");
        return result;
    }
}