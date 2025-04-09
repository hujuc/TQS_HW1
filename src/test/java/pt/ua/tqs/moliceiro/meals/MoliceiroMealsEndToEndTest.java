package pt.ua.tqs.moliceiro.meals;

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
import pt.ua.tqs.moliceiro.meals.repository.MealRepository;
import pt.ua.tqs.moliceiro.meals.repository.ReservationRepository;
import pt.ua.tqs.moliceiro.meals.repository.RestaurantRepository;
import pt.ua.tqs.moliceiro.meals.service.MealService;
import pt.ua.tqs.moliceiro.meals.service.ReservationService;
import pt.ua.tqs.moliceiro.meals.service.RestaurantService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class MoliceiroMealsEndToEndTest {

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
    private RestaurantService restaurantService;

    @Autowired
    private MealService mealService;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private MealRepository mealRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    private Restaurant testRestaurant;
    private Meal testMeal;

    @BeforeEach
    void setUp() {
        // Clean up the database
        reservationRepository.deleteAll();
        mealRepository.deleteAll();
        restaurantRepository.deleteAll();

        // Create a test restaurant
        testRestaurant = new Restaurant();
        testRestaurant.setName("Test Restaurant");
        testRestaurant.setLocation("Aveiro");
        testRestaurant.setCapacity(100);
        testRestaurant.setOperatingHours("09:00-22:00");
        testRestaurant = restaurantService.createRestaurant(testRestaurant);

        // Create a test meal
        testMeal = new Meal();
        testMeal.setRestaurant(testRestaurant);
        testMeal.setName("Test Meal");
        testMeal.setDescription("A delicious test meal");
        testMeal.setPrice(15.0);
        testMeal.setDate(LocalDate.now());
        testMeal.setMealType("lunch");
        testMeal = mealService.createMeal(testMeal);
    }

    @Test
    void whenCompleteReservationFlow_thenSuccess() {
        // Step 1: Create a reservation
        Reservation reservation = new Reservation();
        reservation.setMeal(testMeal);
        reservation.setCustomerName("John Doe");
        reservation.setCustomerEmail("john@example.com");
        reservation.setNumberOfPeople(2);
        reservation.setReservationTime(LocalDateTime.now());
        
        Reservation savedReservation = reservationService.createReservation(reservation);
        
        // Verify reservation was created
        assertThat(savedReservation).isNotNull();
        assertThat(savedReservation.getId()).isNotNull();
        assertThat(savedReservation.getReservationCode()).isNotNull();
        assertThat(savedReservation.getStatus()).isEqualTo(Reservation.ReservationStatus.ACTIVE);
        assertThat(savedReservation.getIsUsed()).isFalse();

        // Step 2: Get reservation by code
        Optional<Reservation> foundReservation = reservationService.getReservationByCode(savedReservation.getReservationCode());
        assertThat(foundReservation).isPresent();
        assertThat(foundReservation.get().getCustomerName()).isEqualTo("John Doe");

        // Step 3: Cancel reservation
        Optional<Reservation> cancelledReservation = reservationService.cancelReservation(savedReservation.getReservationCode());
        assertThat(cancelledReservation).isPresent();
        assertThat(cancelledReservation.get().getStatus()).isEqualTo(Reservation.ReservationStatus.CANCELED);

        // Step 4: Verify reservation is cancelled
        Optional<Reservation> finalCheck = reservationService.getReservationByCode(savedReservation.getReservationCode());
        assertThat(finalCheck).isPresent();
        assertThat(finalCheck.get().getStatus()).isEqualTo(Reservation.ReservationStatus.CANCELED);
    }

    @org.junit.jupiter.api.AfterAll
    static void tearDown() {
        postgres.close();
    }
} 