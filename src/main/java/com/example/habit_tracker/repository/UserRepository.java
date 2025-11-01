package com.example.habit_tracker.repository;

import com.example.habit_tracker.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	@EntityGraph(value = "user-with-habit")
	List<User> findAllWithHabitsBy();
}
