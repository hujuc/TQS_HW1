package pt.ua.tqs.moliceiro.meals.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pt.ua.tqs.moliceiro.meals.dto.OpenWeatherResponse;

@FeignClient(name = "weather-client", url = "${weather.api.base-url}")
public interface WeatherClient {

    @GetMapping("/forecast")
    OpenWeatherResponse getForecast(
        @RequestParam("q") String location,
        @RequestParam("appid") String apiKey,
        @RequestParam("units") String units
    );
} 