package com.example.habit_tracker.service;

import com.example.habit_tracker.model.User;
import com.example.habit_tracker.repository.UserRepository;

import java.util.Optional;

import javax.management.RuntimeErrorException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
	private final UserRepository userRepository;
	private final KafkaLoggerService kafkaLoggerService;

	@Autowired
	public UserService(UserRepository userRepository, KafkaLoggerService kafkaLoggerService) {
		this.userRepository = userRepository;
		this.kafkaLoggerService = kafkaLoggerService;
	}

	public User createUser(String name, String passwordHash) {
		try {
			User newSavedUser = new User();
			newSavedUser.setUserName(name);
			newSavedUser.setPasswordHash(passwordHash);
			userRepository.save(newSavedUser);
			kafkaLoggerService.logUserCreation(newSavedUser.getId(), name);
			return newSavedUser;
		} catch (Exception e) {
			kafkaLoggerService.logError("createUser", e.getMessage());
			throw e;
		}
	}

	public User getUser(Long userId) {
		return userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Habit not found"));
	}
}
