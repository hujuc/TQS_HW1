package pt.ua.tqs.moliceiro.meals.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pt.ua.tqs.moliceiro.meals.model.Meal;
import pt.ua.tqs.moliceiro.meals.model.Reservation;
import pt.ua.tqs.moliceiro.meals.model.Restaurant;
import pt.ua.tqs.moliceiro.meals.repository.MealRepository;
import pt.ua.tqs.moliceiro.meals.repository.ReservationRepository;
import pt.ua.tqs.moliceiro.meals.repository.RestaurantRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final MealRepository mealRepository;
    private final RestaurantRepository restaurantRepository;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    @Autowired
    public ReservationService(ReservationRepository reservationRepository, MealRepository mealRepository, RestaurantRepository restaurantRepository) {
        this.reservationRepository = reservationRepository;
        this.mealRepository = mealRepository;
        this.restaurantRepository = restaurantRepository;
    }

    public Optional<Reservation> getReservationByCode(String code) {
        return reservationRepository.findByReservationCode(code);
    }

    public List<Reservation> getReservationsByMeal(Long mealId) {
        return reservationRepository.findByMealId(mealId);
    }

    public List<Reservation> getReservationsByCustomerEmail(String email) {
        return reservationRepository.findByCustomerEmail(email);
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public boolean hasActiveReservations(Long mealId) {
        return reservationRepository.existsByMealIdAndIsUsedFalse(mealId);
    }

    public Reservation createReservation(Reservation reservation) {
        // Validate reservation data
        validateReservation(reservation);

        // Verify if the meal exists and has available seats
        Meal meal = mealRepository.findById(reservation.getMeal().getId())
            .orElseThrow(() -> new IllegalArgumentException("Meal not found"));

        // Check if the restaurant has enough capacity
        Restaurant restaurant = meal.getRestaurant();
        if (restaurant.getCapacity() < reservation.getNumberOfPeople()) {
            throw new IllegalArgumentException("Not enough capacity in the restaurant");
        }

        // Set the reservation time and status
        reservation.setReservationTime(LocalDateTime.now());
        reservation.setStatus(Reservation.ReservationStatus.ACTIVE);
        
        // Update restaurant capacity
        restaurant.setCapacity(restaurant.getCapacity() - reservation.getNumberOfPeople());
        restaurantRepository.save(restaurant);
        
        // Save the reservation
        return reservationRepository.save(reservation);
    }

    public Optional<Reservation> cancelReservation(String code) {
        return reservationRepository.findByReservationCode(code)
            .map(reservation -> {
                cancelReservationInternal(reservation);
                return reservationRepository.save(reservation);
            });
    }

    private void cancelReservationInternal(Reservation reservation) {
        if (reservation.getIsUsed()) {
            throw new IllegalStateException("Reservation has already been used");
        }
        
        // If the reservation is already canceled, just return it without changes
        if (reservation.getStatus() == Reservation.ReservationStatus.CANCELED) {
            return;
        }
        
        // Restore restaurant capacity
        Restaurant restaurant = reservation.getMeal().getRestaurant();
        restaurant.setCapacity(restaurant.getCapacity() + reservation.getNumberOfPeople());
        restaurantRepository.save(restaurant);
        
        // Update status to cancelled
        reservation.setStatus(Reservation.ReservationStatus.CANCELED);
    }

    public Optional<Reservation> markReservationAsUsed(String code) {
        return reservationRepository.findByReservationCode(code)
            .map(reservation -> {
                markReservationAsUsedInternal(reservation);
                return reservationRepository.save(reservation);
            });
    }

    private void markReservationAsUsedInternal(Reservation reservation) {
        if (reservation.getIsUsed()) {
            throw new IllegalStateException("Reservation has already been used");
        }
        reservation.setIsUsed(true);
        reservation.setStatus(Reservation.ReservationStatus.COMPLETED);
        // Restore restaurant capacity when marking a reservation as used
        Restaurant restaurant = reservation.getMeal().getRestaurant();
        restaurant.setCapacity(restaurant.getCapacity() + reservation.getNumberOfPeople());
        restaurantRepository.save(restaurant);
    }

    public Optional<Reservation> deleteReservation(String code) {
        return reservationRepository.findByReservationCode(code)
            .map(reservation -> {
                // Only restore capacity if the reservation is not used AND not canceled
                if (!reservation.getIsUsed() && reservation.getStatus() != Reservation.ReservationStatus.CANCELED) {
                    Restaurant restaurant = reservation.getMeal().getRestaurant();
                    restaurant.setCapacity(restaurant.getCapacity() + reservation.getNumberOfPeople());
                    restaurantRepository.save(restaurant);
                }
                // Only delete if the reservation is not canceled
                if (reservation.getStatus() != Reservation.ReservationStatus.CANCELED) {
                    reservationRepository.delete(reservation);
                }
                return reservation;
            });
    }

    private void validateReservation(Reservation reservation) {
        if (reservation.getCustomerName() == null || reservation.getCustomerName().trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name is required");
        }

        if (reservation.getCustomerEmail() == null || !EMAIL_PATTERN.matcher(reservation.getCustomerEmail()).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }

        if (reservation.getNumberOfPeople() == null || reservation.getNumberOfPeople() <= 0) {
            throw new IllegalArgumentException("Number of people must be greater than 0");
        }

        if (reservation.getMeal() == null || reservation.getMeal().getId() == null) {
            throw new IllegalArgumentException("Meal is required");
        }
    }
} 