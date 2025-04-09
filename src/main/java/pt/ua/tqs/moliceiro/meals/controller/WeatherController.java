package pt.ua.tqs.moliceiro.meals.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ua.tqs.moliceiro.meals.model.WeatherForecast;
import pt.ua.tqs.moliceiro.meals.service.WeatherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    private final WeatherService weatherService;

    @Autowired
    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/forecast")
    public ResponseEntity<?> getWeatherForecast(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam String location) {
        try {
            if (date == null) {
                date = LocalDate.now();
            } else if (date.isBefore(LocalDate.now())) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Cannot get weather forecast for past dates");
                error.put("message", "Please use current or future dates");
                return ResponseEntity.badRequest().body(error);
            }
            WeatherForecast forecast = weatherService.getWeatherForecast(date, location);
            return ResponseEntity.ok(forecast);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Weather forecast not available");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @GetMapping("/cache-stats")
    @Operation(summary = "Get weather cache statistics", 
              description = "Returns statistics about the weather forecast cache")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved cache statistics")
    public ResponseEntity<Map<String, Object>> getCacheStats() {
        Map<String, Object> stats = weatherService.getCacheStats();
        return ResponseEntity.ok(stats);
    }
} 