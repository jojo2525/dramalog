package com.dramalog.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.dramalog.dto.BookmarkDramaResponse;
import com.dramalog.model.Bookmark;
import com.dramalog.model.Drama;
import com.dramalog.model.User;
import com.dramalog.repository.BookmarkRepository;
import com.dramalog.repository.DramaRepository;
import com.dramalog.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class BookmarkService {

    private final BookmarkRepository bookmarkRepo;
    private final DramaRepository dramaRepo;
    private final UserRepository userRepo;

    public BookmarkService(BookmarkRepository bookmarkRepo, DramaRepository dramaRepo, UserRepository userRepo) {
        this.bookmarkRepo = bookmarkRepo;
        this.dramaRepo = dramaRepo;
        this.userRepo = userRepo;
    }

    // 내 찜 목록 (드라마 카드용)
    public List<BookmarkDramaResponse> getMyBookmarks(Integer userId) {
        List<Bookmark> list = bookmarkRepo.findByUser_UserID(userId);

        return list.stream()
                .map(b -> {
                    Drama d = b.getDrama();
                    return new BookmarkDramaResponse(
                            d.getDramaID(),
                            d.getTitle(),
                            d.getGenre(),
                            d.getCoverImage(),
                            d.getAvgRating()
                    );
                })
                .collect(Collectors.toList());
    }

    // 특정 드라마 찜 여부
    public boolean isBookmarked(Integer userId, Integer dramaId) {
        return bookmarkRepo.existsByUser_UserIDAndDrama_DramaID(userId, dramaId);
    }

    // 토글(찜 추가/해제)
    @Transactional
    public boolean toggle(Integer userId, Integer dramaId) {
        boolean exists = bookmarkRepo.existsByUser_UserIDAndDrama_DramaID(userId, dramaId);

        if (exists) {
            bookmarkRepo.deleteByUser_UserIDAndDrama_DramaID(userId, dramaId);
            return false;
        }

        User u = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Drama d = dramaRepo.findById(dramaId)
                .orElseThrow(() -> new IllegalArgumentException("Drama not found"));

        Bookmark b = new Bookmark();
        b.setUser(u);
        b.setDrama(d);
        bookmarkRepo.save(b);

        return true;
    }
}