package com.dramalog.controller;

import java.util.List;
import org.springframework.web.bind.annotation.*;
import com.dramalog.dto.*;
import com.dramalog.service.DramaService;

@RestController
@RequestMapping("/api/dramas")
public class DramaController {
	
	private final DramaService dramaService;
	
	public DramaController(DramaService dramaService) {
		this.dramaService = dramaService;
	}
	
	// 홈: 전체 드라마 목록
	@GetMapping
	public List<DramaSummaryResponse> getAllDramas() {
		return dramaService.getAllDramaSummaries();
	}
	
	// 홈: 추천 드라마 영역, 온보딩 X, count만큼의 랜덤한 드라마 반환
	// count는 기본값으로 10 해뒀습니다.
	@GetMapping("/recommand")
	public List<DramaSummaryResponse> getRandomDramas() {
		return dramaService.getRandomDramaSummaries(45);
	}
	
	// 상세페이지: 드라마 상세 정보
	@GetMapping("/{dramaId}")
	public DramaDetailResponse getDramaDetail(@PathVariable("dramaId") Integer dramaId) {
		return dramaService.getDramaDetail(dramaId);
	}

}
