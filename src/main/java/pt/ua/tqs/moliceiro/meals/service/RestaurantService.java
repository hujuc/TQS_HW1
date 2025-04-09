package pt.ua.tqs.moliceiro.meals.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pt.ua.tqs.moliceiro.meals.model.Restaurant;
import pt.ua.tqs.moliceiro.meals.repository.RestaurantRepository;

import java.util.List;
import java.util.Optional;

@Service
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    @Autowired
    public RestaurantService(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    public List<Restaurant> getAllRestaurants() {
        return restaurantRepository.findAll();
    }

    public Optional<Restaurant> getRestaurantById(Long id) {
        return restaurantRepository.findById(id);
    }

    public Restaurant createRestaurant(Restaurant restaurant) {
        return restaurantRepository.save(restaurant);
    }

    public Optional<Restaurant> updateRestaurant(Long id, Restaurant updatedRestaurant) {
        return restaurantRepository.findById(id)
            .map(existingRestaurant -> {
                updateRestaurantFields(existingRestaurant, updatedRestaurant);
                return restaurantRepository.save(existingRestaurant);
            });
    }

    private void updateRestaurantFields(Restaurant existingRestaurant, Restaurant updatedRestaurant) {
        existingRestaurant.setName(updatedRestaurant.getName());
        existingRestaurant.setLocation(updatedRestaurant.getLocation());
        existingRestaurant.setCapacity(updatedRestaurant.getCapacity());
        existingRestaurant.setOperatingHours(updatedRestaurant.getOperatingHours());
    }

    public void deleteRestaurant(Long id) {
        restaurantRepository.deleteById(id);
    }
} 