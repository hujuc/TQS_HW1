package pt.ua.tqs.moliceiro.meals.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ua.tqs.moliceiro.meals.model.Restaurant;
import pt.ua.tqs.moliceiro.meals.service.RestaurantService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestaurantControllerTest {

    @Mock
    private RestaurantService restaurantService;

    @InjectMocks
    private RestaurantController restaurantController;

    private Restaurant testRestaurant;

    @BeforeEach
    void setUp() {
        testRestaurant = new Restaurant();
        testRestaurant.setId(1L);
        testRestaurant.setName("Test Restaurant");
        testRestaurant.setLocation("Test Location");
        testRestaurant.setCapacity(100);
        testRestaurant.setOperatingHours("09:00-17:00");
    }

    @Test
    void whenGetAllRestaurants_thenReturnAllRestaurants() {
        // Arrange
        List<Restaurant> expectedRestaurants = Arrays.asList(testRestaurant);
        when(restaurantService.getAllRestaurants()).thenReturn(expectedRestaurants);

        // Act
        ResponseEntity<List<Restaurant>> response = restaurantController.getAllRestaurants();

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedRestaurants);
        verify(restaurantService, times(1)).getAllRestaurants();
    }

    @Test
    void whenGetRestaurantById_thenReturnRestaurant() {
        // Arrange
        when(restaurantService.getRestaurantById(1L)).thenReturn(Optional.of(testRestaurant));

        // Act
        ResponseEntity<Restaurant> response = restaurantController.getRestaurantById(1L);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(testRestaurant);
        verify(restaurantService, times(1)).getRestaurantById(1L);
    }

    @Test
    void whenGetRestaurantByIdNotFound_thenReturnNotFound() {
        // Arrange
        when(restaurantService.getRestaurantById(1L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Restaurant> response = restaurantController.getRestaurantById(1L);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(restaurantService, times(1)).getRestaurantById(1L);
    }

    @Test
    void whenCreateRestaurant_thenReturnCreatedRestaurant() {
        // Arrange
        when(restaurantService.createRestaurant(any(Restaurant.class))).thenReturn(testRestaurant);

        // Act
        ResponseEntity<Restaurant> response = restaurantController.createRestaurant(testRestaurant);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(testRestaurant);
        verify(restaurantService, times(1)).createRestaurant(testRestaurant);
    }

    @Test
    void whenUpdateRestaurant_thenReturnUpdatedRestaurant() {
        // Arrange
        when(restaurantService.updateRestaurant(1L, testRestaurant)).thenReturn(Optional.of(testRestaurant));

        // Act
        ResponseEntity<Restaurant> response = restaurantController.updateRestaurant(1L, testRestaurant);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(testRestaurant);
        verify(restaurantService, times(1)).updateRestaurant(1L, testRestaurant);
    }

    @Test
    void whenUpdateRestaurantNotFound_thenReturnNotFound() {
        // Arrange
        when(restaurantService.updateRestaurant(1L, testRestaurant)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Restaurant> response = restaurantController.updateRestaurant(1L, testRestaurant);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(restaurantService, times(1)).updateRestaurant(1L, testRestaurant);
    }

    @Test
    void whenDeleteRestaurant_thenReturnNoContent() {
        // Act
        ResponseEntity<Void> response = restaurantController.deleteRestaurant(1L);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(restaurantService, times(1)).deleteRestaurant(1L);
    }
} 