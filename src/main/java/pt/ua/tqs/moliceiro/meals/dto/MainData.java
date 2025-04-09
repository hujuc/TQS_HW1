package pt.ua.tqs.moliceiro.meals.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MainData {
    @JsonProperty("temp")
    private Double temperature;
    
    @JsonProperty("humidity")
    private Double humidity;
} 