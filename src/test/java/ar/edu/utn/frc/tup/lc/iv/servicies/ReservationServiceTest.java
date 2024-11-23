package ar.edu.utn.frc.tup.lc.iv.servicies;

import ar.edu.utn.frc.tup.lc.iv.entity.*;
import ar.edu.utn.frc.tup.lc.iv.models.PassengetDto;
import ar.edu.utn.frc.tup.lc.iv.models.ReservationDto;
import ar.edu.utn.frc.tup.lc.iv.repositories.PassengerRepository;
import ar.edu.utn.frc.tup.lc.iv.repositories.ReservationRepository;
import ar.edu.utn.frc.tup.lc.iv.repositories.FlightRepository;
import ar.edu.utn.frc.tup.lc.iv.repositories.SeatRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservationServiceTest {

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private PassengerRepository passengerRepository;

    @Mock
    private SeatRepository seatRepository;

    @InjectMocks
    private ReservationService reservationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveReservation_successful() {
        Flight flight = new Flight();
        flight.setId("AR1550");
        flight.setSeat_map(List.of(new Seat("1A", "available", flight), new Seat("1B", "reserved", flight)));

        ReservationDto reservationDto = new ReservationDto();
        reservationDto.setId("AF456B");
        reservationDto.setFlight("AR1550");
        reservationDto.setPassengers(List.of(new PassengetDto("Diego Maradona", "1A")));

        when(flightRepository.findById("AR1550")).thenReturn(Optional.of(flight));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(new Reservation());

        Reservation reservation = reservationService.saveReservation(reservationDto);

        assertNotNull(reservation);
        verify(reservationRepository, times(1)).save(any(Reservation.class));
        verify(passengerRepository, times(1)).saveAll(anyList());
    }

    @Test
    void saveReservation_flightNotFound() {
        ReservationDto reservationDto = new ReservationDto();
        reservationDto.setFlight("AR1550");

        when(flightRepository.findById("AR1550")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> reservationService.saveReservation(reservationDto));
    }

    @Test
    void saveReservation_noSeatsAvailable() {
        Flight flight = new Flight();
        flight.setId("AR1550");
        flight.setSeat_map(List.of(new Seat("1A", "reserved", flight), new Seat("1B", "reserved", flight)));

        ReservationDto reservationDto = new ReservationDto();
        reservationDto.setFlight("AR1550");

        when(flightRepository.findById("AR1550")).thenReturn(Optional.of(flight));

        assertThrows(IllegalArgumentException.class, () -> reservationService.saveReservation(reservationDto));
    }

    @Test
    void getReservationById_successful() {
        Reservation reservation = new Reservation();
        reservation.setId("AF456B");

        when(reservationRepository.findById("AF456B")).thenReturn(Optional.of(reservation));

        Reservation foundReservation = reservationService.getReservationById("AF456B");

        assertNotNull(foundReservation);
        assertEquals("AF456B", foundReservation.getId());
    }

    @Test
    void getReservationById_notFound() {
        when(reservationRepository.findById("AF456B")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> reservationService.getReservationById("AF456B"));
    }


    @Test
    void saveOrUpdateReservation_existingReservationNotReadyToCheckIn() {
        Flight flight = new Flight();
        flight.setId("AR1550");
        flight.setSeat_map(List.of(new Seat("1A", "available", flight), new Seat("1B", "available", flight)));

        Reservation reservation = new Reservation();
        reservation.setId("AF456B");
        reservation.setStatus(ReservationStatus.DUE.toString());
        reservation.setFlight(flight);

        ReservationDto reservationDto = new ReservationDto();
        reservationDto.setId("AF456B");
        reservationDto.setFlight("AR1550");
        reservationDto.setPassengers(List.of(new PassengetDto("Diego Maradona", "1A")));

        when(flightRepository.findById("AR1550")).thenReturn(Optional.of(flight));
        when(reservationRepository.findById("AF456B")).thenReturn(Optional.of(reservation));

        assertThrows(IllegalArgumentException.class, () -> reservationService.saveOrUpdateReservation(reservationDto));
    }

    @Test
    void saveOrUpdateReservation_seatNotAvailable() {
        Flight flight = new Flight();
        flight.setId("AR1550");
        flight.setSeat_map(List.of(new Seat("1A", "reserved", flight), new Seat("1B", "reserved", flight)));

        Reservation reservation = new Reservation();
        reservation.setId("AF456B");
        reservation.setStatus(ReservationStatus.READY_TO_CHECK_IN.toString());
        reservation.setFlight(flight);

        ReservationDto reservationDto = new ReservationDto();
        reservationDto.setId("AF456B");
        reservationDto.setFlight("AR1550");
        reservationDto.setPassengers(List.of(new PassengetDto("Diego Maradona", "1A")));

        when(flightRepository.findById("AR1550")).thenReturn(Optional.of(flight));
        when(reservationRepository.findById("AF456B")).thenReturn(Optional.of(reservation));

        assertThrows(IllegalArgumentException.class, () -> reservationService.saveOrUpdateReservation(reservationDto));
    }

    @Test
    void saveReservation_passengerSeatNotAvailable() {
        Flight flight = new Flight();
        flight.setId("AR1550");
        flight.setSeat_map(List.of(new Seat("1A", "reserved", flight), new Seat("1B", "reserved", flight)));

        ReservationDto reservationDto = new ReservationDto();
        reservationDto.setId("AF456B");
        reservationDto.setFlight("AR1550");
        reservationDto.setPassengers(List.of(new PassengetDto("Diego Maradona", "1A")));

        when(flightRepository.findById("AR1550")).thenReturn(Optional.of(flight));

        assertThrows(IllegalArgumentException.class, () -> reservationService.saveReservation(reservationDto));
    }
}