package ar.edu.utn.frc.tup.lc.iv.controllers;

import ar.edu.utn.frc.tup.lc.iv.entity.Flight;
import ar.edu.utn.frc.tup.lc.iv.servicies.FlightService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FlightControllerTest {

    @Mock
    private FlightService flightService;

    @InjectMocks
    private FlightController flightController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getFlightById_successful() {
        Flight flight = new Flight();
        flight.setId("AR1550");

        when(flightService.getFlightById("AR1550")).thenReturn(flight);

        ResponseEntity<Flight> response = flightController.getFlightById("AR1550");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("AR1550", response.getBody().getId());
        verify(flightService, times(1)).getFlightById("AR1550");
    }

    @Test
    void createFlight_successful() {
        Flight flight = new Flight();
        flight.setId("AR1550");

        when(flightService.saveFlight(any(Flight.class))).thenReturn(flight);

        ResponseEntity<Flight> response = flightController.saveFlight(flight);

        assertNotNull(response);
        assertEquals(201, response.getStatusCodeValue());
        assertEquals("AR1550", response.getBody().getId());
        verify(flightService, times(1)).saveFlight(any(Flight.class));
    }
}