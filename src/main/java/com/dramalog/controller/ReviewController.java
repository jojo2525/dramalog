package com.dramalog.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.dramalog.model.Review;
import com.dramalog.service.ReviewService;

@RestController
@RequestMapping("/api")
public class ReviewController {
	private final ReviewService reviewService;
	
	public ReviewController(ReviewService reviewService) {
		this.reviewService = reviewService;
	}
	
	// 홈: 나의 리뷰 목록 (userID를 url로 받는 버전)
	@GetMapping("/reviews/by-user/{userID}")
	public List<Review> getReviewsByUser(@PathVariable("userID") Integer userID) {
		return reviewService.getReviewsByUser(userID);
	}
	
	// 홈: 나의 리뷰 목록 (currentUser 버전, 현재 로그인된 유저의 리뷰를 가져온다.)
	/*
	@GetMapping("/reviews/me")
	public List<Review> myReviews(HttpSession session) {
	    User me = (User) session.getAttribute("currentUser");
	    return reviewService.getReviewsByUser(me.getUserID());
	}
	*/
	
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
}
