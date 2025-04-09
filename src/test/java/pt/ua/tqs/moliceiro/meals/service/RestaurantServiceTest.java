package pt.ua.tqs.moliceiro.meals.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.ua.tqs.moliceiro.meals.model.Restaurant;
import pt.ua.tqs.moliceiro.meals.repository.RestaurantRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestaurantServiceTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private RestaurantService restaurantService;

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
        when(restaurantRepository.findAll()).thenReturn(expectedRestaurants);

        // Act
        List<Restaurant> actualRestaurants = restaurantService.getAllRestaurants();

        // Assert
        assertThat(actualRestaurants).hasSize(1);
        assertThat(actualRestaurants.get(0)).isEqualTo(testRestaurant);
        verify(restaurantRepository, times(1)).findAll();
    }

    @Test
    void whenGetRestaurantById_thenReturnRestaurant() {
        // Arrange
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(testRestaurant));

        // Act
        Optional<Restaurant> foundRestaurant = restaurantService.getRestaurantById(1L);

        // Assert
        assertThat(foundRestaurant).isPresent();
        assertThat(foundRestaurant.get()).isEqualTo(testRestaurant);
        verify(restaurantRepository, times(1)).findById(1L);
    }

    @Test
    void whenGetRestaurantByIdNotFound_thenReturnEmpty() {
        // Arrange
        when(restaurantRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        Optional<Restaurant> foundRestaurant = restaurantService.getRestaurantById(1L);

        // Assert
        assertThat(foundRestaurant).isEmpty();
        verify(restaurantRepository, times(1)).findById(1L);
    }

    @Test
    void whenCreateRestaurant_thenReturnSavedRestaurant() {
        // Arrange
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(testRestaurant);

        // Act
        Restaurant savedRestaurant = restaurantService.createRestaurant(testRestaurant);

        // Assert
        assertThat(savedRestaurant).isEqualTo(testRestaurant);
        verify(restaurantRepository, times(1)).save(testRestaurant);
    }

    @Test
    void whenUpdateRestaurant_thenReturnUpdatedRestaurant() {
        // Arrange
        Restaurant updatedRestaurant = new Restaurant();
        updatedRestaurant.setName("Updated Name");
        updatedRestaurant.setLocation("Updated Location");
        updatedRestaurant.setCapacity(150);
        updatedRestaurant.setOperatingHours("10:00-18:00");

        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(testRestaurant));
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(updatedRestaurant);

        // Act
        Optional<Restaurant> result = restaurantService.updateRestaurant(1L, updatedRestaurant);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Updated Name");
        verify(restaurantRepository, times(1)).findById(1L);
        verify(restaurantRepository, times(1)).save(any(Restaurant.class));
    }

    @Test
    void whenUpdateRestaurantNotFound_thenReturnEmpty() {
        // Arrange
        Restaurant updatedRestaurant = new Restaurant();
        when(restaurantRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        Optional<Restaurant> result = restaurantService.updateRestaurant(1L, updatedRestaurant);

        // Assert
        assertThat(result).isEmpty();
        verify(restaurantRepository, times(1)).findById(1L);
        verify(restaurantRepository, never()).save(any(Restaurant.class));
    }

    @Test
    void whenUpdateRestaurant_thenAllFieldsShouldBeUpdated() {
        // Arrange
        Restaurant updatedRestaurant = new Restaurant();
        updatedRestaurant.setName("Updated Name");
        updatedRestaurant.setLocation("Updated Location");
        updatedRestaurant.setCapacity(150);
        updatedRestaurant.setOperatingHours("10:00-18:00");

        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(testRestaurant));
        when(restaurantRepository.save(any(Restaurant.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Optional<Restaurant> result = restaurantService.updateRestaurant(1L, updatedRestaurant);

        // Assert
        assertThat(result).isPresent();
        Restaurant savedRestaurant = result.get();
        assertThat(savedRestaurant.getName()).isEqualTo("Updated Name");
        assertThat(savedRestaurant.getLocation()).isEqualTo("Updated Location");
        assertThat(savedRestaurant.getCapacity()).isEqualTo(150);
        assertThat(savedRestaurant.getOperatingHours()).isEqualTo("10:00-18:00");
        verify(restaurantRepository, times(1)).findById(1L);
        verify(restaurantRepository, times(1)).save(any(Restaurant.class));
    }

    @Test
    void whenDeleteRestaurant_thenRepositoryShouldBeCalled() {
        // Act
        restaurantService.deleteRestaurant(1L);

        // Assert
        verify(restaurantRepository, times(1)).deleteById(1L);
    }
} 