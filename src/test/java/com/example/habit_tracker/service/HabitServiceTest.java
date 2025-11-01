package com.example.habit_tracker.service;

import com.example.habit_tracker.model.Habit;
import com.example.habit_tracker.model.User;
import com.example.habit_tracker.repository.HabitRepository;
import com.example.habit_tracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Testcontainers
@Transactional
class HabitServiceTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private HabitService habitService;

    @Autowired
    private HabitRepository habitRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserName("testuser");
        testUser.setPasswordHash("hashedpassword");
        testUser = userRepository.save(testUser);
    }

    @Test
    void createHabitWithCorrectData() {
        Habit habit = habitService.createHabit(
            testUser.getId(),
            "Exercise",
            "Daily exercise routine"
        );

        assertNotNull(habit.getId());
        assertEquals("Exercise", habit.getName());
        assertEquals("Daily exercise routine", habit.getDescription());
        assertTrue(habit.getIsActive());
        assertEquals(testUser.getId(), habit.getUser().getId());
        assertNotNull(habit.getCreatedAt());
    }

    @Test
    void createHabitWithDuplicateNameThrowsException() {
        habitService.createHabit(testUser.getId(), "Exercise", "Description");

        assertThrows(RuntimeException.class, () -> {
            habitService.createHabit(testUser.getId(), "Exercise", "Another description");
        });
    }

    @Test
    void createHabitWithNonExistentUserThrowsException() {
        assertThrows(RuntimeException.class, () -> {
            habitService.createHabit(9999L, "Exercise", "Description");
        });
    }

    @Test
    void getUserHabitsReturnsAllUserHabits() {
        habitService.createHabit(testUser.getId(), "Exercise", "Description 1");
        habitService.createHabit(testUser.getId(), "Reading", "Description 2");

        List<Habit> habits = habitService.getUserHabits(testUser.getId());

        assertEquals(2, habits.size());
        assertTrue(habits.stream().anyMatch(h -> h.getName().equals("Exercise")));
        assertTrue(habits.stream().anyMatch(h -> h.getName().equals("Reading")));
    }

    @Test
    void getActiveUserHabitsReturnsOnlyActiveHabits() {
        Habit activeHabit = habitService.createHabit(testUser.getId(), "Exercise", "Description");
        Habit inactiveHabit = habitService.createHabit(testUser.getId(), "Reading", "Description");

        habitService.updateHabit(inactiveHabit.getId(), "Reading", "Description", false);
        List<Habit> activeHabits = habitService.getUsersActiveHabits(testUser.getId());

        assertEquals(1, activeHabits.size());
        assertEquals("Exercise", activeHabits.get(0).getName());
        assertTrue(activeHabits.get(0).getIsActive());
    }

    @Test
    void updateHabitSuccessfully() {
        Habit habit = habitService.createHabit(testUser.getId(), "Exercise", "Description");

        Habit updatedHabit = habitService.updateHabit(
            habit.getId(),
            "Updated Exercise",
            "Updated description",
            false
        );

        assertEquals("Updated Exercise", updatedHabit.getName());
        assertEquals("Updated description", updatedHabit.getDescription());
        assertFalse(updatedHabit.getIsActive());
    }

    @Test
    void updateHabitWithNonExistentHabitThrowsException() {
        assertThrows(RuntimeException.class, () -> {
            habitService.updateHabit(999L, "Name", "Description", true);
        });
    }

    @Test
    void deleteHabitSuccessfully() {
        Habit habit = habitService.createHabit(testUser.getId(), "Exercise", "Description");
        Long habitId = habit.getId();

        habitService.deleteHabit(habitId);

        Optional<Habit> deletedHabit = habitService.getHabitById(habitId);
        assertFalse(deletedHabit.isPresent());
    }

    @Test
    void completeHabitDeactivatesHabit() {
        Habit habit = habitService.createHabit(testUser.getId(), "Exercise", "Description");

        habitService.completeHabit(habit.getId());
        boolean isActive = habitRepository.findHabitById(habit.getId()).get().getIsActive();

        assertFalse(isActive);
    }

    @Test
    void completeHabitWithInactiveHabitThrowsException() {
        Habit habit = habitService.createHabit(testUser.getId(), "Exercise", "Description");
        habitService.updateHabit(habit.getId(), null, null, false);

        assertThrows(RuntimeException.class, () -> {
            habitService.completeHabit(habit.getId());
        });
    }
}
