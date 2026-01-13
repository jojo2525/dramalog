package com.dramalog.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dramalog.model.Review;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
	
	// 회차 리뷰만 (유저레벨 높은 순)
	@Query("""
			select r
			from Review r
			join r.user u
			where r.dramaID = :dramaID
			and r.episodeSelected = :episodeSelected
			order by u.level desc, r.createdAt desc
			""")
    List<Review> findEpisodeByDramaSorted(@Param("dramaID") Integer dramaID, @Param("episodeSelected") Integer episodeSelected);
    
    // 전체 리뷰만 (유저레벨 높은 순)
    @Query("""
    		select r
    		from Review r
    		join r.user u
    		where r.dramaID = :dramaID
    		and r.episodeSelected is null
    		order by u.level desc, r.createdAt desc
    		""")
    List<Review> findOverallByDramaSorted(@Param("dramaID") Integer dramaID);
    
    // 내가 쓴 리뷰 (최신순)
    List<Review> findByUserIDOrderByCreatedAtDesc(Integer userID);
}