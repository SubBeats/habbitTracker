package com.example.habit_tracker.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class KafkaLoggerService {

	private final KafkaTemplate<String, Object> kafkaTemplate;
	private static final String LOG_TOPIC = "user-activity-logs";
	private static final String ERROR_LOG = "user-activity-logs";

	public KafkaLoggerService(KafkaTemplate<String, Object> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}

	public void logUserCreation(Long userId, String username) {
        UserCreationEvent event = new UserCreationEvent(userId, username, LocalDateTime.now());
		kafkaTemplate.send(LOG_TOPIC, event);
	}

	public void logError(String methodName, String errorMessage) {
        ErrorEvent event = new ErrorEvent(methodName, errorMessage, LocalDateTime.now());
        kafkaTemplate.send(ERROR_LOG, event);
	}
}

class UserCreationEvent {
	private final Long userId;
	private final String username;
	private final LocalDateTime timestamp;

	public UserCreationEvent(Long userId, String username, LocalDateTime timestamp) {
		this.userId = userId;
		this.username = username;
		this.timestamp = timestamp;
	}
}

class ErrorEvent {
	private final String methodName;
	private final String errorMessage;
	private final LocalDateTime timestamp;

	public ErrorEvent(String methodName, String errorMessage, LocalDateTime timestamp) {
		this.methodName = methodName;
		this.errorMessage = errorMessage;
		this.timestamp = timestamp;
	}
}