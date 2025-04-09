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
import pt.ua.tqs.moliceiro.meals.model.Restaurant;
import pt.ua.tqs.moliceiro.meals.service.MealService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MealControllerTest {

    @Mock
    private MealService mealService;

    @InjectMocks
    private MealController mealController;

    private Restaurant testRestaurant;
    private Meal testMeal;
    private List<Meal> testMeals;

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

        testMeals = Arrays.asList(testMeal);
    }

    @Test
    void whenGetAllMeals_thenReturnMeals() {
        // Arrange
        when(mealService.getAllMeals()).thenReturn(testMeals);

        // Act
        ResponseEntity<List<Meal>> response = mealController.getAllMeals();

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(testMeals);
        verify(mealService, times(1)).getAllMeals();
    }

    @Test
    void whenGetMealById_thenReturnMeal() {
        // Arrange
        when(mealService.getMealById(1L)).thenReturn(Optional.of(testMeal));

        // Act
        ResponseEntity<Meal> response = mealController.getMealById(1L);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(testMeal);
        verify(mealService, times(1)).getMealById(1L);
    }

    @Test
    void whenGetMealByIdNotFound_thenReturnNotFound() {
        // Arrange
        when(mealService.getMealById(1L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Meal> response = mealController.getMealById(1L);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
        verify(mealService, times(1)).getMealById(1L);
    }

    @Test
    void whenGetMealsByRestaurantAndDateRange_thenReturnMeals() {
        // Arrange
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(7);
        when(mealService.getMealsByRestaurantAndDateRange(1L, startDate, endDate)).thenReturn(testMeals);

        // Act
        ResponseEntity<List<Meal>> response = mealController.getMealsByRestaurantAndDateRange(1L, startDate, endDate);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(testMeals);
        verify(mealService, times(1)).getMealsByRestaurantAndDateRange(1L, startDate, endDate);
    }

    @Test
    void whenGetMealsByRestaurantAndDate_thenReturnMeals() {
        // Arrange
        LocalDate date = LocalDate.now();
        when(mealService.getMealsByRestaurantAndDate(1L, date)).thenReturn(testMeals);

        // Act
        ResponseEntity<List<Meal>> response = mealController.getMealsByRestaurantAndDate(1L, date);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(testMeals);
        verify(mealService, times(1)).getMealsByRestaurantAndDate(1L, date);
    }

    @Test
    void whenGetMealsByRestaurantAndDateAndType_thenReturnMeals() {
        // Arrange
        LocalDate date = LocalDate.now();
        String mealType = "lunch";
        when(mealService.getMealsByRestaurantAndDateAndType(1L, date, mealType)).thenReturn(testMeals);

        // Act
        ResponseEntity<List<Meal>> response = mealController.getMealsByRestaurantAndDateAndType(1L, date, mealType);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(testMeals);
        verify(mealService, times(1)).getMealsByRestaurantAndDateAndType(1L, date, mealType);
    }

    @Test
    void whenCreateValidMeal_thenReturnCreatedMeal() {
        // Arrange
        when(mealService.createMeal(any(Meal.class))).thenReturn(testMeal);

        // Act
        ResponseEntity<Meal> response = mealController.createMeal(testMeal);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(testMeal);
        verify(mealService, times(1)).createMeal(testMeal);
    }

    @Test
    void whenCreateInvalidMeal_thenReturnBadRequest() {
        // Arrange
        when(mealService.createMeal(any(Meal.class)))
            .thenThrow(new IllegalArgumentException("Invalid meal"));

        // Act
        ResponseEntity<Meal> response = mealController.createMeal(testMeal);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(mealService, times(1)).createMeal(testMeal);
    }

    @Test
    void whenUpdateValidMeal_thenReturnUpdatedMeal() {
        // Arrange
        when(mealService.updateMeal(eq(1L), any(Meal.class))).thenReturn(Optional.of(testMeal));

        // Act
        ResponseEntity<Meal> response = mealController.updateMeal(1L, testMeal);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(testMeal);
        verify(mealService, times(1)).updateMeal(1L, testMeal);
    }

    @Test
    void whenUpdateNonExistentMeal_thenReturnNotFound() {
        // Arrange
        when(mealService.updateMeal(eq(1L), any(Meal.class))).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Meal> response = mealController.updateMeal(1L, testMeal);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
        verify(mealService, times(1)).updateMeal(1L, testMeal);
    }

    @Test
    void whenDeleteMeal_thenReturnNoContent() {
        // Act
        ResponseEntity<Void> response = mealController.deleteMeal(1L);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(mealService, times(1)).deleteMeal(1L);
    }
} 