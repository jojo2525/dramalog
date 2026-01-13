package com.dramalog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    // 로그인 페이지
    @GetMapping("/login")
    public String loginPage() {
        return "login"; 
        // -> templates/login.html
    }

    // 홈 (임시!!)
    @GetMapping("/")
    public String home() {
        return "login"; 
        // 나중에 home.html 만들면 "home"으로 변경하기.
    }
}