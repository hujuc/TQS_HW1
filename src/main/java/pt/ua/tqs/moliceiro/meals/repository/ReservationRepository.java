package pt.ua.tqs.moliceiro.meals.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pt.ua.tqs.moliceiro.meals.model.Reservation;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Optional<Reservation> findByReservationCode(String reservationCode);
    boolean existsByMealIdAndIsUsedFalse(Long mealId);
    List<Reservation> findByMealId(Long mealId);
    List<Reservation> findByCustomerEmail(String email);
} 