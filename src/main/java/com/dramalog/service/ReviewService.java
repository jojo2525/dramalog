package com.dramalog.service;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import com.dramalog.dto.ReviewCreateRequest;
import com.dramalog.dto.ReviewResponse;
import com.dramalog.model.*;
import com.dramalog.repository.DramaRepository;
import com.dramalog.repository.ReviewRepository;
import com.dramalog.repository.UserRepository;

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
	public ReviewResponse createReview(Integer dramaID, ReviewCreateRequest req) {
		
		// 드라마 존재 여부 검증
		Drama drama = dramaRepo.findById(dramaID)
				.orElseThrow(() -> new IllegalArgumentException("드라마가 존재하지 않습니다."));
		
		// 유저 존재 여부 검증
		User user = userRepo.findById(req.getUserID())
				.orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));
		
		Integer ep = req.getEpisodeSelected();
		BigDecimal rating = req.getRating();
		
		// 회차별 리뷰일 시, 회차 범위 검증
		if (ep != null) {
			int total = drama.getEpisodeCount();
			if (ep <= 0 || ep > total) {
				throw new IllegalArgumentException("회차 범위가 올바르지 않습니다.");
			}
		}
		
		// 전체 리뷰일 시, 별점 검증
		// 전체 리뷰: 별점 필수, 회차별 리뷰: 별점 금지
		if (ep == null) {
			if (rating == null ) {
				throw new IllegalArgumentException("전체 리뷰에는 별점이 필수입니다.");
			}
			if (rating.compareTo(BigDecimal.ZERO) < 0 || rating.compareTo(new BigDecimal("5")) > 0) {
				throw new IllegalArgumentException("별점 범위가 올바르지 않습니다.");
			}
		} else {
			if (rating != null) {
				throw new IllegalArgumentException("회차 리뷰에는 별점을 입력할 수 없습니다.");
			}
		}
		
		// 시청상태 검증 (0: 보는 중, 1: 완료, 2: 중도하차)
		Integer watchStatus = req.getWatchStatus();
		if (watchStatus == null || watchStatus < 0 || watchStatus > 2) {
			throw new IllegalArgumentException("시청 상태 값이 올바르지 않습니다.");
		}
		
		// 저장
		Review review = new Review();
		review.setDramaID(drama.getDramaID());
		review.setUserID(user.getUserID());
		review.setEpisodeSelected(ep);
		review.setRating(rating);
		review.setWatchStatus(watchStatus);
		review.setContent(req.getContent());
		
		Review saved = reviewRepo.save(review);
		
		if (saved.getEpisodeSelected() == null) {
			dramaService.updateAvgRating(saved.getDramaID());
		}
		
		return new ReviewResponse(
				saved.getReviewID(),
				saved.getDramaID(),
				saved.getUserID(),
				saved.getEpisodeSelected(),
				saved.getRating(),
				saved.getWatchStatus(),
				saved.getContent());
	}
} 