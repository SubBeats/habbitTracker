package com.example.habit_tracker.controller;

import com.example.habit_tracker.model.Habit;
import com.example.habit_tracker.model.User;
import com.example.habit_tracker.repository.HabitRepository;
import com.example.habit_tracker.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Testcontainers
@Transactional
class HabitControllerTest {

    @SuppressWarnings("resource")
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
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HabitRepository habitRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;
    private User testUser;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        testUser = new User();
        testUser.setUserName("testuser");
        testUser.setPasswordHash("hashedpassword");
        testUser = userRepository.save(testUser);
    }

    @Test
    void createHabit_ShouldReturnCreatedHabit() throws Exception {
        Habit habit = new Habit();
        habit.setName("Read Books");
        habit.setDescription("Read 20 mins daily");
        habit.setUser(testUser);

        String json = objectMapper.writeValueAsString(habit);

        mockMvc.perform(post("/habits")
                .contentType("application/json")
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Read Books"))
                .andExpect(jsonPath("$.description").value("Read 20 mins daily"));
    }

    @Test
    void getUserHabits_ShouldReturnListOfHabits() throws Exception {
        Habit habit = new Habit();
        habit.setName("Exercise");
        habit.setDescription("Daily exercise");
        habit.setUser(testUser);
        habit.setIsActive(true);
        habitRepository.save(habit);

        mockMvc.perform(get("/habits/user/" + testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Exercise"));
    }

    @Test
    void getHabit_WhenHabitExists_ShouldReturnHabit() throws Exception {
        Habit habit = new Habit();
        habit.setName("Exercise");
        habit.setDescription("Daily exercise");
        habit.setUser(testUser);
        habit.setIsActive(true);
        habit = habitRepository.save(habit);

        mockMvc.perform(get("/habits/" + habit.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Exercise"));
    }

    @Test
    void getHabit_WhenHabitNotExists_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/habits/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateHabit_ShouldUpdateAndReturnHabit() throws Exception {
        Habit habit = new Habit();
        habit.setName("Read");
        habit.setDescription("Read books");
        habit.setUser(testUser);
        habit.setIsActive(true);
        habit = habitRepository.save(habit);

        Habit updatedHabit = new Habit();
        updatedHabit.setName("Read Updated");
        updatedHabit.setDescription("Read updated books desc");
        updatedHabit.setUser(testUser);
        updatedHabit.setIsActive(false);

        String json = objectMapper.writeValueAsString(updatedHabit);

        mockMvc.perform(put("/habits/" + habit.getId())
                .contentType("application/json")
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Read Updated"))
                .andExpect(jsonPath("$.description").value("Read updated books desc"))
                .andExpect(jsonPath("$.isActive").value(false));
    }

    @Test
    void deleteHabit_ShouldReturnNoContent() throws Exception {
        Habit habit = new Habit();
        habit.setName("Exercise");
        habit.setDescription("Daily exercise");
        habit.setUser(testUser);
        habit.setIsActive(true);
        habit = habitRepository.save(habit);

        mockMvc.perform(delete("/habits/" + habit.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void completeHabit_ShouldDeactivateHabitAndReturnIt() throws Exception {
        Habit habit = new Habit();
        habit.setName("Exercise");
        habit.setDescription("Daily exercise");
        habit.setUser(testUser);
        habit.setIsActive(true);
        habit = habitRepository.save(habit);

        mockMvc.perform(post("/habits/" + habit.getId() + "/complete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isActive").value(false));
    }

    @Test
    void completeHabit_WhenInactive_ShouldReturnBadRequest() throws Exception {
        Habit habit = new Habit();
        habit.setName("Exercise");
        habit.setDescription("Daily exercise");
        habit.setUser(testUser);
        habit.setIsActive(false);
        habit = habitRepository.save(habit);

        mockMvc.perform(post("/habits/" + habit.getId() + "/complete"))
                .andExpect(status().isBadRequest());
    }
}