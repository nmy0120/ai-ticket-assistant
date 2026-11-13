package com.example.ticketdemo.controller;

import com.example.ticketdemo.common.BizException;
import com.example.ticketdemo.common.Result;
import com.example.ticketdemo.entity.Ticket;
import com.example.ticketdemo.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import com.example.ticketdemo.entity.AiResult;
import com.example.ticketdemo.service.AiService;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private AiService aiService;

    private String getToken(String auth) {
        if (auth == null || !auth.startsWith("Bearer ")) {
            throw new BizException(401, "请先登录");
        }
        return auth.substring(7);
    }

    @PostMapping
    public Result<Ticket> create(@RequestBody Map<String, String> body,
                                 @RequestHeader(value = "Authorization", required = false) String auth) {
        return Result.ok(ticketService.create(getToken(auth), body.get("title"), body.get("description")));
    }

    @GetMapping
    public Result<List<Ticket>> list(@RequestParam(required = false) String status,
                                     @RequestHeader(value = "Authorization", required = false) String auth) {
        return Result.ok(ticketService.list(getToken(auth), status));
    }

    @PostMapping("/{id}/status")
    public Result<Ticket> changeStatus(@PathVariable Long id,
                                       @RequestBody Map<String, String> body,
                                       @RequestHeader(value = "Authorization", required = false) String auth) {
        return Result.ok(ticketService.changeStatus(getToken(auth), id, body.get("toStatus")));
    }

    @PostMapping("/{id}/ai")
    public Result<AiResult> ai(@PathVariable Long id,
                               @RequestHeader(value = "Authorization", required = false) String auth) {
        ticketService.currentUserId(getToken(auth));
        AiResult result = aiService.classify(id);
        ticketService.saveAiResult(id, result);
        return Result.ok(result);
    }
}