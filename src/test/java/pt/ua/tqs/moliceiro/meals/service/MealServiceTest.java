package pt.ua.tqs.moliceiro.meals.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.ua.tqs.moliceiro.meals.model.Meal;
import pt.ua.tqs.moliceiro.meals.model.Restaurant;
import pt.ua.tqs.moliceiro.meals.repository.MealRepository;
import pt.ua.tqs.moliceiro.meals.repository.RestaurantRepository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MealServiceTest {

    @Mock
    private MealRepository mealRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private MealService mealService;

    private Restaurant testRestaurant;
    private Meal testMeal;

    @BeforeEach
    void setUp() {
        // Setup test restaurant
        testRestaurant = new Restaurant();
        testRestaurant.setId(1L);
        testRestaurant.setName("Test Restaurant");
        testRestaurant.setLocation("Aveiro");
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
    }

    @Test
    void whenGetAllMeals_thenReturnAllMeals() {
        // Arrange
        List<Meal> expectedMeals = Arrays.asList(testMeal);
        when(mealRepository.findAll()).thenReturn(expectedMeals);

        // Act
        List<Meal> actualMeals = mealService.getAllMeals();

        // Assert
        assertThat(actualMeals).hasSize(1);
        assertThat(actualMeals.get(0)).isEqualTo(testMeal);
        verify(mealRepository, times(1)).findAll();
    }

    @Test
    void whenGetMealById_thenReturnMeal() {
        // Arrange
        when(mealRepository.findById(1L)).thenReturn(Optional.of(testMeal));

        // Act
        Optional<Meal> foundMeal = mealService.getMealById(1L);

        // Assert
        assertThat(foundMeal).isPresent();
        assertThat(foundMeal.get()).isEqualTo(testMeal);
        verify(mealRepository, times(1)).findById(1L);
    }

    @Test
    void whenGetMealsByRestaurantAndDateRange_thenReturnMeals() {
        // Arrange
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(7);
        List<Meal> expectedMeals = Arrays.asList(testMeal);
        when(mealRepository.findByRestaurantIdAndDateBetween(1L, startDate, endDate))
            .thenReturn(expectedMeals);

        // Act
        List<Meal> actualMeals = mealService.getMealsByRestaurantAndDateRange(1L, startDate, endDate);

        // Assert
        assertThat(actualMeals).hasSize(1);
        assertThat(actualMeals.get(0)).isEqualTo(testMeal);
        verify(mealRepository, times(1))
            .findByRestaurantIdAndDateBetween(1L, startDate, endDate);
    }

    @Test
    void whenCreateMealWithValidRestaurant_thenReturnSavedMeal() {
        // Arrange
        when(restaurantRepository.existsById(1L)).thenReturn(true);
        when(mealRepository.save(any(Meal.class))).thenReturn(testMeal);

        // Act
        Meal savedMeal = mealService.createMeal(testMeal);

        // Assert
        assertThat(savedMeal).isEqualTo(testMeal);
        verify(mealRepository, times(1)).save(testMeal);
    }

    @Test
    void whenCreateMealWithInvalidRestaurant_thenThrowException() {
        // Arrange
        when(restaurantRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> mealService.createMeal(testMeal))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Restaurant not found");
        verify(mealRepository, never()).save(any(Meal.class));
    }

    @Test
    void whenGetMealByIdNotFound_thenReturnEmpty() {
        // Arrange
        when(mealRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        Optional<Meal> foundMeal = mealService.getMealById(1L);

        // Assert
        assertThat(foundMeal).isEmpty();
        verify(mealRepository, times(1)).findById(1L);
    }

    @Test
    void whenUpdateMealNotFound_thenReturnEmpty() {
        // Arrange
        Meal updatedMeal = new Meal();
        when(mealRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        Optional<Meal> result = mealService.updateMeal(1L, updatedMeal);

        // Assert
        assertThat(result).isEmpty();
        verify(mealRepository, times(1)).findById(1L);
        verify(mealRepository, never()).save(any(Meal.class));
    }

    @Test
    void whenUpdateMeal_thenAllFieldsShouldBeUpdated() {
        // Arrange
        Meal updatedMeal = new Meal();
        updatedMeal.setName("Updated Name");
        updatedMeal.setDescription("Updated Description");
        updatedMeal.setPrice(15.0);
        updatedMeal.setDate(LocalDate.now().plusDays(1));
        updatedMeal.setMealType("dinner");

        when(mealRepository.findById(1L)).thenReturn(Optional.of(testMeal));
        when(mealRepository.save(any(Meal.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Optional<Meal> result = mealService.updateMeal(1L, updatedMeal);

        // Assert
        assertThat(result).isPresent();
        Meal savedMeal = result.get();
        assertThat(savedMeal.getName()).isEqualTo("Updated Name");
        assertThat(savedMeal.getDescription()).isEqualTo("Updated Description");
        assertThat(savedMeal.getPrice()).isEqualTo(15.0);
        assertThat(savedMeal.getDate()).isEqualTo(LocalDate.now().plusDays(1));
        assertThat(savedMeal.getMealType()).isEqualTo("dinner");
        verify(mealRepository, times(1)).findById(1L);
        verify(mealRepository, times(1)).save(any(Meal.class));
    }

    @Test
    void whenGetMealsByRestaurantAndDate_thenReturnMeals() {
        // Arrange
        LocalDate date = LocalDate.now();
        List<Meal> expectedMeals = Arrays.asList(testMeal);
        when(mealRepository.findByRestaurantIdAndDate(1L, date))
            .thenReturn(expectedMeals);

        // Act
        List<Meal> actualMeals = mealService.getMealsByRestaurantAndDate(1L, date);

        // Assert
        assertThat(actualMeals).hasSize(1);
        assertThat(actualMeals.get(0)).isEqualTo(testMeal);
        verify(mealRepository, times(1))
            .findByRestaurantIdAndDate(1L, date);
    }

    @Test
    void whenGetMealsByRestaurantAndDateAndType_thenReturnMeals() {
        // Arrange
        LocalDate date = LocalDate.now();
        String mealType = "lunch";
        List<Meal> expectedMeals = Arrays.asList(testMeal);
        when(mealRepository.findByRestaurantIdAndDateAndMealType(1L, date, mealType))
            .thenReturn(expectedMeals);

        // Act
        List<Meal> actualMeals = mealService.getMealsByRestaurantAndDateAndType(1L, date, mealType);

        // Assert
        assertThat(actualMeals).hasSize(1);
        assertThat(actualMeals.get(0)).isEqualTo(testMeal);
        verify(mealRepository, times(1))
            .findByRestaurantIdAndDateAndMealType(1L, date, mealType);
    }

    @Test
    void whenDeleteMeal_thenRepositoryShouldBeCalled() {
        // Act
        mealService.deleteMeal(1L);

        // Assert
        verify(mealRepository, times(1)).deleteById(1L);
    }
} 