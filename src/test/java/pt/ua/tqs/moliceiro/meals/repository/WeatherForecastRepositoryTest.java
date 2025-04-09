package pt.ua.tqs.moliceiro.meals.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import pt.ua.tqs.moliceiro.meals.model.WeatherForecast;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class WeatherForecastRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private WeatherForecastRepository weatherForecastRepository;

    private WeatherForecast testForecast;
    private LocalDate today;

    @BeforeEach
    void setUp() {
        weatherForecastRepository.deleteAll();
        
        today = LocalDate.now();
        
        // Create a test weather forecast
        testForecast = new WeatherForecast();
        testForecast.setDate(today);
        testForecast.setLocation("Test Location");
        testForecast.setTemperature(20.0);
        testForecast.setDescription("Sunny");
        testForecast.setHumidity(65.0);
        testForecast.setWindSpeed(5.0);
        testForecast.setTimestamp(System.currentTimeMillis());
    }

    @Test
    void whenSaveWeatherForecast_thenReturnSavedForecast() {
        // Save the weather forecast
        WeatherForecast savedForecast = weatherForecastRepository.save(testForecast);

        // Verify the weather forecast was saved
        assertThat(savedForecast.getId()).isNotNull();
        assertThat(savedForecast.getDate()).isEqualTo(today);
        assertThat(savedForecast.getLocation()).isEqualTo("Test Location");
        assertThat(savedForecast.getTemperature()).isEqualTo(20.0);
        assertThat(savedForecast.getDescription()).isEqualTo("Sunny");
        assertThat(savedForecast.getHumidity()).isEqualTo(65.0);
        assertThat(savedForecast.getWindSpeed()).isEqualTo(5.0);
    }

    @Test
    void whenFindByDateAndLocation_thenReturnForecast() {
        // Save the weather forecast
        weatherForecastRepository.save(testForecast);

        // Find the weather forecast by date and location
        Optional<WeatherForecast> foundForecast = weatherForecastRepository.findByDateAndLocation(today, "Test Location");

        // Verify the weather forecast was found
        assertThat(foundForecast).isPresent();
        assertThat(foundForecast.get().getDate()).isEqualTo(today);
        assertThat(foundForecast.get().getLocation()).isEqualTo("Test Location");
    }

    @Test
    void whenFindByDateAndLocationNotFound_thenReturnEmpty() {
        // Try to find a non-existent weather forecast
        Optional<WeatherForecast> foundForecast = weatherForecastRepository.findByDateAndLocation(today, "Non-existent Location");

        // Verify no weather forecast was found
        assertThat(foundForecast).isEmpty();
    }

    @Test
    void whenFindByDifferentDate_thenReturnEmpty() {
        // Save the weather forecast
        weatherForecastRepository.save(testForecast);

        // Try to find the weather forecast with a different date
        LocalDate differentDate = today.plusDays(1);
        Optional<WeatherForecast> foundForecast = weatherForecastRepository.findByDateAndLocation(differentDate, "Test Location");

        // Verify no weather forecast was found
        assertThat(foundForecast).isEmpty();
    }

    @Test
    void whenFindByDifferentLocation_thenReturnEmpty() {
        // Save the weather forecast
        weatherForecastRepository.save(testForecast);

        // Try to find the weather forecast with a different location
        Optional<WeatherForecast> foundForecast = weatherForecastRepository.findByDateAndLocation(today, "Different Location");

        // Verify no weather forecast was found
        assertThat(foundForecast).isEmpty();
    }

    @org.junit.jupiter.api.AfterAll
    static void tearDown() {
        postgres.close();
    }
} 