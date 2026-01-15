package com.dramalog.dto;

public record ChatMessageResponse (
		String sender,
		String text,
		long ts // timestamp, 메시지 생성 시각
		){}
