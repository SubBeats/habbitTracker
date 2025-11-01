package com.example.habit_tracker.model;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@NamedEntityGraph(
		name = "user-with-habit",
		attributeNodes = @NamedAttributeNode("habits")
)
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "userName")
	private String userName;

	@Column(nullable = false, name = "passwordHash")
	private String passwordHash;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Habit> habits = new ArrayList<>();
}
