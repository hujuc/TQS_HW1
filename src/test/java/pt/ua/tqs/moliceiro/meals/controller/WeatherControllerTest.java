package pt.ua.tqs.moliceiro.meals.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.ua.tqs.moliceiro.meals.model.WeatherForecast;
import pt.ua.tqs.moliceiro.meals.service.WeatherService;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WeatherControllerTest {

    @Mock
    private WeatherService weatherService;

    @InjectMocks
    private WeatherController weatherController;

    private WeatherForecast testForecast;
    private Map<String, Object> testCacheStats;

    @BeforeEach
    void setUp() {
        // Setup test weather forecast
        testForecast = new WeatherForecast();
        testForecast.setId(1L);
        testForecast.setDate(LocalDate.now());
        testForecast.setLocation("Aveiro");
        testForecast.setDescription("Sunny");
        testForecast.setTemperature(25.0);
        testForecast.setHumidity(60.0);
        testForecast.setWindSpeed(10.0);
        testForecast.setTimestamp(System.currentTimeMillis());

        // Setup test cache stats
        testCacheStats = new HashMap<>();
        testCacheStats.put("totalRequests", 100);
        testCacheStats.put("hits", 80);
        testCacheStats.put("misses", 20);
        testCacheStats.put("hitRate", 0.8);
    }

    @Test
    void whenGetWeatherForecastWithValidDate_thenReturnForecast() {
        // Arrange
        LocalDate date = LocalDate.now();
        String location = "Aveiro";
        when(weatherService.getWeatherForecast(date, location)).thenReturn(testForecast);

        // Act
        ResponseEntity<?> response = weatherController.getWeatherForecast(date, location);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(testForecast);
        verify(weatherService, times(1)).getWeatherForecast(date, location);
    }

    @Test
    void whenGetWeatherForecastWithNullDate_thenUseCurrentDate() {
        // Arrange
        String location = "Aveiro";
        when(weatherService.getWeatherForecast(any(), eq(location))).thenReturn(testForecast);

        // Act
        ResponseEntity<?> response = weatherController.getWeatherForecast(null, location);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(testForecast);
        verify(weatherService, times(1)).getWeatherForecast(any(), eq(location));
    }

    @Test
    void whenGetWeatherForecastWithPastDate_thenReturnBadRequest() {
        // Arrange
        LocalDate pastDate = LocalDate.now().minusDays(1);
        String location = "Aveiro";

        // Act
        ResponseEntity<?> response = weatherController.getWeatherForecast(pastDate, location);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        @SuppressWarnings("unchecked")
        Map<String, String> error = (Map<String, String>) response.getBody();
        assertThat(error).containsKey("error");
        assertThat(error).containsKey("message");
        verify(weatherService, never()).getWeatherForecast(any(), any());
    }

    @Test
    void whenGetWeatherForecastWithServiceError_thenReturnNotFound() {
        // Arrange
        LocalDate date = LocalDate.now();
        String location = "Aveiro";
        when(weatherService.getWeatherForecast(date, location))
            .thenThrow(new RuntimeException("API Error"));

        // Act
        ResponseEntity<?> response = weatherController.getWeatherForecast(date, location);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        @SuppressWarnings("unchecked")
        Map<String, String> error = (Map<String, String>) response.getBody();
        assertThat(error).containsKey("error");
        assertThat(error).containsKey("message");
        verify(weatherService, times(1)).getWeatherForecast(date, location);
    }

    @Test
    void whenGetCacheStats_thenReturnStats() {
        // Arrange
        when(weatherService.getCacheStats()).thenReturn(testCacheStats);

        // Act
        ResponseEntity<Map<String, Object>> response = weatherController.getCacheStats();

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(testCacheStats);
        verify(weatherService, times(1)).getCacheStats();
    }
} 