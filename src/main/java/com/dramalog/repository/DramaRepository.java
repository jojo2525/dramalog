package com.dramalog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dramalog.model.Drama;

public interface DramaRepository extends JpaRepository<Drama, Integer> {
	

}
