package pt.ua.tqs.moliceiro.meals.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ua.tqs.moliceiro.meals.model.Meal;
import pt.ua.tqs.moliceiro.meals.model.Reservation;
import pt.ua.tqs.moliceiro.meals.model.Restaurant;
import pt.ua.tqs.moliceiro.meals.service.ReservationService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationControllerTest {

    @Mock
    private ReservationService reservationService;

    @InjectMocks
    private ReservationController reservationController;

    private Restaurant testRestaurant;
    private Meal testMeal;
    private Reservation testReservation;

    @BeforeEach
    void setUp() {
        // Setup test restaurant
        testRestaurant = new Restaurant();
        testRestaurant.setId(1L);
        testRestaurant.setName("Test Restaurant");
        testRestaurant.setLocation("Test Location");
        testRestaurant.setCapacity(100);
        testRestaurant.setOperatingHours("09:00-17:00");

        // Setup test meal
        testMeal = new Meal();
        testMeal.setId(1L);
        testMeal.setRestaurant(testRestaurant);
        testMeal.setName("Lunch Special");
        testMeal.setDescription("Daily special");
        testMeal.setPrice(10.0);
        testMeal.setDate(LocalDate.now());
        testMeal.setMealType("lunch");

        // Setup test reservation
        testReservation = new Reservation();
        testReservation.setId(1L);
        testReservation.setMeal(testMeal);
        testReservation.setCustomerName("John Doe");
        testReservation.setCustomerEmail("john@example.com");
        testReservation.setNumberOfPeople(2);
        testReservation.setReservationTime(LocalDateTime.now());
        testReservation.setIsUsed(false);
    }

    @Test
    void whenGetReservationByCode_thenReturnReservation() {
        // Arrange
        when(reservationService.getReservationByCode("ABC123")).thenReturn(Optional.of(testReservation));

        // Act
        ResponseEntity<Reservation> response = reservationController.getReservationByCode("ABC123");

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(testReservation);
        verify(reservationService, times(1)).getReservationByCode("ABC123");
    }

    @Test
    void whenGetReservationByCodeNotFound_thenReturnNotFound() {
        // Arrange
        when(reservationService.getReservationByCode("ABC123")).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Reservation> response = reservationController.getReservationByCode("ABC123");

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(reservationService, times(1)).getReservationByCode("ABC123");
    }

    @Test
    void whenGetReservationsByMeal_thenReturnReservations() {
        // Arrange
        List<Reservation> expectedReservations = Arrays.asList(testReservation);
        when(reservationService.getReservationsByMeal(1L)).thenReturn(expectedReservations);

        // Act
        ResponseEntity<List<Reservation>> response = reservationController.getReservationsByMeal(1L);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedReservations);
        verify(reservationService, times(1)).getReservationsByMeal(1L);
    }

    @Test
    void whenGetReservationsByCustomer_thenReturnReservations() {
        // Arrange
        List<Reservation> expectedReservations = Arrays.asList(testReservation);
        when(reservationService.getReservationsByCustomerEmail("john@example.com")).thenReturn(expectedReservations);

        // Act
        ResponseEntity<List<Reservation>> response = reservationController.getReservationsByCustomer("john@example.com");

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedReservations);
        verify(reservationService, times(1)).getReservationsByCustomerEmail("john@example.com");
    }

    @Test
    void whenCreateValidReservation_thenReturnCreatedReservation() {
        // Arrange
        when(reservationService.createReservation(any(Reservation.class))).thenReturn(testReservation);

        // Act
        ResponseEntity<Reservation> response = reservationController.createReservation(testReservation);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(testReservation);
        verify(reservationService, times(1)).createReservation(testReservation);
    }

    @Test
    void whenCreateInvalidReservation_thenReturnBadRequest() {
        // Arrange
        when(reservationService.createReservation(any(Reservation.class)))
            .thenThrow(new IllegalArgumentException("Invalid reservation"));

        // Act
        ResponseEntity<Reservation> response = reservationController.createReservation(testReservation);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(reservationService, times(1)).createReservation(testReservation);
    }

    @Test
    void whenCancelValidReservation_thenReturnCancelledReservation() {
        // Arrange
        when(reservationService.cancelReservation("ABC123")).thenReturn(Optional.of(testReservation));

        // Act
        ResponseEntity<Reservation> response = reservationController.cancelReservation("ABC123");

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(testReservation);
        verify(reservationService, times(1)).cancelReservation("ABC123");
    }

    @Test
    void whenCancelNonExistentReservation_thenReturnNotFound() {
        // Arrange
        when(reservationService.cancelReservation("ABC123")).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Reservation> response = reservationController.cancelReservation("ABC123");

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(reservationService, times(1)).cancelReservation("ABC123");
    }

    @Test
    void whenMarkValidReservationAsUsed_thenReturnUpdatedReservation() {
        // Arrange
        when(reservationService.markReservationAsUsed("ABC123")).thenReturn(Optional.of(testReservation));

        // Act
        ResponseEntity<Reservation> response = reservationController.markReservationAsUsed("ABC123");

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(testReservation);
        verify(reservationService, times(1)).markReservationAsUsed("ABC123");
    }

    @Test
    void whenMarkNonExistentReservationAsUsed_thenReturnNotFound() {
        // Arrange
        when(reservationService.markReservationAsUsed("ABC123")).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Reservation> response = reservationController.markReservationAsUsed("ABC123");

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(reservationService, times(1)).markReservationAsUsed("ABC123");
    }
} 