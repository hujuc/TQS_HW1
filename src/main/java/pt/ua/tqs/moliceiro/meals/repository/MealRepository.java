package pt.ua.tqs.moliceiro.meals.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pt.ua.tqs.moliceiro.meals.model.Meal;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MealRepository extends JpaRepository<Meal, Long> {
    List<Meal> findByRestaurantIdAndDateBetween(Long restaurantId, LocalDate startDate, LocalDate endDate);
    List<Meal> findByRestaurantIdAndDateAndMealType(Long restaurantId, LocalDate date, String mealType);
    List<Meal> findByRestaurantIdAndDate(Long restaurantId, LocalDate date);
} 