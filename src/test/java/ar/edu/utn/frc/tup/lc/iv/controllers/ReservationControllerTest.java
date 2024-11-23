package ar.edu.utn.frc.tup.lc.iv.controllers;

import ar.edu.utn.frc.tup.lc.iv.entity.Reservation;
import ar.edu.utn.frc.tup.lc.iv.models.ReservationDto;
import ar.edu.utn.frc.tup.lc.iv.servicies.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReservationControllerTest {

    @Mock
    private ReservationService reservationService;

    @InjectMocks
    private ReservationController reservationController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getReservationById_successful() {
        ReservationDto reservation = new ReservationDto();
        reservation.setId("RES123");

        Reservation reservationEntity = new Reservation();
        reservationEntity.setId("RES123");

        when(reservationService.getReservationById("RES123")).thenReturn(reservationEntity);

        ResponseEntity<Reservation> response = reservationController.getReservation("RES123");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("RES123", response.getBody().getId());
        verify(reservationService, times(1)).getReservationById("RES123");
    }

    @Test
    void createReservation_successful() {
        ReservationDto reservation = new ReservationDto();
        reservation.setId("RES123");

        Reservation reservationEntity = new Reservation();
        reservationEntity.setId("RES123");

        when(reservationService.saveReservation(any(ReservationDto.class))).thenReturn(reservationEntity);

        ResponseEntity<Reservation> response = reservationController.createReservation(reservation);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("RES123", response.getBody().getId());
        verify(reservationService, times(1)).saveReservation(any(ReservationDto.class));
    }


    @Test
    void checkInReservation_badRequest() {
        ReservationDto reservation = new ReservationDto();
        reservation.setId("RES123");

        doThrow(new IllegalArgumentException("Invalid reservation")).when(reservationService).saveOrUpdateReservation(any(ReservationDto.class));

        ResponseEntity<String> response = reservationController.checkInReservation(reservation);

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Invalid reservation", response.getBody());
        verify(reservationService, times(1)).saveOrUpdateReservation(any(ReservationDto.class));
    }
}