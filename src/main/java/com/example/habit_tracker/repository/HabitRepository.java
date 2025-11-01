package com.example.habit_tracker.repository;

import com.example.habit_tracker.model.Habit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HabitRepository extends JpaRepository<Habit, Long> {
   
    @Query("SELECT h FROM Habit h WHERE h.id = :habitId")
    Optional<Habit> findHabitById(@Param("habitId") Long habitId);

    @Query("SELECT h FROM Habit h WHERE h.user.id = :userId")
    List<Habit> findByUserId(@Param("userId") Long userId);

    @Query("SELECT h FROM Habit h WHERE h.user.id = :userId and h.isActive = true")
    List<Habit> findActiveHabitByUserId(@Param("userId") Long userId);

    @Query("SELECT h FROM Habit h WHERE h.user.id = :userId AND h.isActive = true")
    Optional<Habit> findActiveHabitsByUserId(@Param("userId") Long userId);
}
