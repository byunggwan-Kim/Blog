package com.sparta.blog.controller;

import com.sparta.blog.dto.LoginRequestDto;
import com.sparta.blog.dto.MsgResponseDto;
import com.sparta.blog.dto.SignupRequestDto;
import com.sparta.blog.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/user/login")
    public MsgResponseDto login(@RequestBody LoginRequestDto requestDto, HttpServletResponse res) {
        try {
            userService.login(requestDto, res);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new MsgResponseDto("로그인 성공", 200);
    }

    @PostMapping("/user/signup")
    public MsgResponseDto signup(@RequestBody SignupRequestDto requestDto) {
        userService.signup(requestDto);
        return new MsgResponseDto("회원가입 성공", 200);
    }
}