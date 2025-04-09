package pt.ua.tqs.moliceiro.meals.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.ua.tqs.moliceiro.meals.client.WeatherClient;
import pt.ua.tqs.moliceiro.meals.dto.*;
import pt.ua.tqs.moliceiro.meals.model.WeatherForecast;
import pt.ua.tqs.moliceiro.meals.repository.WeatherForecastRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WeatherServiceTest {

    @Mock
    private WeatherClient weatherClient;

    @Mock
    private WeatherForecastRepository weatherForecastRepository;

    @InjectMocks
    private WeatherService weatherService;

    private OpenWeatherResponse testResponse;
    private WeatherForecast testForecast;

    @BeforeEach
    void setUp() {
        // Setup test response
        MainData mainData = new MainData();
        mainData.setTemperature(20.0);
        mainData.setHumidity(65.0);

        Weather weather = new Weather();
        weather.setDescription("clear sky");

        Wind wind = new Wind();
        wind.setSpeed(5.0);

        WeatherData weatherData = new WeatherData();
        LocalDateTime dateTime = LocalDate.of(2024, 4, 7).atStartOfDay();
        weatherData.setTimestamp(dateTime.toEpochSecond(ZoneOffset.UTC));
        weatherData.setMain(mainData);
        weatherData.setWeather(Arrays.asList(weather));
        weatherData.setWind(wind);

        testResponse = new OpenWeatherResponse();
        testResponse.setWeatherData(Arrays.asList(weatherData));

        // Setup test forecast
        testForecast = new WeatherForecast();
        testForecast.setId(1L);
        testForecast.setDate(LocalDate.of(2024, 4, 7));
        testForecast.setLocation("Aveiro");
        testForecast.setTemperature(20.0);
        testForecast.setDescription("clear sky");
        testForecast.setHumidity(65.0);
        testForecast.setWindSpeed(5.0);
        testForecast.setTimestamp(dateTime.toEpochSecond(ZoneOffset.UTC));
    }

    @Test
    void whenGetWeatherForecastFromCache_thenReturnCachedForecast() {
        // Arrange
        when(weatherForecastRepository.findByDateAndLocation(any(), any()))
            .thenReturn(Optional.of(testForecast));

        // Act
        WeatherForecast result = weatherService.getWeatherForecast(
            LocalDate.of(2024, 4, 7), "Aveiro");

        // Assert
        assertThat(result).isEqualTo(testForecast);
        verify(weatherClient, never()).getForecast(any(), any(), any());
    }

    @Test
    void whenGetWeatherForecastNotInCache_thenFetchFromApi() {
        // Arrange
        when(weatherForecastRepository.findByDateAndLocation(any(), any()))
            .thenReturn(Optional.empty());
        when(weatherClient.getForecast(any(), any(), any()))
            .thenReturn(testResponse);
        when(weatherForecastRepository.save(any()))
            .thenReturn(testForecast);

        // Act
        WeatherForecast result = weatherService.getWeatherForecast(
            LocalDate.of(2024, 4, 7), "Aveiro");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getTemperature()).isEqualTo(20.0);
        assertThat(result.getDescription()).isEqualTo("clear sky");
        verify(weatherClient, times(1)).getForecast(any(), any(), any());
        verify(weatherForecastRepository, times(1)).save(any());
    }

    @Test
    void whenGetForecastWithNullLocation_thenReturnDefaultForecast() {
        // Act
        WeatherForecast result = weatherService.getForecast(null);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getLocation()).isEqualTo("Aveiro,PT");
        assertThat(result.getTemperature()).isEqualTo(20.0);
        assertThat(result.getDescription()).isEqualTo("Partly cloudy");
    }

    @Test
    void whenGetForecastWithEmptyLocation_thenReturnDefaultForecast() {
        // Act
        WeatherForecast result = weatherService.getForecast("");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getLocation()).isEqualTo("Aveiro,PT");
        assertThat(result.getTemperature()).isEqualTo(20.0);
        assertThat(result.getDescription()).isEqualTo("Partly cloudy");
    }

    @Test
    void whenGetForecastWithTestLocation_thenReturnDefaultForecast() {
        // Act
        WeatherForecast result = weatherService.getForecast("Test Location");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getLocation()).isEqualTo("Aveiro,PT");
        assertThat(result.getTemperature()).isEqualTo(20.0);
        assertThat(result.getDescription()).isEqualTo("Partly cloudy");
    }

    @Test
    void whenGetForecastWithValidLocation_thenReturnForecast() {
        // Arrange
        when(weatherClient.getForecast(any(), any(), any()))
            .thenReturn(testResponse);

        // Act
        WeatherForecast result = weatherService.getForecast("Aveiro,PT");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getLocation()).isEqualTo("Aveiro,PT");
        assertThat(result.getTemperature()).isEqualTo(20.0);
        assertThat(result.getDescription()).isEqualTo("clear sky");
        assertThat(result.getHumidity()).isEqualTo(65.0);
        assertThat(result.getWindSpeed()).isEqualTo(5.0);
    }

    @Test
    void whenGetForecastWithApiError_thenReturnDefaultForecast() {
        // Arrange
        when(weatherClient.getForecast(any(), any(), any()))
            .thenThrow(new RuntimeException("API Error"));

        // Act
        WeatherForecast result = weatherService.getForecast("Aveiro,PT");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getLocation()).isEqualTo("Aveiro,PT");
        assertThat(result.getTemperature()).isEqualTo(20.0);
        assertThat(result.getDescription()).isEqualTo("Partly cloudy");
    }

    @Test
    void whenGetCacheStats_thenReturnCorrectStats() {
        // Arrange
        when(weatherForecastRepository.findByDateAndLocation(any(), any()))
            .thenReturn(Optional.empty())
            .thenReturn(Optional.of(testForecast));
        when(weatherClient.getForecast(any(), any(), any()))
            .thenReturn(testResponse);
        when(weatherForecastRepository.save(any()))
            .thenReturn(testForecast);

        // Act - First call (cache miss)
        weatherService.getWeatherForecast(LocalDate.of(2024, 4, 7), "Aveiro");
        // Second call (cache hit)
        weatherService.getWeatherForecast(LocalDate.of(2024, 4, 7), "Aveiro");

        Map<String, Object> stats = weatherService.getCacheStats();

        // Assert
        assertThat(stats)
            .isNotNull()
            .containsEntry("totalRequests", 2)
            .containsEntry("cacheHits", 1)
            .containsEntry("cacheMisses", 1)
            .containsEntry("hitRate", 0.5);
    }

    @Test
    void whenGetForecastWithEmptyResponse_thenReturnDefaultForecast() {
        // Arrange
        OpenWeatherResponse emptyResponse = new OpenWeatherResponse();
        emptyResponse.setWeatherData(new ArrayList<>());
        when(weatherClient.getForecast(any(), any(), any()))
            .thenReturn(emptyResponse);

        // Act
        WeatherForecast result = weatherService.getForecast("Aveiro,PT");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getLocation()).isEqualTo("Aveiro,PT");
        assertThat(result.getTemperature()).isEqualTo(20.0);
        assertThat(result.getDescription()).isEqualTo("Partly cloudy");
    }

    @Test
    void whenGetWeatherForecastWithDateNotInResponse_thenReturnDefaultForecast() {
        // Arrange
        when(weatherForecastRepository.findByDateAndLocation(any(), any()))
            .thenReturn(Optional.empty());
        when(weatherClient.getForecast(any(), any(), any()))
            .thenReturn(testResponse);

        // Act
        LocalDate targetDate = LocalDate.of(2024, 4, 8);
        WeatherForecast result = weatherService.getWeatherForecast(targetDate, "Aveiro");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getDate()).isEqualTo(targetDate);
        assertThat(result.getLocation()).isEqualTo("Aveiro");
        assertThat(result.getTemperature()).isEqualTo(20.0);
        assertThat(result.getDescription()).isEqualTo("Partly cloudy");
        assertThat(result.getHumidity()).isEqualTo(65.0);
        assertThat(result.getWindSpeed()).isEqualTo(5.0);
    }
}