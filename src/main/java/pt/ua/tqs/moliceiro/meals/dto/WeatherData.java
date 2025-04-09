package pt.ua.tqs.moliceiro.meals.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherData {
    @JsonProperty("dt")
    private Long timestamp;
    
    @JsonProperty("main")
    private MainData main;
    
    @JsonProperty("weather")
    private List<Weather> weather;
    
    @JsonProperty("wind")
    private Wind wind;
} 