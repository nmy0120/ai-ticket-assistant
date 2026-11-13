package com.example.ticketdemo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ticketdemo.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}