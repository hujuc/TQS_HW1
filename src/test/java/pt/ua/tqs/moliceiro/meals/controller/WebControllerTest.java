package pt.ua.tqs.moliceiro.meals.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WebController.class)
class WebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void whenGetIndex_thenReturnIndexPage() throws Exception {
        mockMvc.perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(view().name("index.html"));
    }

    @Test
    void whenGetRestaurants_thenReturnRestaurantsPage() throws Exception {
        mockMvc.perform(get("/restaurants"))
            .andExpect(status().isOk())
            .andExpect(view().name("restaurants.html"));
    }

    @Test
    void whenGetRestaurantDetails_thenReturnRestaurantDetailsPage() throws Exception {
        mockMvc.perform(get("/restaurant-details"))
            .andExpect(status().isOk())
            .andExpect(view().name("restaurant-details.html"));
    }

    @Test
    void whenGetMeals_thenReturnMealsPage() throws Exception {
        mockMvc.perform(get("/meals"))
            .andExpect(status().isOk())
            .andExpect(view().name("meals.html"));
    }

    @Test
    void whenGetReservations_thenReturnReservationsPage() throws Exception {
        mockMvc.perform(get("/reservations"))
            .andExpect(status().isOk())
            .andExpect(view().name("reservations.html"));
    }

    @Test
    void whenGetReservationForm_thenReturnReservationFormPage() throws Exception {
        mockMvc.perform(get("/reservation-form"))
            .andExpect(status().isOk())
            .andExpect(view().name("reservation-form.html"));
    }
} 