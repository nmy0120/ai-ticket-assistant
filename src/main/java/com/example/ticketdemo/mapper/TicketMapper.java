package com.example.ticketdemo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ticketdemo.entity.Ticket;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TicketMapper extends BaseMapper<Ticket> {
}