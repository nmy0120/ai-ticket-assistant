package com.example.ticketdemo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("ticket")
public class Ticket {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private String title;
    private String description;
    private String status;
    private Long assigneeId;
    private String category;
    private String summary;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}