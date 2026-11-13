package com.example.ticketdemo.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.ticketdemo.common.BizException;
import com.example.ticketdemo.entity.Ticket;
import com.example.ticketdemo.entity.TicketLog;
import com.example.ticketdemo.mapper.TicketLogMapper;
import com.example.ticketdemo.mapper.TicketMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import java.util.Map;
import java.util.Set;

import java.time.LocalDateTime;
import java.util.List;
import com.example.ticketdemo.entity.AiResult;

@Service
public class TicketService {

    @Autowired
    private TicketMapper ticketMapper;

    @Autowired
    private TicketLogMapper ticketLogMapper;

    @Autowired
    private TokenService tokenService;

    private static final Map<String, Set<String>> ALLOWED = Map.of(
            "PENDING", Set.of("PROCESSING", "CLOSED"),
            "PROCESSING", Set.of("DONE", "PENDING"),
            "DONE", Set.of("CLOSED"),
            "CLOSED", Set.of()
    );

    public Long currentUserId(String token) {
        Long userId = tokenService.getUserId(token);
        if (userId == null) {
            throw new BizException(401, "请先登录");
        }
        return userId;
    }

    @Transactional
    public Ticket create(String token, String title, String description) {
        Long userId = currentUserId(token);
        if (title == null || title.isBlank()) {
            throw new BizException("标题不能为空");
        }

        Ticket ticket = new Ticket();
        ticket.setUserId(userId);
        ticket.setTitle(title);
        ticket.setDescription(description);
        ticket.setStatus("PENDING");
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setUpdatedAt(LocalDateTime.now());
        ticketMapper.insert(ticket);

        TicketLog log = new TicketLog();
        log.setTicketId(ticket.getId());
        log.setAction("CREATE");
        log.setOperatorId(userId);
        log.setCreatedAt(LocalDateTime.now());
        ticketLogMapper.insert(log);

        return ticket;
    }

    public List<Ticket> list(String token, String status) {
        currentUserId(token);
        LambdaQueryWrapper<Ticket> query = new LambdaQueryWrapper<>();
        if (status != null && !status.isBlank()) {
            query.eq(Ticket::getStatus, status);
        }
        query.orderByDesc(Ticket::getCreatedAt);
        return ticketMapper.selectList(query);
    }

    @Transactional
    public Ticket changeStatus(String token, Long id, String toStatus) {
        Long userId = currentUserId(token);

        Ticket ticket = ticketMapper.selectById(id);
        if (ticket == null) {
            throw new BizException("工单不存在");
        }

        Set<String> allowed = ALLOWED.getOrDefault(ticket.getStatus(), Set.of());
        if (!allowed.contains(toStatus)) {
            throw new BizException("不允许从 " + ticket.getStatus() + " 转到 " + toStatus);
        }

        int rows = ticketMapper.update(null,
                new LambdaUpdateWrapper<Ticket>()
                        .eq(Ticket::getId, id)
                        .eq(Ticket::getStatus, ticket.getStatus())
                        .set(Ticket::getStatus, toStatus)
                        .set(Ticket::getUpdatedAt, LocalDateTime.now())
        );

        if (rows == 0) {
            throw new BizException("工单状态已变化，请刷新");
        }

        TicketLog log = new TicketLog();
        log.setTicketId(id);
        log.setAction(ticket.getStatus() + "->" + toStatus);
        log.setOperatorId(userId);
        log.setCreatedAt(LocalDateTime.now());
        ticketLogMapper.insert(log);

        return ticketMapper.selectById(id);
    }
    public void saveAiResult(Long id, AiResult result) {
        ticketMapper.update(null,
                new LambdaUpdateWrapper<Ticket>()
                        .eq(Ticket::getId, id)
                        .set(Ticket::getCategory, result.getCategory())
                        .set(Ticket::getSummary, result.getSummary())
                        .set(Ticket::getUpdatedAt, LocalDateTime.now())
        );
    }
}