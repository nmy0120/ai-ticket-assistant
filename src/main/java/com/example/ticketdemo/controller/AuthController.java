package com.example.ticketdemo.controller;

import com.example.ticketdemo.common.BizException;
import com.example.ticketdemo.common.Result;
import com.example.ticketdemo.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new BizException("用户名和密码不能为空");
        }
        return Result.ok(authService.login(username, password));
    }
}