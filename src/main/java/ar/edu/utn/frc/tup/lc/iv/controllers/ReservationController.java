package ar.edu.utn.frc.tup.lc.iv.controllers;

import ar.edu.utn.frc.tup.lc.iv.entity.Passenger;
import ar.edu.utn.frc.tup.lc.iv.entity.Reservation;
import ar.edu.utn.frc.tup.lc.iv.models.ReservationDto;
import ar.edu.utn.frc.tup.lc.iv.servicies.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    // Endpoint para crear una nueva reserva
    @PostMapping
    public ResponseEntity<Reservation> createReservation(@RequestBody ReservationDto reservation) {
        try{
            return ResponseEntity.ok(reservationService.saveReservation(reservation));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    // Endpoint para obtener una reserva por ID
    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getReservation(@PathVariable String id) {
        try {
            Reservation reservation = reservationService.getReservationById(id);
            return ResponseEntity.ok(reservation);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @PutMapping
    public ResponseEntity<String> checkInReservation(@RequestBody ReservationDto reservation) {
        try {
            reservationService.saveOrUpdateReservation(reservation);
            return ResponseEntity.ok("Check-in realizado exitosamente");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}
