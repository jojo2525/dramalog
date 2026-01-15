package com.dramalog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.ui.Model;

import com.dramalog.model.User;
import com.dramalog.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class PageController {

    private final UserRepository userRepo;

    public PageController(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    // 홈
    @GetMapping("/")
    public String home(HttpSession session, Model model) {

        User sessionUser = (User) session.getAttribute("currentUser");

        // 로그인 안 되어 있으면 기본값
        if (sessionUser == null) {
            model.addAttribute("userName", "홍길동");
            model.addAttribute("level", 1);
            return "home";
        }

        //  DB에서 최신 유저 다시 조회 (level 최신화)
        User fresh = userRepo.findById(sessionUser.getUserID())
                .orElse(sessionUser);

        //  세션도 최신 객체로 교체
        session.setAttribute("currentUser", fresh);

        String name = (fresh.getName() != null && !fresh.getName().isBlank())
                ? fresh.getName()
                : "홍길동";

        int level = (fresh.getLevel() != null) ? fresh.getLevel() : 1;
        if (level < 1) level = 1;
        if (level > 3) level = 3;

        model.addAttribute("userName", name);
        model.addAttribute("level", level);

        return "home";
    }

    @GetMapping("/drama/{dramaId}")
    public String dramaDetailPage(@PathVariable Integer dramaId, Model model) {
        model.addAttribute("dramaId", dramaId);
        return "drama-detail";
    }

    @GetMapping("/review/new/{dramaId}")
    public String reviewNew(@PathVariable Integer dramaId, Model model) {
        model.addAttribute("dramaId", dramaId);
        return "review-new";
    }
    
    // 채팅방 테스트용: 나중에 지우기!
    @GetMapping("/test/chat")
    public String testChat() {
        return "test-chat";
    }

}