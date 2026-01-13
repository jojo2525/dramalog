package com.dramalog.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.dramalog.model.Review;

public interface ReviewRepository extends JpaRepository<Review, Integer> {

    List<Review> findByDramaIDAndEpisodeSelected(Integer dramaID, Integer episodeSelected);

    List<Review> findByDramaID(Integer dramaID);

    List<Review> findByUserID(Integer userID);
}