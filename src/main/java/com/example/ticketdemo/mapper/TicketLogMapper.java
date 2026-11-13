package com.example.ticketdemo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ticketdemo.entity.TicketLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TicketLogMapper extends BaseMapper<TicketLog> {
}