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
import pt.ua.tqs.moliceiro.meals.model.Reservation;
import pt.ua.tqs.moliceiro.meals.model.Restaurant;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class ReservationRepositoryTest {

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
    private ReservationRepository reservationRepository;

    @Autowired
    private MealRepository mealRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    private Restaurant testRestaurant;
    private Meal testMeal;
    private Reservation testReservation;

    @BeforeEach
    void setUp() {
        reservationRepository.deleteAll();
        mealRepository.deleteAll();
        restaurantRepository.deleteAll();
        
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
        testMeal.setDate(LocalDate.now());
        testMeal.setMealType("lunch");
        testMeal = mealRepository.save(testMeal);

        // Create a test reservation
        testReservation = new Reservation();
        testReservation.setMeal(testMeal);
        testReservation.setCustomerName("John Doe");
        testReservation.setCustomerEmail("john@example.com");
        testReservation.setNumberOfPeople(2);
        testReservation.setReservationTime(LocalDateTime.now());
        testReservation.setReservationCode("ABC123");
        testReservation.setStatus(Reservation.ReservationStatus.ACTIVE);
        testReservation.setIsUsed(false);
    }

    @Test
    void whenFindByReservationCode_thenReturnReservation() {
        // Save the reservation
        testReservation = reservationRepository.save(testReservation);
        String generatedCode = testReservation.getReservationCode();

        // Find the reservation by code
        Optional<Reservation> foundReservation = reservationRepository.findByReservationCode(generatedCode);

        // Verify the reservation was found
        assertThat(foundReservation).isPresent();
        assertThat(foundReservation.get().getCustomerName()).isEqualTo("John Doe");
        assertThat(foundReservation.get().getCustomerEmail()).isEqualTo("john@example.com");
    }

    @Test
    void whenFindActiveReservationsByMeal_thenReturnReservations() {
        // Save the reservation
        reservationRepository.save(testReservation);

        // Create another reservation for the same meal
        Reservation anotherReservation = new Reservation();
        anotherReservation.setMeal(testMeal);
        anotherReservation.setCustomerName("Jane Doe");
        anotherReservation.setCustomerEmail("jane@example.com");
        anotherReservation.setNumberOfPeople(3);
        anotherReservation.setReservationTime(LocalDateTime.now());
        anotherReservation.setReservationCode("DEF456");
        anotherReservation.setStatus(Reservation.ReservationStatus.ACTIVE);
        anotherReservation.setIsUsed(false);
        reservationRepository.save(anotherReservation);

        // Find active reservations by meal
        List<Reservation> foundReservations = reservationRepository.findByMealId(testMeal.getId());

        // Verify results
        assertThat(foundReservations).hasSize(2);
        assertThat(foundReservations).extracting(Reservation::getCustomerName)
            .containsExactlyInAnyOrder("John Doe", "Jane Doe");
    }

    @org.junit.jupiter.api.AfterAll
    static void tearDown() {
        postgres.close();
    }
} 