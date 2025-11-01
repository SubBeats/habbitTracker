package com.example.habit_tracker.model;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "habits")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Habit {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String name;

	@Column(nullable = false)
	private String description;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "is_active", nullable = false)
	private Boolean isActive = true;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
	}
}
