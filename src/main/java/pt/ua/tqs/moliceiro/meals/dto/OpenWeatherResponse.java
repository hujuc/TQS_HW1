package pt.ua.tqs.moliceiro.meals.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenWeatherResponse {
    @JsonProperty("list")
    private List<WeatherData> weatherData;
}