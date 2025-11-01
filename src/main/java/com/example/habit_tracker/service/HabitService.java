package com.example.habit_tracker.service;

import com.example.habit_tracker.repository.HabitRepository;
import com.example.habit_tracker.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.habit_tracker.model.Habit;
import com.example.habit_tracker.model.User;

@Service
public class HabitService {
    private final HabitRepository habitRepository;
    private final UserRepository userRepository;
    
    @Autowired
    public HabitService(HabitRepository habitRepository, UserRepository userRepository) {
        this.habitRepository = habitRepository;
        this.userRepository = userRepository;
    }
    
    public Habit createHabit(Long userId, String name, String description) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Habit habit = new Habit();
        habit.setName(name);
        habit.setDescription(description);
        habit.setUser(user);
        return habitRepository.save(habit);
    }

    public List<Habit> getUserHabits(Long userId) {
        return habitRepository.findByUserId(userId);
    }

    public List<Habit> getUsersActiveHabits(Long userId) {
        return habitRepository.findActiveHabitByUserId(userId);
    }

    public Optional<Habit> getHabitById(Long habitId) {
        return habitRepository.findById(habitId);
    }

    public Habit updateHabit(Long habitId, String name, String description, Boolean isActive) {
        try{
            Habit habit = getHabitById(habitId).orElseThrow(() -> new RuntimeException("Habit not found"));
            habit.setName(name);
            habit.setDescription(description);
            habit.setIsActive(isActive);
            return habitRepository.save(habit);
        }
        catch(Exception ex){
            throw new RuntimeException("Invalid arguments");
        }
    }

    public void deleteHabit(Long habitId) {
        Habit habit = getHabitById(habitId).orElseThrow(() -> new RuntimeException("Habit not found"));
        habitRepository.delete(habit);
    }

    @Transactional()
    public Habit completeHabit(Long habitId) {
        Habit habit = getHabitById(habitId).orElseThrow(() -> new RuntimeException("Habit not found"));
        
        if(!habit.getIsActive()){
            throw new RuntimeException("Habit already complete!");
        }

        habit.setIsActive(false);
        return habitRepository.save(habit);
    }
}   
