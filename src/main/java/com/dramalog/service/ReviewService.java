package com.dramalog.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.dramalog.dto.ReviewCreateRequest;
import com.dramalog.dto.ReviewResponse;
import com.dramalog.model.Drama;
import com.dramalog.model.Review;
import com.dramalog.model.User;
import com.dramalog.repository.DramaRepository;
import com.dramalog.repository.ReviewRepository;
import com.dramalog.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepo;
    private final DramaRepository dramaRepo;
    private final UserRepository userRepo;
    private final DramaService dramaService;

    public ReviewService(ReviewRepository reviewRepo,
                         DramaRepository dramaRepo,
                         UserRepository userRepo,
                         DramaService dramaService) {
        this.reviewRepo = reviewRepo;
        this.dramaRepo = dramaRepo;
        this.userRepo = userRepo;
        this.dramaService = dramaService;
    }

    // 홈: 나의 리뷰 목록
    public List<Review> getReviewsByUser(Integer userID) {
        return reviewRepo.findByUserIDOrderByCreatedAtDesc(userID);
    }

    // 상세페이지: 전체 리뷰
    public List<Review> getOverallReviews(Integer dramaID) {
        return reviewRepo.findOverallByDramaSorted(dramaID);
    }

    // 상세페이지: 회차별 리뷰
    public List<Review> getEpisodeReviews(Integer dramaID, Integer episodeSelected) {
        return reviewRepo.findEpisodeByDramaSorted(dramaID, episodeSelected);
    }

    // 등록된 회차별 리뷰 개수
    public List<Object[]> getEpisodeCounts(Integer dramaID) {
        return reviewRepo.countByEpisode(dramaID);
    }

    // 리뷰 작성
    @Transactional
    public ReviewResponse createReview(Integer dramaID, ReviewCreateRequest req) {

        Drama drama = dramaRepo.findById(dramaID)
                .orElseThrow(() -> new IllegalArgumentException("드라마가 존재하지 않습니다."));

        User user = userRepo.findById(req.getUserID())
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));

        Integer ep = req.getEpisodeSelected();
        BigDecimal rating = req.getRating();

        if (ep != null) {
            int total = drama.getEpisodeCount();
            if (ep <= 0 || ep > total) {
                throw new IllegalArgumentException("회차 범위가 올바르지 않습니다.");
            }
        }

        if (ep == null && rating == null) {
            throw new IllegalArgumentException("전체 리뷰에는 별점이 필수입니다.");
        }
        if (rating != null) {
            if (rating.compareTo(BigDecimal.ZERO) < 0 || rating.compareTo(new BigDecimal("5")) > 0) {
                throw new IllegalArgumentException("별점 범위가 올바르지 않습니다.");
            }
        }

        Integer watchStatus = req.getWatchStatus();
        if (watchStatus == null || watchStatus < 0 || watchStatus > 2) {
            throw new IllegalArgumentException("시청 상태 값이 올바르지 않습니다.");
        }

        Review review = new Review();
        review.setDramaID(drama.getDramaID());
        review.setUserID(user.getUserID());
        review.setEpisodeSelected(ep);
        review.setRating(rating);
        review.setWatchStatus(watchStatus);
        review.setContent(req.getContent());

        Review saved = reviewRepo.save(review);

        // 1) 유저 레벨 먼저 갱신 + 저장 (refreshAvgRating가 clear쳐도 안전)
        long cnt = reviewRepo.countByUserID(saved.getUserID());

        int newLevel;
        if (cnt >= 10) newLevel = 3;
        else if (cnt >= 1) newLevel = 2;
        else newLevel = 1;

        user.setLevel(newLevel);
        userRepo.save(user);  // ✅ 이 줄이 핵심

        // 2) 전체 리뷰만 평균 별점 갱신
        if (saved.getEpisodeSelected() == null) {
            reviewRepo.refreshAvgRating(saved.getDramaID());
        }

        return new ReviewResponse(
                saved.getReviewID(),
                saved.getDramaID(),
                saved.getUserID(),
                saved.getEpisodeSelected(),
                saved.getRating(),
                saved.getWatchStatus(),
                saved.getContent()
        );
    }


    @Transactional
    public void deleteMyReview(Integer reviewId, Integer currentUserId) {
        Review review = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰가 존재하지 않습니다."));

        // 내 리뷰인지 체크
        if (review.getUserID() != currentUserId) {
            throw new IllegalArgumentException("본인 리뷰만 삭제할 수 있습니다.");
        }

        Integer dramaId = review.getDramaID();
        boolean isOverall = (review.getEpisodeSelected() == null);

        reviewRepo.delete(review);

     // 전체 리뷰 삭제면 평균 별점 다시 갱신
        if (isOverall) {
            reviewRepo.refreshAvgRating(dramaId);
        }

        // 삭제 후 유저 레벨 재계산
        long cnt = reviewRepo.countByUserID(currentUserId);

        int newLevel;
        if (cnt >= 10) newLevel = 3;
        else if (cnt >= 1) newLevel = 2;
        else newLevel = 1;

        User u = userRepo.findById(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));
        u.setLevel(newLevel);
        userRepo.save(u);
    }
}