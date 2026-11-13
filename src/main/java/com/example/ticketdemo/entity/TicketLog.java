package com.example.ticketdemo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("ticket_log")
public class TicketLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long ticketId;
    private String action;
    private Long operatorId;
    private LocalDateTime createdAt;
}