package com.dramalog.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dramalog.dto.ReviewCreateRequest;
import com.dramalog.dto.ReviewResponse;
import com.dramalog.model.Review;
import com.dramalog.model.User;
import com.dramalog.repository.UserRepository;
import com.dramalog.service.ReviewService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api")
public class ReviewController {
	private final ReviewService reviewService;
    private final UserRepository userRepo; 
	
	public ReviewController(ReviewService reviewService, UserRepository userRepo ) {
		this.reviewService = reviewService;
		this.userRepo = userRepo;
	}
	
	// 홈: 나의 리뷰 목록 (userID를 url로 받는 버전)
	@GetMapping("/reviews/by-user/{userID}")
	public List<Review> getReviewsByUser(@PathVariable("userID") Integer userID) {
		return reviewService.getReviewsByUser(userID);
	}
	
	// 홈: 나의 리뷰 목록 (currentUser 버전, 현재 로그인된 유저의 리뷰를 가져온다.)
	@GetMapping("/reviews/me")
	public List<Review> myReviews(HttpSession session) {
	    User me = (User) session.getAttribute("currentUser");
	    return reviewService.getReviewsByUser(me.getUserID());
	}
	
	//상세페이지: 회차별 리뷰 개수 
	@GetMapping("/dramas/{dramaID}/review-counts")
	public List<Object[]> getEpisodeReviewCounts(@PathVariable Integer dramaID) {
	    return reviewService.getEpisodeCounts(dramaID);
	}
	
	// 상세페이지: 전체 리뷰
	@GetMapping("/dramas/{dramaID}/reviews")
	public List<Review> getDramaReviews(
			@PathVariable Integer dramaID,
			@RequestParam(required = false) Integer episodeSelected) { // url에서는 ?episodeSelected=3 같이 나타난다.
		if (episodeSelected == null) {
			return reviewService.getOverallReviews(dramaID);
		}
		return reviewService.getEpisodeReviews(dramaID, episodeSelected);
	}
	
	// 리뷰 작성
	@PostMapping("/dramas/{dramaID}/reviews")
	public ReviewResponse createReview(
	        @PathVariable Integer dramaID,
	        @RequestBody ReviewCreateRequest request,
	        HttpSession session
	) {
	    ReviewResponse res = reviewService.createReview(dramaID, request);

	    // DB에서 최신 유저 다시 조회
	    User refreshed = userRepo.findById(request.getUserID()).orElse(null);
	    if (refreshed != null) {
	        session.setAttribute("currentUser", refreshed);
	    }

	    return res;
	}
	
	// 리뷰 삭제 
	@DeleteMapping("/reviews/{reviewId}")
	public String deleteMyReview(@PathVariable Integer reviewId, HttpSession session) {
	    User me = (User) session.getAttribute("currentUser");
	    if (me == null) throw new IllegalStateException("로그인이 필요합니다.");

	    reviewService.deleteMyReview(reviewId, me.getUserID());

	    // 세션 유저 갱신 (레벨 변경 반영)
	    User refreshed = userRepo.findById(me.getUserID()).orElse(null);
	    if (refreshed != null) session.setAttribute("currentUser", refreshed);

	    return "ok";
	}
}
