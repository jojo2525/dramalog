package com.dramalog.controller;

import org.springframework.web.bind.annotation.*;
import com.dramalog.model.User;
import com.dramalog.service.AuthService;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    
    // 로그인
    @PostMapping("/login")
    public User login(
            @RequestBody LoginRequest req,
            HttpSession session
    ) {
        User user = authService.loginRegister(req.name(), req.pin4());
        session.setAttribute("currentUser", user); // AppState.setCurrentUser 대체
        return user;
    }
    
    // 로그인용 DTO
    public record LoginRequest(String name, String pin4) {}

    // 현재 유저 확인
    @GetMapping("/me")
    public User me(HttpSession session) {
        return (User) session.getAttribute("currentUser"); // 세션이 현재 유저 객체 반환
    }

    // 로그인 여부 확인
    @GetMapping("/is-logged-in")
    public boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("currentUser") != null;
    }

    // 로그아웃
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "logout ok";
    }
}
