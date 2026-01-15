package com.dramalog.websocket;

import java.net.URI;
import java.time.Instant;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.dramalog.dto.ChatMessageRequest;
import com.dramalog.dto.ChatMessageResponse;
import com.dramalog.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;


@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {
	
	// 채팅은 최대 300자, 초당 5개로 제한
	private static final int MAX_TEXT_LEN = 300;
	private static final int MAX_MSG_PER_SEC = 5;
	
	// JSON <-> Java 객체 변환기 (dto 파싱/생성에 사용)
	private final ObjectMapper om = new ObjectMapper();
	
	// rooms: 해당 dramaID 채팅방에 접속 중인 세션(유저) 목록
	// dramaID=10 채팅방에 3명이 있다면, rooms.get(10) = {세션1, 세션2, 세션3}
    private final ConcurrentMap<Integer, CopyOnWriteArraySet<WebSocketSession>> rooms = new ConcurrentHashMap<>();
    
    // rate: sessionID가 최근 1초 동안 몇 번 보냈는지 카운터
    private final ConcurrentMap<String, RateCounter> rate = new ConcurrentHashMap<>();

    // WebSocket 연결 성공 후 1번 호출
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    	// 1. 로그인 여부 확인
    	User user = (User) session.getAttributes().get("currentUser");
    	// 1-1. 로그인이 안 된 상태: WebSocket 연결 거절
    	if (user == null) {
    		session.close(CloseStatus.NOT_ACCEPTABLE.withReason("로그인이 필요합니다."));
    		return;
    	}
    	// 2. URL에서 dramaID 추출
    	Integer dramaID = extractDramaID(session.getUri());
    	if (dramaID == null) {
    		session.close(CloseStatus.NOT_ACCEPTABLE.withReason("유효하지 않은 드라마입니다."));
    		return;
    	}
    	// 3. rooms 맵에 해당 드라마 채팅방이 없다면 새로 만들고.
    	// 해당 채팅방에 현재 session을 추가한다.
        rooms.computeIfAbsent(dramaID, k -> new CopyOnWriteArraySet<>()).add(session);
    }
    
    // 클라이언트가 메시지를 보낼 때 매번 호출
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    	// 1. 로그인 여부 재확인
    	User user = (User) session.getAttributes().get("currentUser");
    	if (user == null) {
    		session.close(CloseStatus.NOT_ACCEPTABLE.withReason("로그인이 필요합니다."));
    		return;
    	}
    	// 2. dramaID 추출
    	Integer dramaID = extractDramaID(session.getUri());
    	if (dramaID == null) {return;}
    	// 3. 도배 방지: 초당 MAX_PER_SEC를 넘으면 무시
    	if (!allow(session.getId())) {return;}
    	// 4. 클라이언트가 보낸 JSON -> ChatMessageRequest로 파싱
    	ChatMessageRequest req;
    	try {
    		req = om.readValue(message.getPayload(), ChatMessageRequest.class);
    	} catch (Exception e) {
    		return;
    	}
    	// 5. 메시지 내용 검증, 길이 제한
    	String text = (req.text() == null) ? "" : req.text().trim();
    	if (text.isEmpty()) {return;} // 공백만 보내면 무시
    	if (text.length() > MAX_TEXT_LEN) {text = text.substring(0, MAX_TEXT_LEN);} // 300자 이상은 자른다.
    	// 6. 서버 세션의 로그인 유저 이름을 sender로 설정한다.
    	String sender = user.getName();
    	// 7. 서버 -> 클라이언트로 보내는 ChatMessageResponse
    	ChatMessageResponse out = new ChatMessageResponse(
    			sender,
    			text,
    			Instant.now().toEpochMilli());
    	String payload = om.writeValueAsString(out);
    	// 8. 채팅방 내의 세션들에게 브로드캐스트
    	Set<WebSocketSession> targets = rooms.getOrDefault(dramaID, new CopyOnWriteArraySet<>());
    	for (WebSocketSession s : targets) {
    		if (s.isOpen()) {
    			s.sendMessage(new TextMessage(payload));
    		}
    	}
    }
    
    // 연결이 끊겼을 때 호출
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
    	// 채팅방에서 해당 세션을 제거
    	Integer dramaID = extractDramaID(session.getUri());
    	if (dramaID != null) {
    		Set<WebSocketSession> set = rooms.get(dramaID);
    		if (set != null) {
    			set.remove(session);
    			
    			if (set.isEmpty()) {rooms.remove(dramaID);} // 채팅방이 비었다면 rooms에서 삭제
    		}
    	}
    	// rate-limit 카운터 제거
    	rate.remove(session.getId());
    }
    
    // URL에서 dramaID 추출
    private Integer extractDramaID(URI uri) {
    	if (uri == null || uri.getPath() == null) {return null;}
    	
    	String[] parts = uri.getPath().split("/");
    	if (parts.length == 0) return null;
    	
    	String last = parts[parts.length - 1]; // last = dramaID
    	try {
    		return Integer.parseInt(last);
    	} catch (NumberFormatException e) {
    		return null;
    	}
    }
    
    // rate-limit 허용 여부
    private boolean allow(String sessionID) {
    	RateCounter c = rate.computeIfAbsent(sessionID, k -> new RateCounter());
    	return c.hit(MAX_MSG_PER_SEC);
    }
    
    // rate-limit 카운터
    // 1초 안에 count를 올리고, 한도를 넘으면 false
    static class RateCounter {
    	private long windowStartMs = System.currentTimeMillis();
    	private int count = 0;
    	
    	synchronized boolean hit(int maxPerSec) {
    		long now = System.currentTimeMillis();
    		
    		// 1초가 지나면 카운트 초기화
    		if (now - windowStartMs >= 1000) {
    			windowStartMs = now;
    			count = 0;
    		}
    		
    		count++; // 이번 메시지 카운트
    		
    		return count <= maxPerSec; // 한도 넘으면 false
    	}
    }
}
