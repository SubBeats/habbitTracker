package com.example.habit_tracker.controller;

import com.example.habit_tracker.model.Habit;
import com.example.habit_tracker.service.HabitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/habits")
public class HabitController {

    private final HabitService habitService;

    @Autowired
    public HabitController(HabitService habitService) {
        this.habitService = habitService;
    }

    @PostMapping
    public ResponseEntity<Habit> createHabit(@RequestBody Habit habit) {
        try {
            habitService.createHabit(
                habit.getUser().getId(),
                habit.getName(),
                habit.getDescription()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(habit);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Habit>> getUserHabits(@PathVariable Long userId) {
        try {
            List<Habit> habits = habitService.getUserHabits(userId);
            return ResponseEntity.ok(habits);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{habitId}")
    public ResponseEntity<Habit> getHabit(@PathVariable Long habitId) {
        try {
            Optional<Habit> habit = habitService.getHabitById(habitId);
            return habit.map(ResponseEntity::ok)
                       .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{habitId}")
    public ResponseEntity<Habit> updateHabit(@PathVariable Long habitId, 
                                           @RequestBody Habit habit) {
        try {
            habitService.updateHabit(
                habitId,
                habit.getName(),
                habit.getDescription(),
                habit.getIsActive()
            );
            return ResponseEntity.ok(habit);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{habitId}")
    public ResponseEntity<Void> deleteHabit(@PathVariable Long habitId) {
        try {
            habitService.deleteHabit(habitId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{habitId}/complete")
    public ResponseEntity<Habit> completeHabit(@PathVariable Long habitId) {
        try {
            Habit habit = habitService.completeHabit(habitId);
            return ResponseEntity.ok(habit);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
