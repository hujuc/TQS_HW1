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
import pt.ua.tqs.moliceiro.meals.model.Restaurant;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class RestaurantRepositoryTest {

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
    private RestaurantRepository restaurantRepository;

    private Restaurant testRestaurant;

    @BeforeEach
    void setUp() {
        restaurantRepository.deleteAll();
        
        // Create a test restaurant
        testRestaurant = new Restaurant();
        testRestaurant.setName("Test Restaurant");
        testRestaurant.setLocation("Test Location");
        testRestaurant.setCapacity(100);
        testRestaurant.setOperatingHours("09:00-17:00");
    }

    @Test
    void whenSaveRestaurant_thenReturnSavedRestaurant() {
        // Save the restaurant
        Restaurant savedRestaurant = restaurantRepository.save(testRestaurant);

        // Verify the restaurant was saved
        assertThat(savedRestaurant.getId()).isNotNull();
        assertThat(savedRestaurant.getName()).isEqualTo("Test Restaurant");
        assertThat(savedRestaurant.getLocation()).isEqualTo("Test Location");
        assertThat(savedRestaurant.getCapacity()).isEqualTo(100);
        assertThat(savedRestaurant.getOperatingHours()).isEqualTo("09:00-17:00");
    }

    @Test
    void whenFindAll_thenReturnAllRestaurants() {
        // Save multiple restaurants
        Restaurant restaurant1 = restaurantRepository.save(testRestaurant);
        
        Restaurant restaurant2 = new Restaurant();
        restaurant2.setName("Another Restaurant");
        restaurant2.setLocation("Another Location");
        restaurant2.setCapacity(50);
        restaurant2.setOperatingHours("10:00-18:00");
        restaurantRepository.save(restaurant2);

        // Find all restaurants
        List<Restaurant> foundRestaurants = restaurantRepository.findAll();

        // Verify results
        assertThat(foundRestaurants).hasSize(2);
        assertThat(foundRestaurants).extracting(Restaurant::getName)
            .containsExactlyInAnyOrder("Test Restaurant", "Another Restaurant");
    }

    @Test
    void whenFindById_thenReturnRestaurant() {
        // Save the restaurant
        Restaurant savedRestaurant = restaurantRepository.save(testRestaurant);

        // Find the restaurant by ID
        Optional<Restaurant> foundRestaurant = restaurantRepository.findById(savedRestaurant.getId());

        // Verify the restaurant was found
        assertThat(foundRestaurant).isPresent();
        assertThat(foundRestaurant.get().getName()).isEqualTo("Test Restaurant");
    }

    @Test
    void whenUpdateRestaurant_thenReturnUpdatedRestaurant() {
        // Save the restaurant
        Restaurant savedRestaurant = restaurantRepository.save(testRestaurant);

        // Update the restaurant
        savedRestaurant.setName("Updated Restaurant");
        savedRestaurant.setCapacity(150);
        Restaurant updatedRestaurant = restaurantRepository.save(savedRestaurant);

        // Verify the restaurant was updated
        assertThat(updatedRestaurant.getName()).isEqualTo("Updated Restaurant");
        assertThat(updatedRestaurant.getCapacity()).isEqualTo(150);
    }

    @Test
    void whenDeleteRestaurant_thenReturnEmpty() {
        // Save the restaurant
        Restaurant savedRestaurant = restaurantRepository.save(testRestaurant);

        // Delete the restaurant
        restaurantRepository.deleteById(savedRestaurant.getId());

        // Verify the restaurant was deleted
        Optional<Restaurant> foundRestaurant = restaurantRepository.findById(savedRestaurant.getId());
        assertThat(foundRestaurant).isEmpty();
    }
    
    @org.junit.jupiter.api.AfterAll
    static void tearDown() {
        postgres.close();
    }
} 