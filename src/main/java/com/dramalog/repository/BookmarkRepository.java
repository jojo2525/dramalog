package com.dramalog.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.dramalog.model.Bookmark;

public interface BookmarkRepository extends JpaRepository<Bookmark, Integer> {

    boolean existsByUser_UserIDAndDrama_DramaID(Integer userId, Integer dramaId);

    void deleteByUser_UserIDAndDrama_DramaID(Integer userId, Integer dramaId);

    List<Bookmark> findByUser_UserID(Integer userId);
}