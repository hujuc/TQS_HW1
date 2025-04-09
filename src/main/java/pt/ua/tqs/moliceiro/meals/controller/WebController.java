package pt.ua.tqs.moliceiro.meals.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/")
    public String index() {
        return "index.html";
    }

    @GetMapping("/restaurants")
    public String restaurants() {
        return "restaurants.html";
    }

    @GetMapping("/restaurant-details")
    public String restaurantDetails() {
        return "restaurant-details.html";
    }

    @GetMapping("/meals")
    public String meals() {
        return "meals.html";
    }

    @GetMapping("/reservations")
    public String reservations() {
        return "reservations.html";
    }

    @GetMapping("/reservation-form")
    public String reservationForm() {
        return "reservation-form.html";
    }
} 