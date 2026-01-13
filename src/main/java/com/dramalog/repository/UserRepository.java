package com.dramalog.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.dramalog.model.User;

public interface UserRepository extends JpaRepository<User, Integer>{
	
	Optional<User> findByNameAndPin4(String name, String pin4);
}
