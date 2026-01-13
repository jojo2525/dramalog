package com.dramalog.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;

@Entity
@Table(name = "reviews")
public class Review {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "review_id", nullable = false)
	private Integer reviewID;
	
	@Column(name = "user_id", nullable = false)
	private int userID;
	
	@Column(name = "drama_id", nullable = false)
	private int dramaID;
	
	@Column(name = "watch_status")
	private Integer watchStatus;
	
	@Column(name = "episode_selected")
	private Integer episodeSelected;
	
	@Column(name = "content", nullable = false)
	private String content;
	
	@CreationTimestamp
	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;
	
	@Column(name = "rating")
	private BigDecimal rating;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
	private User user;
	
	public Review() {}

	public Integer getReviewID() {return reviewID;}
	public int getUserID() {return userID;}
	public int getDramaID() {return dramaID;}
	public Integer getWatchStatus() {return watchStatus;}
	public Integer getEpisodeSelected() {return episodeSelected;}
	public String getContent() {return content;}
	public LocalDateTime getCreatedAt() {return createdAt;}
	public BigDecimal getRating() {return rating;}

	public void setReviewID(Integer reviewID) {this.reviewID = reviewID;}
	public void setUserID(int userID) {this.userID = userID;}
	public void setDramaID(int dramaID) {this.dramaID = dramaID;}
	public void setWatchStatus(int watchStatus) {this.watchStatus = watchStatus;}
	public void setEpisodeSelected(int episodeSelected) {this.episodeSelected = episodeSelected;}
	public void setContent(String content) {this.content = content;}
	public void setCreatedAt(LocalDateTime createdAt) {this.createdAt = createdAt;}
	public void setRating(BigDecimal rating) {this.rating = rating;}
}
