package com.dramalog.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Service;
import com.dramalog.dto.DramaDetailResponse;
import com.dramalog.dto.DramaSummaryResponse;
import com.dramalog.model.Drama;
import com.dramalog.repository.DramaRepository;

@Service
public class DramaService {
	
	private final DramaRepository dramaRepo;
	
	public DramaService(DramaRepository dramaRepo) {
		this.dramaRepo = dramaRepo;
	}
	
	// 홈: 전체 드라마 목록
	public List<DramaSummaryResponse> getAllDramaSummaries() {
		List<Drama> dramas = dramaRepo.findAll();
		List<DramaSummaryResponse> result = new ArrayList<>();
		
		for (Drama d : dramas) {
			DramaSummaryResponse dto = new DramaSummaryResponse(
					d.getDramaID(),
					d.getTitle(),
					d.getGenre(),
					d.getCoverImage(),
					d.getAvgRating());
			result.add(dto);
		}
		
		return result;
	}
	
	// 홈: 추천 드라마 영역, 온보딩 X, count만큼의 랜덤한 드라마 반환
	public List<DramaSummaryResponse> getRandomDramaSummaries(int count) {
		List<Drama> dramas = dramaRepo.findAll();
		Collections.shuffle(dramas);
		
		List<DramaSummaryResponse> result = new ArrayList<>();
		for (Drama d : dramas) {
			result.add(new DramaSummaryResponse(
					d.getDramaID(),
					d.getTitle(),
					d.getGenre(),
					d.getCoverImage(),
					d.getAvgRating()));
			if (result.size() == count) break;
		}
		return result;
	}
	
	// 상세페이지: 드라마 상세 정보
	public DramaDetailResponse getDramaDetail(Integer dramaID) {
		Drama d = dramaRepo.findById(dramaID)
				.orElseThrow(() -> new IllegalArgumentException("Drama not found"));
		
		return new DramaDetailResponse(
				d.getDramaID(),
				d.getTitle(),
				d.getWriter(),
				d.getGenre(),
				d.getSynopsis(),
				d.getMainActor1(),
				d.getMainActor2(),
				d.getReleaseDate(),
				d.getEpisodeCount(),
				d.getCoverImage(),
				d.getAvgRating(),
				d.getHotEpisode());
	}
}