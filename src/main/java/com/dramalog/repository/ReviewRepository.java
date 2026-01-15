package com.dramalog.repository;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.dramalog.model.Review;

import jakarta.transaction.Transactional;

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
    
    // 리뷰 회차 API
    @Query("""
    		select r.episodeSelected, count(r)
    		from Review r
    		where r.dramaID = :dramaID
    		and r.episodeSelected is not null
    		group by r.episodeSelected
    		""")
    		List<Object[]> countByEpisode(@Param("dramaID") Integer dramaID);
    
    // 내가 쓴 리뷰 (최신순)
    List<Review> findByUserIDOrderByCreatedAtDesc(Integer userID);
    
    // 내 전체 리뷰 수 
    long countByUserID(Integer userID);
    
    // 리뷰 삭제 
    @Transactional
    void deleteByReviewIDAndUserID(Integer reviewID, Integer userID);
    
    // 평균 별점 계산
    @Query("""
    	    SELECT AVG(r.rating)
    	    FROM Review r
    	    WHERE r.dramaID = :dramaID
    	      AND r.episodeSelected IS NULL
    	""")
    BigDecimal findAvgRatingForDrama(@Param("dramaID") Integer dramaID);
    
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
    update Drama d
    set d.avgRating = (
      select avg(r.rating)
      from Review r
      where r.dramaID = d.dramaID
        and r.episodeSelected is null
    )
    where d.dramaID = :dramaId
    """)
    void refreshAvgRating(@Param("dramaId") Integer dramaId);

}