package com.dramalog.model;

import jakarta.persistence.*;

@Entity
@Table(
    name = "bookmark",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "drama_id"})
    }
)
public class Bookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer bookmarkID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drama_id", nullable = false)
    private Drama drama;

    // 게터 세터 ~ 
    public Integer getBookmarkID() {
        return bookmarkID;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Drama getDrama() {
        return drama;
    }

    public void setDrama(Drama drama) {
        this.drama = drama;
    }
}