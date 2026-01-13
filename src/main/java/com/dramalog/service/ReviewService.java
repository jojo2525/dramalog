package com.dramalog.service;

import org.springframework.stereotype.Service;

import com.dramalog.repository.ReviewRepository;

@Service
public class ReviewService {
	
	private final ReviewRepository reviewRepo;
	
	public ReviewService(ReviewRepository reviewRepo) {
		this.reviewRepo = reviewRepo;
	}
	
	
}