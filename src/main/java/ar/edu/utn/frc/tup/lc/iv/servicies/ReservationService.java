package ar.edu.utn.frc.tup.lc.iv.servicies;

import ar.edu.utn.frc.tup.lc.iv.entity.*;
import ar.edu.utn.frc.tup.lc.iv.models.PassengetDto;
import ar.edu.utn.frc.tup.lc.iv.models.ReservationDto;
import ar.edu.utn.frc.tup.lc.iv.repositories.PassengerRepository;
import ar.edu.utn.frc.tup.lc.iv.repositories.ReservationRepository;
import ar.edu.utn.frc.tup.lc.iv.repositories.FlightRepository;
import ar.edu.utn.frc.tup.lc.iv.repositories.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final FlightRepository flightRepository;
    private final ReservationRepository reservationRepository;
    private final PassengerRepository passengerRepository;
    private final SeatRepository seatRepository;

    public Reservation saveReservation(ReservationDto reservationO) {
        // Verificar si el vuelo existe
        Optional<Flight> flightOpt = flightRepository.findById(reservationO.getFlight());
        if (flightOpt.isEmpty()) {
            throw new IllegalArgumentException("Vuelo no encontrado.");
        }
        Flight flight = flightOpt.get();

        // Verificar si hay disponibilidad de asientos
        long reserved = flight.getSeat_map().stream()
                .filter(seat -> "reserved".equals(seat.getStatus()))
                .count();
        if (reserved >= flight.getSeat_map().size()) {
            throw new IllegalArgumentException("No hay asientos disponibles.");
        }

        // Crear la nueva reserva
        Reservation reservation = new Reservation();
        reservation.setId(reservationO.getId());
        reservation.setStatus(ReservationStatus.READY_TO_CHECK_IN.toString());
        reservation.setFlight(flight);

        // Crear la lista de pasajeros
        List<Passenger> passengers = reservationO.getPassengers().stream()
                .map(passengerDto -> {
                    Passenger passenger = new Passenger();
                    passenger.setName(passengerDto.getName());
                    passenger.setReservation(reservation);
                    return passenger;
                })
                .collect(Collectors.toList());

        reservationRepository.save(reservation);
        passengerRepository.saveAll(passengers);

        reservation.setPassengers(passengers);

        return reservation;
    }

    // Queria hacer los 2 metodos en 1 solo pero se me hizo alto choclo
    public Reservation saveOrUpdateReservation(ReservationDto reservationO) {
        // Verificar si el vuelo existe
        Optional<Flight> flightOpt = flightRepository.findById(reservationO.getFlight());
        if (flightOpt.isEmpty()) {
            throw new IllegalArgumentException("Vuelo no encontrado.");
        }
        Flight flight = flightOpt.get();

        // Verificar si la reserva existe
        Optional<Reservation> existingReservationOpt = Optional.empty();
        if (reservationO.getId() != null && !reservationO.getId().isEmpty()) {
            existingReservationOpt = reservationRepository.findById(reservationO.getId());
        }

        Reservation reservation;
        if (existingReservationOpt.isPresent()) {
            reservation = existingReservationOpt.get();

            if (!reservation.getStatus().equals(ReservationStatus.READY_TO_CHECK_IN.toString())) {
                if (reservation.getStatus().equals(ReservationStatus.DUE.toString())) {
                    throw new IllegalArgumentException("La reserva está vencida.");
                } else {
                    throw new IllegalArgumentException("La reserva no está lista para el check-in.");
                }
            }

            reservation.getPassengers().clear();
            for (PassengetDto passengerDto : reservationO.getPassengers()) {
                Optional<Seat> seatOpt = flight.getSeat_map().stream()
                        .filter(seat -> "available".equals(seat.getStatus()))
                        .findFirst();
                if (seatOpt.isEmpty()) {
                    throw new IllegalArgumentException("El asiento " + passengerDto.getSeat() + " no está disponible.");
                }
                Seat seat = seatOpt.get();
                seat.setStatus("reserved");
                seatRepository.save(seat);

                Passenger passenger = new Passenger();
                passenger.setName(passengerDto.getName());
                passenger.setSeat(seat);
                passenger.setReservation(reservation);
                passengerRepository.save(passenger);
            }

            reservation.setStatus(ReservationStatus.CHECKED_IN.toString());
            reservationRepository.save(reservation);

        } else {
            throw new IllegalArgumentException("Reserva no encontrada.");
        }

        return reservation;
    }




    public Reservation getReservationById(String reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found."));
    }

}