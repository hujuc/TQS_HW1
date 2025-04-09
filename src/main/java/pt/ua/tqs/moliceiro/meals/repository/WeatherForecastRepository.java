package pt.ua.tqs.moliceiro.meals.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.ua.tqs.moliceiro.meals.model.WeatherForecast;

import java.time.LocalDate;
import java.util.Optional;

public interface WeatherForecastRepository extends JpaRepository<WeatherForecast, Long> {
    Optional<WeatherForecast> findByDateAndLocation(LocalDate date, String location);
} 