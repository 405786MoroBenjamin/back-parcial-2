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

    public Reservation saveOrUpdateReservation(ReservationDto reservationO) {
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

        // Buscar si la reserva ya existe (si el ID no es null o vacío)
        Optional<Reservation> existingReservationOpt = Optional.empty();
        if (reservationO.getId() != null && !reservationO.getId().isEmpty()) {
            existingReservationOpt = reservationRepository.findById(reservationO.getId());
        }

        Reservation reservation;
        if (existingReservationOpt.isPresent()) {
            // Actualizar la reserva existente
            reservation = existingReservationOpt.get();
            reservation.setStatus(ReservationStatus.CHECKED_IN.toString());
        } else {
            // Crear una nueva reserva
            reservation = new Reservation();
            reservation.setId(reservationO.getId());
            reservation.setStatus(ReservationStatus.READY_TO_CHECK_IN.toString());
            reservation.setFlight(flight);
        }

        // Crear o actualizar la lista de pasajeros
        List<Passenger> updatedPassengers = reservationO.getPassengers().stream()
                .map(passengerDto -> {
                    Passenger passenger = new Passenger();
                    passenger.setName(passengerDto.getName());
                    // Asociar el pasajero con la reserva
                    passenger.setReservation(reservation);

                    return passenger;
                })
                .collect(Collectors.toList());

        // Guardar o actualizar los pasajeros
        passengerRepository.saveAll(updatedPassengers);

        // Asignar los pasajeros actualizados a la reserva
        reservation.setPassengers(updatedPassengers);

        // Guardar la reserva
        reservationRepository.save(reservation);

        return reservation;
    }


    public Reservation getReservationById(String reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found."));
    }

    public void checkInReservation(ReservationDto reservationDto) {
        // Obtener la reserva por el ID
        Reservation existingReservation = reservationRepository.findById(reservationDto.getId()).orElse(null);
        if (existingReservation == null) {
            throw new IllegalArgumentException("Reserva no encontrada");
        }

        if (!existingReservation.getStatus().equals("READY_TO_CHECK_IN")) {
            throw new IllegalArgumentException("La reserva no está lista para el check-in");
        }

        // Verificar que la reserva no haya vencido
        if (existingReservation.getFlight().getDeparture().isBefore(LocalDateTime.now())) {
            existingReservation.setStatus("DUE");
            reservationRepository.save(existingReservation);  // Actualizamos el estado a DUE
            throw new IllegalArgumentException("La reserva ha vencido");
        }

        List<Seat> seatsToCheck = getSeatsByPassengerSeats(reservationDto.getPassengers());

        for (Seat seat : seatsToCheck) {
            if (!seat.getStatus().equals("available")) {
                throw new IllegalArgumentException("El asiento " + seat.getSeat() + " no está disponible");
            }
        }

        // Asignar los asientos a los pasajeros y cambiar su estado a 'reserved'
        for (int i = 0; i < reservationDto.getPassengers().size(); i++) {
            PassengetDto passengerDto = reservationDto.getPassengers().get(i);
            Seat seat = seatsToCheck.get(i);
            seat.setStatus("reserved");

            Passenger passenger = new Passenger();
            passenger.setName(passengerDto.getName());
            passenger.setSeat(seat);
            passenger.setReservation(existingReservation);

            seatRepository.save(seat);
        }

        existingReservation.setStatus("CHECKED-IN");
        reservationRepository.save(existingReservation);
    }

    private List<Seat> getSeatsByPassengerSeats(List<PassengetDto> passengers) {
        return seatRepository.findAllById(passengers.stream()
                .map(PassengetDto::getSeat)
                .collect(Collectors.toList()));
    }
}