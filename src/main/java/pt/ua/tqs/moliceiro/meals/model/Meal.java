package pt.ua.tqs.moliceiro.meals.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "meals")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Meal {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String description;
    
    @Column(nullable = false)
    private Double price;
    
    @Column(nullable = false)
    private LocalDate date;
    
    @Column(nullable = false)
    private String mealType; // breakfast, lunch, dinner
} 