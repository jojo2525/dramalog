package com.dramalog.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.dramalog.model.Review;
import com.dramalog.repository.ReviewRepository;

@Service
public class ReviewService {
	
	private final ReviewRepository reviewRepo;
	
	public ReviewService(ReviewRepository reviewRepo) {
		this.reviewRepo = reviewRepo;
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
} 