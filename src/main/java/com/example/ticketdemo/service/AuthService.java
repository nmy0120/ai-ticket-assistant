package com.example.ticketdemo.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.ticketdemo.common.BizException;
import com.example.ticketdemo.entity.User;
import com.example.ticketdemo.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TokenService tokenService;

    public Map<String, Object> login(String username, String password) {
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, username)
        );
        if (user == null || !user.getPassword().equals(password)) {
            throw new BizException(401, "用户名或密码错误");
        }

        String token = tokenService.create(user.getId());

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("role", user.getRole());
        result.put("userId", user.getId());
        return result;
    }
}