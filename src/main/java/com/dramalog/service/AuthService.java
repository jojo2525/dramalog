package com.dramalog.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import com.dramalog.model.User;
import com.dramalog.repository.UserRepository;
import jakarta.transaction.Transactional;

@Service
public class AuthService {
	
	private final UserRepository userRepo;
	
	public AuthService(UserRepository userRepo) {
		this.userRepo = userRepo;
	}
	
	@Transactional
	public User loginRegister(String name, String pin4) {
		
		Optional<User> found = userRepo.findByNameAndPin4(name, pin4);
		
		if (found.isPresent()) {
			System.out.println(name + "님, 로그인되었습니다.");
			return found.get();
		}
		
		User newUser = new User();
		newUser.setName(name);
		newUser.setPin4(pin4); // 자바 안에서의 새로운 유저 객체 생성
		
		User saved = userRepo.save(newUser); // DB에 확정된 객체 반환 (id 포함)
		System.out.println(name + "님, 회원가입되었습니다.");
		return saved;
	}
}
