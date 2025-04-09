package pt.ua.tqs.moliceiro.meals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import pt.ua.tqs.moliceiro.meals.model.Restaurant;
import pt.ua.tqs.moliceiro.meals.repository.RestaurantRepository;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class MoliceiroMealsApplicationTests {

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

    @Test
    void contextLoads() {
        // This test will fail if the application context cannot start
    }

    @Test
    void testRestaurantRepository() {
        // Create a test restaurant
        Restaurant restaurant = new Restaurant();
        restaurant.setName("Test Restaurant");
        restaurant.setLocation("Test Location");
        restaurant.setCapacity(100);
        restaurant.setOperatingHours("09:00-17:00");

        // Save the restaurant
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);

        // Verify the restaurant was saved
        assertThat(savedRestaurant.getId()).isNotNull();
        assertThat(savedRestaurant.getName()).isEqualTo("Test Restaurant");
    }
} 