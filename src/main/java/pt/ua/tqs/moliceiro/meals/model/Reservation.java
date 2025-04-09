package pt.ua.tqs.moliceiro.meals.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "reservations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {
    
    public enum ReservationStatus {
        PENDING,
        ACTIVE,
        COMPLETED,
        CANCELED
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "meal_id", nullable = false)
    private Meal meal;
    
    @Column(nullable = false)
    private String customerName;
    
    @Column(nullable = false)
    private String customerEmail;
    
    @Column(nullable = false)
    private Integer numberOfPeople;
    
    @Column(nullable = false)
    private LocalDateTime reservationTime;
    
    @Column(nullable = false, unique = true)
    private String reservationCode;
    
    @Column(nullable = false)
    private Boolean isUsed = false;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status = ReservationStatus.PENDING;
    
    @PrePersist
    public void generateReservationCode() {
        this.reservationCode = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
} 