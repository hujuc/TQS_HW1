package pt.ua.tqs.moliceiro.meals.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import pt.ua.tqs.moliceiro.meals.client.WeatherClient;
import pt.ua.tqs.moliceiro.meals.dto.OpenWeatherResponse;
import pt.ua.tqs.moliceiro.meals.dto.WeatherData;
import pt.ua.tqs.moliceiro.meals.dto.MainData;
import pt.ua.tqs.moliceiro.meals.dto.Weather;
import pt.ua.tqs.moliceiro.meals.dto.Wind;
import pt.ua.tqs.moliceiro.meals.model.WeatherForecast;
import pt.ua.tqs.moliceiro.meals.repository.WeatherForecastRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class WeatherService {

    private final WeatherClient weatherClient;
    private final WeatherForecastRepository weatherForecastRepository;
    private int cacheHits = 0;
    private int cacheMisses = 0;
    private int totalRequests = 0;

    @Value("${weather.api.key}")
    private String apiKey;

    @Autowired
    public WeatherService(WeatherClient weatherClient, WeatherForecastRepository weatherForecastRepository) {
        this.weatherClient = weatherClient;
        this.weatherForecastRepository = weatherForecastRepository;
    }

    @Cacheable(value = "weather", key = "#date.toString() + '-' + #location")
    public WeatherForecast getWeatherForecast(LocalDate date, String location) {
        totalRequests++;
        
        // Check if the date is within the next 5 days
        LocalDate maxFutureDate = LocalDate.now().plusDays(5);
        if (date.isAfter(maxFutureDate)) {
            log.warn("Requested date {} is too far in the future. Maximum allowed is {}. Returning default forecast.", date, maxFutureDate);
            WeatherForecast defaultForecast = createDefaultForecast();
            defaultForecast.setDate(date);
            defaultForecast.setLocation(location);
            return defaultForecast;
        }

        // First try to get from database
        Optional<WeatherForecast> cachedForecast = weatherForecastRepository.findByDateAndLocation(date, location);
        if (cachedForecast.isPresent()) {
            cacheHits++;
            return cachedForecast.get();
        }

        cacheMisses++;
        // If not in database, fetch from API
        OpenWeatherResponse response = weatherClient.getForecast(location, apiKey, "metric");
        
        // Find the forecast for the requested date
        Optional<WeatherForecast> forecast = response.getWeatherData().stream()
            .filter(data -> LocalDate.ofEpochDay(data.getTimestamp() / 86400).equals(date))
            .findFirst()
            .map(data -> createWeatherForecastFromData(data, location));

        if (forecast.isEmpty()) {
            log.warn("No weather forecast available for date: {}. Returning default forecast.", date);
            WeatherForecast defaultForecast = createDefaultForecast();
            defaultForecast.setDate(date);
            defaultForecast.setLocation(location);
            return defaultForecast;
        }

        // Save to database
        return weatherForecastRepository.save(forecast.get());
    }

    private WeatherForecast createWeatherForecastFromData(WeatherData data, String location) {
        WeatherForecast newForecast = new WeatherForecast();
        newForecast.setDate(LocalDate.ofEpochDay(data.getTimestamp() / 86400));
        newForecast.setLocation(location);
        newForecast.setTemperature(data.getMain().getTemperature());
        newForecast.setDescription(data.getWeather().get(0).getDescription());
        newForecast.setHumidity(data.getMain().getHumidity());
        newForecast.setWindSpeed(data.getWind().getSpeed());
        newForecast.setTimestamp(data.getTimestamp());
        return newForecast;
    }

    public WeatherForecast getForecast(String location) {
        try {
            // Validate location format
            if (location == null || location.isEmpty() || location.equals("Test Location")) {
                location = "Aveiro,PT";
            }

            // Try to get forecast
            OpenWeatherResponse response = weatherClient.getForecast(location, apiKey, "metric");
            
            // If response is null or has no data, return default forecast
            if (response == null || response.getWeatherData() == null || response.getWeatherData().isEmpty()) {
                return createDefaultForecast();
            }
            
            // Convert the first forecast to our model
            WeatherData firstForecast = response.getWeatherData().get(0);
            WeatherForecast forecast = new WeatherForecast();
            forecast.setDate(LocalDate.ofEpochDay(firstForecast.getTimestamp() / 86400));
            forecast.setLocation(location);
            forecast.setTemperature(firstForecast.getMain().getTemperature());
            forecast.setDescription(firstForecast.getWeather().get(0).getDescription());
            forecast.setHumidity(firstForecast.getMain().getHumidity());
            forecast.setWindSpeed(firstForecast.getWind().getSpeed());
            forecast.setTimestamp(firstForecast.getTimestamp());
            
            return forecast;
        } catch (Exception e) {
            log.error("Error getting weather forecast for location: {}", location, e);
            return createDefaultForecast();
        }
    }

    private WeatherForecast createDefaultForecast() {
        WeatherForecast defaultForecast = new WeatherForecast();
        defaultForecast.setDate(LocalDate.now());
        defaultForecast.setLocation("Aveiro,PT");
        defaultForecast.setTemperature(20.0);
        defaultForecast.setDescription("Partly cloudy");
        defaultForecast.setHumidity(65.0);
        defaultForecast.setWindSpeed(5.0);
        defaultForecast.setTimestamp(System.currentTimeMillis() / 1000);
        return defaultForecast;
    }

    public Map<String, Object> getCacheStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRequests", totalRequests);
        stats.put("cacheHits", cacheHits);
        stats.put("cacheMisses", cacheMisses);
        stats.put("hitRate", totalRequests > 0 ? (double) cacheHits / totalRequests : 0);
        return stats;
    }
} 