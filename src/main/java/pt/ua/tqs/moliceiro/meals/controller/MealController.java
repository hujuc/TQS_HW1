package pt.ua.tqs.moliceiro.meals.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ua.tqs.moliceiro.meals.model.Meal;
import pt.ua.tqs.moliceiro.meals.service.MealService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/meals")
@Tag(name = "Meal", description = "Meal management API")
public class MealController {

    private final MealService mealService;

    @Autowired
    public MealController(MealService mealService) {
        this.mealService = mealService;
    }

    @GetMapping
    @Operation(summary = "Get all meals", description = "Returns a list of all meals")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved all meals")
    public ResponseEntity<List<Meal>> getAllMeals() {
        return ResponseEntity.ok(mealService.getAllMeals());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get meal by ID", description = "Returns a meal by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the meal"),
        @ApiResponse(responseCode = "404", description = "Meal not found")
    })
    public ResponseEntity<Meal> getMealById(
            @Parameter(description = "ID of the meal to retrieve") @PathVariable Long id) {
        return mealService.getMealById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/restaurant/{restaurantId}")
    @Operation(summary = "Get meals by restaurant and date range", 
              description = "Returns meals for a specific restaurant within a date range")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved meals")
    public ResponseEntity<List<Meal>> getMealsByRestaurantAndDateRange(
            @Parameter(description = "ID of the restaurant") @PathVariable Long restaurantId,
            @Parameter(description = "Start date (YYYY-MM-DD)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (YYYY-MM-DD)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(mealService.getMealsByRestaurantAndDateRange(restaurantId, startDate, endDate));
    }

    @GetMapping("/restaurant/{restaurantId}/date/{date}")
    @Operation(summary = "Get meals by restaurant and date", 
              description = "Returns meals for a specific restaurant on a specific date")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved meals")
    public ResponseEntity<List<Meal>> getMealsByRestaurantAndDate(
            @Parameter(description = "ID of the restaurant") @PathVariable Long restaurantId,
            @Parameter(description = "Date (YYYY-MM-DD)") @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(mealService.getMealsByRestaurantAndDate(restaurantId, date));
    }

    @GetMapping("/restaurant/{restaurantId}/date/{date}/type/{mealType}")
    @Operation(summary = "Get meals by restaurant, date and type", 
              description = "Returns meals for a specific restaurant on a specific date and meal type")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved meals")
    public ResponseEntity<List<Meal>> getMealsByRestaurantAndDateAndType(
            @Parameter(description = "ID of the restaurant") @PathVariable Long restaurantId,
            @Parameter(description = "Date (YYYY-MM-DD)") @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Parameter(description = "Type of meal (breakfast, lunch, dinner)") @PathVariable String mealType) {
        return ResponseEntity.ok(mealService.getMealsByRestaurantAndDateAndType(restaurantId, date, mealType));
    }

    @PostMapping
    public ResponseEntity<Meal> createMeal(@RequestBody Meal meal) {
        try {
            return ResponseEntity.ok(mealService.createMeal(meal));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Meal> updateMeal(@PathVariable Long id, @RequestBody Meal updatedMeal) {
        return mealService.updateMeal(id, updatedMeal)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMeal(@PathVariable Long id) {
        mealService.deleteMeal(id);
        return ResponseEntity.noContent().build();
    }
} 