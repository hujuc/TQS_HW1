package pt.ua.tqs.moliceiro.meals.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pt.ua.tqs.moliceiro.meals.model.Meal;
import pt.ua.tqs.moliceiro.meals.repository.MealRepository;
import pt.ua.tqs.moliceiro.meals.repository.RestaurantRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class MealService {

    private final MealRepository mealRepository;
    private final RestaurantRepository restaurantRepository;

    @Autowired
    public MealService(MealRepository mealRepository, 
                      RestaurantRepository restaurantRepository) {
        this.mealRepository = mealRepository;
        this.restaurantRepository = restaurantRepository;
    }

    public List<Meal> getAllMeals() {
        return mealRepository.findAll();
    }

    public Optional<Meal> getMealById(Long id) {
        return mealRepository.findById(id);
    }

    public List<Meal> getMealsByRestaurantAndDateRange(Long restaurantId, LocalDate startDate, LocalDate endDate) {
        return mealRepository.findByRestaurantIdAndDateBetween(restaurantId, startDate, endDate);
    }

    public List<Meal> getMealsByRestaurantAndDate(Long restaurantId, LocalDate date) {
        return mealRepository.findByRestaurantIdAndDate(restaurantId, date);
    }

    public List<Meal> getMealsByRestaurantAndDateAndType(Long restaurantId, LocalDate date, String mealType) {
        return mealRepository.findByRestaurantIdAndDateAndMealType(restaurantId, date, mealType);
    }

    public Meal createMeal(Meal meal) {
        // Verify if the restaurant exists
        if (!restaurantRepository.existsById(meal.getRestaurant().getId())) {
            throw new IllegalArgumentException("Restaurant not found");
        }
        return mealRepository.save(meal);
    }

    public Optional<Meal> updateMeal(Long id, Meal updatedMeal) {
        return mealRepository.findById(id)
            .map(existingMeal -> {
                updateMealFields(existingMeal, updatedMeal);
                return mealRepository.save(existingMeal);
            });
    }

    private void updateMealFields(Meal existingMeal, Meal updatedMeal) {
        existingMeal.setName(updatedMeal.getName());
        existingMeal.setDescription(updatedMeal.getDescription());
        existingMeal.setPrice(updatedMeal.getPrice());
        existingMeal.setDate(updatedMeal.getDate());
        existingMeal.setMealType(updatedMeal.getMealType());
    }

    public void deleteMeal(Long id) {
        mealRepository.deleteById(id);
    }
} 