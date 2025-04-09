package pt.ua.tqs.moliceiro.meals.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "weather_forecasts")
public class WeatherForecast {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private Double temperature;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Double humidity;

    @Column(name = "wind_speed", nullable = false)
    private Double windSpeed;

    @Column(nullable = false)
    private Long timestamp;
} 