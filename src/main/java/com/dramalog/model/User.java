package com.dramalog.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    
    @Column(name = "user_id")
    private Integer userID;
    
    @Column(name = "name")
    private String name;
    
    @Column(name = "pin4")
    private String pin4;
    
    @Column(name = "review_count", nullable=false)
    private Integer reviewCount = 0;
    
    @Column(name = "level", nullable=false)
    private Integer level = 0;
    
    public User() {}
	public User(String name, String pin4) {
		this.name = name;
		this.pin4 = pin4;
	}

	public Integer getUserID() {return userID;}	
	public String getName() {return name;}
	public String getPin4() {return pin4;}
	public Integer getReviewCount() {return reviewCount;}
	public Integer getLevel() {return level;}

	public void setUserID(Integer userID) {this.userID = userID;}
	public void setName(String name) {this.name = name;}
	public void setPin4(String pin4) {this.pin4 = pin4;}
	public void setReviewCount(Integer reviewCount) {this.reviewCount = reviewCount;}
	public void setLevel(Integer level) {this.level = level;}
	
    
    
}
