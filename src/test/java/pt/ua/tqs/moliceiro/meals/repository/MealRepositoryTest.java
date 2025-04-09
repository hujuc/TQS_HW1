package pt.ua.tqs.moliceiro.meals.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import pt.ua.tqs.moliceiro.meals.model.Meal;
import pt.ua.tqs.moliceiro.meals.model.Restaurant;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class MealRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MealRepository mealRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    private Restaurant testRestaurant;
    private Meal testMeal;
    private LocalDate today;

    @BeforeEach
    void setUp() {
        mealRepository.deleteAll();
        restaurantRepository.deleteAll();
        
        today = LocalDate.now();
        
        // Create a test restaurant
        testRestaurant = new Restaurant();
        testRestaurant.setName("Test Restaurant");
        testRestaurant.setLocation("Test Location");
        testRestaurant.setCapacity(100);
        testRestaurant.setOperatingHours("09:00-17:00");
        testRestaurant = restaurantRepository.save(testRestaurant);

        // Create a test meal
        testMeal = new Meal();
        testMeal.setRestaurant(testRestaurant);
        testMeal.setName("Test Meal");
        testMeal.setDescription("Test Description");
        testMeal.setPrice(10.0);
        testMeal.setDate(today);
        testMeal.setMealType("lunch");
    }

    @Test
    void whenFindByRestaurantIdAndDateBetween_thenReturnMeals() {
        // Save the meal
        mealRepository.save(testMeal);

        // Create another meal for the same restaurant
        Meal anotherMeal = new Meal();
        anotherMeal.setRestaurant(testRestaurant);
        anotherMeal.setName("Another Meal");
        anotherMeal.setDescription("Another Description");
        anotherMeal.setPrice(15.0);
        anotherMeal.setDate(today);
        anotherMeal.setMealType("dinner");
        mealRepository.save(anotherMeal);

        // Find meals by restaurant ID and date range
        List<Meal> foundMeals = mealRepository.findByRestaurantIdAndDateBetween(
            testRestaurant.getId(),
            today,
            today.plusDays(1)
        );

        // Verify results
        assertThat(foundMeals).hasSize(2);
        assertThat(foundMeals).extracting(Meal::getName)
            .containsExactlyInAnyOrder("Test Meal", "Another Meal");
    }
    
    @org.junit.jupiter.api.AfterAll
    static void tearDown() {
        postgres.close();
    }
} 