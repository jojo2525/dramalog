package com.dramalog.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.dramalog.dto.BookmarkDramaResponse;
import com.dramalog.model.User;
import com.dramalog.service.BookmarkService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/bookmarks")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    public BookmarkController(BookmarkService bookmarkService) {
        this.bookmarkService = bookmarkService;
    }

    private Integer requireLogin(HttpSession session) {
        User u = (User) session.getAttribute("currentUser");
        return (u == null) ? null : u.getUserID();
    }

    // 찜 목록
    @GetMapping("/me")
    public ResponseEntity<?> myBookmarks(HttpSession session) {
        Integer userId = requireLogin(session);
        if (userId == null) return ResponseEntity.status(401).body("로그인이 필요합니다.");

        List<BookmarkDramaResponse> list = bookmarkService.getMyBookmarks(userId);
        return ResponseEntity.ok(list);
    }

    // 상세페이지: 찜 여부
    @GetMapping("/{dramaId}/exists")
    public ResponseEntity<?> exists(@PathVariable Integer dramaId, HttpSession session) {
        Integer userId = requireLogin(session);
        if (userId == null) return ResponseEntity.status(401).body(false);

        boolean bookmarked = bookmarkService.isBookmarked(userId, dramaId);
        return ResponseEntity.ok(bookmarked);
    }

    // 상세페이지: 토글
    @PostMapping("/{dramaId}/toggle")
    public ResponseEntity<?> toggle(@PathVariable Integer dramaId, HttpSession session) {
        Integer userId = requireLogin(session);
        if (userId == null) return ResponseEntity.status(401).body("로그인이 필요합니다.");

        boolean bookmarked = bookmarkService.toggle(userId, dramaId);
        return ResponseEntity.ok(Map.of("bookmarked", bookmarked));
    }
}
