package pt.ua.tqs.moliceiro.meals.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ua.tqs.moliceiro.meals.model.Reservation;
import pt.ua.tqs.moliceiro.meals.service.ReservationService;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@Tag(name = "Reservation", description = "Reservation management API")
public class ReservationController {

    private final ReservationService reservationService;

    @Autowired
    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/{code}")
    @Operation(summary = "Get reservation by code", description = "Returns a reservation by its code")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the reservation"),
        @ApiResponse(responseCode = "404", description = "Reservation not found")
    })
    public ResponseEntity<Reservation> getReservationByCode(
            @Parameter(description = "Code of the reservation to retrieve") @PathVariable String code) {
        return reservationService.getReservationByCode(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/meal/{mealId}")
    @Operation(summary = "Get reservations by meal", description = "Returns all reservations for a specific meal")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved reservations")
    public ResponseEntity<List<Reservation>> getReservationsByMeal(
            @Parameter(description = "ID of the meal") @PathVariable Long mealId) {
        List<Reservation> reservations = reservationService.getReservationsByMeal(mealId);
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/customer/{email}")
    @Operation(summary = "Get reservations by customer", description = "Returns all reservations for a specific customer")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved reservations")
    public ResponseEntity<List<Reservation>> getReservationsByCustomer(
            @Parameter(description = "Email of the customer") @PathVariable String email) {
        List<Reservation> reservations = reservationService.getReservationsByCustomerEmail(email);
        return ResponseEntity.ok(reservations);
    }

    @GetMapping
    @Operation(summary = "Get all reservations", description = "Returns a list of all reservations")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved all reservations")
    public ResponseEntity<List<Reservation>> getAllReservations() {
        List<Reservation> reservations = reservationService.getAllReservations();
        return ResponseEntity.ok(reservations);
    }

    @PostMapping
    @Operation(summary = "Create a new reservation", description = "Creates a new reservation")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully created the reservation"),
        @ApiResponse(responseCode = "400", description = "Invalid reservation data")
    })
    public ResponseEntity<Reservation> createReservation(
            @Parameter(description = "Reservation object to create") @RequestBody Reservation reservation) {
        try {
            Reservation createdReservation = reservationService.createReservation(reservation);
            return ResponseEntity.ok(createdReservation);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{code}/cancel")
    @Operation(summary = "Cancel a reservation", description = "Cancels a reservation by its code")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully cancelled the reservation"),
        @ApiResponse(responseCode = "404", description = "Reservation not found"),
        @ApiResponse(responseCode = "400", description = "Reservation cannot be cancelled")
    })
    public ResponseEntity<Reservation> cancelReservation(
            @Parameter(description = "Code of the reservation to cancel") @PathVariable String code) {
        return reservationService.cancelReservation(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{code}/use")
    @Operation(summary = "Mark reservation as used", description = "Marks a reservation as used")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully marked the reservation as used"),
        @ApiResponse(responseCode = "404", description = "Reservation not found")
    })
    public ResponseEntity<Reservation> markReservationAsUsed(
            @Parameter(description = "Code of the reservation to mark as used") @PathVariable String code) {
        return reservationService.markReservationAsUsed(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{code}")
    @Operation(summary = "Delete a reservation", description = "Deletes a reservation by its code")
    @ApiResponse(responseCode = "200", description = "Reservation successfully deleted")
    @ApiResponse(responseCode = "404", description = "Reservation not found")
    public ResponseEntity<Void> deleteReservation(
            @Parameter(description = "Reservation code") @PathVariable String code) {
        return reservationService.deleteReservation(code)
            .map(reservation -> ResponseEntity.ok().<Void>build())
            .orElse(ResponseEntity.notFound().build());
    }
} 