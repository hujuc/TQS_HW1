package pt.ua.tqs.moliceiro.meals.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.ua.tqs.moliceiro.meals.model.Meal;
import pt.ua.tqs.moliceiro.meals.model.Reservation;
import pt.ua.tqs.moliceiro.meals.model.Restaurant;
import pt.ua.tqs.moliceiro.meals.repository.MealRepository;
import pt.ua.tqs.moliceiro.meals.repository.ReservationRepository;
import pt.ua.tqs.moliceiro.meals.repository.RestaurantRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private MealRepository mealRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private ReservationService reservationService;

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
        when(reservationRepository.findByReservationCode("ABC123")).thenReturn(Optional.of(testReservation));

        // Act
        Optional<Reservation> foundReservation = reservationService.getReservationByCode("ABC123");

        // Assert
        assertThat(foundReservation).isPresent();
        assertThat(foundReservation.get()).isEqualTo(testReservation);
        verify(reservationRepository, times(1)).findByReservationCode("ABC123");
    }

    @Test
    void whenGetReservationsByMeal_thenReturnReservations() {
        // Arrange
        List<Reservation> expectedReservations = Arrays.asList(testReservation);
        when(reservationRepository.findByMealId(1L)).thenReturn(expectedReservations);

        // Act
        List<Reservation> actualReservations = reservationService.getReservationsByMeal(1L);

        // Assert
        assertThat(actualReservations).hasSize(1);
        assertThat(actualReservations.get(0)).isEqualTo(testReservation);
        verify(reservationRepository, times(1)).findByMealId(1L);
    }

    @Test
    void whenGetReservationsByCustomerEmail_thenReturnReservations() {
        // Arrange
        List<Reservation> expectedReservations = Arrays.asList(testReservation);
        when(reservationRepository.findByCustomerEmail("john@example.com")).thenReturn(expectedReservations);

        // Act
        List<Reservation> actualReservations = reservationService.getReservationsByCustomerEmail("john@example.com");

        // Assert
        assertThat(actualReservations).hasSize(1);
        assertThat(actualReservations.get(0)).isEqualTo(testReservation);
        verify(reservationRepository, times(1)).findByCustomerEmail("john@example.com");
    }

    @Test
    void whenCheckActiveReservations_thenReturnCorrectStatus() {
        // Arrange
        when(reservationRepository.existsByMealIdAndIsUsedFalse(1L)).thenReturn(true);

        // Act
        boolean hasActiveReservations = reservationService.hasActiveReservations(1L);

        // Assert
        assertThat(hasActiveReservations).isTrue();
        verify(reservationRepository, times(1)).existsByMealIdAndIsUsedFalse(1L);
    }

    @Test
    void whenCreateValidReservation_thenReturnSavedReservation() {
        // Arrange
        when(mealRepository.findById(1L)).thenReturn(Optional.of(testMeal));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(testReservation);
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(testRestaurant);

        // Act
        Reservation savedReservation = reservationService.createReservation(testReservation);

        // Assert
        assertThat(savedReservation).isEqualTo(testReservation);
        verify(reservationRepository, times(1)).save(testReservation);
        verify(restaurantRepository, times(1)).save(testRestaurant);
    }

    @Test
    void whenCreateReservationWithInvalidMeal_thenThrowException() {
        // Arrange
        when(mealRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> reservationService.createReservation(testReservation))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Meal not found");
        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    void whenCreateReservationWithNotEnoughSeats_thenThrowException() {
        // Arrange
        testReservation.setNumberOfPeople(150); // More than restaurant capacity
        when(mealRepository.findById(1L)).thenReturn(Optional.of(testMeal));

        // Act & Assert
        assertThatThrownBy(() -> reservationService.createReservation(testReservation))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Not enough capacity in the restaurant");
        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    void whenCreateReservationWithInvalidEmail_thenThrowException() {
        // Arrange
        testReservation.setCustomerEmail("invalid-email");

        // Act & Assert
        assertThatThrownBy(() -> reservationService.createReservation(testReservation))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Invalid email format");
        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    void whenCreateReservationWithEmptyName_thenThrowException() {
        // Arrange
        testReservation.setCustomerName("");

        // Act & Assert
        assertThatThrownBy(() -> reservationService.createReservation(testReservation))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Customer name is required");
        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    void whenCreateReservationWithInvalidNumberOfPeople_thenThrowException() {
        // Arrange
        testReservation.setNumberOfPeople(0);

        // Act & Assert
        assertThatThrownBy(() -> reservationService.createReservation(testReservation))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Number of people must be greater than 0");
        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    void whenCancelValidReservation_thenReturnCancelledReservation() {
        // Arrange
        when(reservationRepository.findByReservationCode("ABC123")).thenReturn(Optional.of(testReservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(testReservation);
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(testRestaurant);

        // Act
        Optional<Reservation> cancelledReservation = reservationService.cancelReservation("ABC123");

        // Assert
        assertThat(cancelledReservation).isPresent();
        assertThat(cancelledReservation.get()).isEqualTo(testReservation);
        verify(reservationRepository, times(1)).save(testReservation);
        verify(restaurantRepository, times(1)).save(testRestaurant);
    }

    @Test
    void whenCancelUsedReservation_thenThrowException() {
        // Arrange
        testReservation.setIsUsed(true);
        when(reservationRepository.findByReservationCode("ABC123")).thenReturn(Optional.of(testReservation));

        // Act & Assert
        assertThatThrownBy(() -> reservationService.cancelReservation("ABC123"))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Reservation has already been used");
        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    void whenMarkValidReservationAsUsed_thenReturnUpdatedReservation() {
        // Arrange
        when(reservationRepository.findByReservationCode("ABC123")).thenReturn(Optional.of(testReservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(testReservation);
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(testRestaurant);

        // Act
        Optional<Reservation> updatedReservation = reservationService.markReservationAsUsed("ABC123");

        // Assert
        assertThat(updatedReservation).isPresent();
        assertThat(updatedReservation.get().getIsUsed()).isTrue();
        verify(reservationRepository, times(1)).save(testReservation);
        verify(restaurantRepository, times(1)).save(testRestaurant);
    }

    @Test
    void whenMarkUsedReservationAsUsed_thenThrowException() {
        // Arrange
        testReservation.setIsUsed(true);
        when(reservationRepository.findByReservationCode("ABC123")).thenReturn(Optional.of(testReservation));

        // Act & Assert
        assertThatThrownBy(() -> reservationService.markReservationAsUsed("ABC123"))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Reservation has already been used");
        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    void whenGetAllReservations_thenReturnAllReservations() {
        // Arrange
        List<Reservation> expectedReservations = Arrays.asList(testReservation);
        when(reservationRepository.findAll()).thenReturn(expectedReservations);

        // Act
        List<Reservation> actualReservations = reservationService.getAllReservations();

        // Assert
        assertThat(actualReservations).hasSize(1);
        assertThat(actualReservations.get(0)).isEqualTo(testReservation);
        verify(reservationRepository, times(1)).findAll();
    }

    @Test
    void whenDeleteReservation_thenReturnDeletedReservation() {
        // Arrange
        when(reservationRepository.findByReservationCode("ABC123")).thenReturn(Optional.of(testReservation));
        doNothing().when(reservationRepository).delete(testReservation);
        when(restaurantRepository.save(testRestaurant)).thenReturn(testRestaurant);

        // Act
        Optional<Reservation> deletedReservation = reservationService.deleteReservation("ABC123");

        // Assert
        assertThat(deletedReservation)
            .isPresent()
            .get()
            .extracting(Reservation::getReservationCode, Reservation::getCustomerName, Reservation::getNumberOfPeople)
            .containsExactly(testReservation.getReservationCode(), testReservation.getCustomerName(), testReservation.getNumberOfPeople());
        verify(reservationRepository, times(1)).delete(testReservation);
        verify(restaurantRepository, times(1)).save(testRestaurant);
    }

    @Test
    void whenDeleteUsedReservation_thenReturnDeletedReservationWithoutRestoringCapacity() {
        // Arrange
        testReservation.setIsUsed(true);
        when(reservationRepository.findByReservationCode("ABC123")).thenReturn(Optional.of(testReservation));
        doNothing().when(reservationRepository).delete(testReservation);

        // Act
        Optional<Reservation> deletedReservation = reservationService.deleteReservation("ABC123");

        // Assert
        assertThat(deletedReservation)
            .isPresent()
            .get()
            .extracting(Reservation::getReservationCode, Reservation::getCustomerName, Reservation::getNumberOfPeople)
            .containsExactly(testReservation.getReservationCode(), testReservation.getCustomerName(), testReservation.getNumberOfPeople());
        verify(reservationRepository, times(1)).delete(testReservation);
        verify(restaurantRepository, never()).save(any());
    }

    @Test
    void whenDeleteNonExistentReservation_thenReturnEmpty() {
        // Arrange
        when(reservationRepository.findByReservationCode("ABC123")).thenReturn(Optional.empty());

        // Act
        Optional<Reservation> deletedReservation = reservationService.deleteReservation("ABC123");

        // Assert
        assertThat(deletedReservation).isEmpty();
        verify(reservationRepository, never()).delete(any());
        verify(restaurantRepository, never()).save(any());
    }

    @Test
    void whenDeleteCanceledReservation_thenReturnDeletedReservationWithoutRestoringCapacity() {
        // Arrange
        testReservation.setStatus(Reservation.ReservationStatus.CANCELED);
        when(reservationRepository.findByReservationCode("ABC123")).thenReturn(Optional.of(testReservation));

        // Act
        Optional<Reservation> deletedReservation = reservationService.deleteReservation("ABC123");

        // Assert
        assertThat(deletedReservation)
            .isPresent()
            .contains(testReservation);

        verify(reservationRepository, never()).delete(any());
        verify(restaurantRepository, never()).save(any());
    }
} 