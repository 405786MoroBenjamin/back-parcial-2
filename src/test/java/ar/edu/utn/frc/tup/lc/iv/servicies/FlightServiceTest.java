package ar.edu.utn.frc.tup.lc.iv.servicies;

import ar.edu.utn.frc.tup.lc.iv.entity.*;
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

class FlightServiceTest {

    @Mock
    private FlightRepository flightRepository;

    @InjectMocks
    private FlightService flightService;

    @Mock
    private SeatRepository seatRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getFlightById_successful() {
        Flight flight = new Flight();
        flight.setId("AR1550");

        when(flightRepository.findById("AR1550")).thenReturn(Optional.of(flight));

        Flight foundFlight = flightService.getFlightById("AR1550");

        assertNotNull(foundFlight);
        assertEquals("AR1550", foundFlight.getId());
    }

    @Test
    void getFlightById_notFound() {
        when(flightRepository.findById("AR1550")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> flightService.getFlightById("AR1550"));
    }

    @Test
    void saveFlight_successful() {
        // Preparación de datos de prueba
        Flight flight = new Flight();
        flight.setId("AR1550");

        List<Seat> seats = List.of(new Seat("1A", "available", flight), new Seat("1B", "reserved", flight));
        flight.setSeat_map(seats);

        // Configuración del comportamiento de los mocks
        when(flightRepository.save(any(Flight.class))).thenReturn(flight);
        when(seatRepository.save(any(Seat.class))).thenReturn(new Seat());

        // Llamada al método de servicio
        Flight savedFlight = flightService.saveFlight(flight);

        // Verificaciones
        assertNotNull(savedFlight);
        assertEquals("AR1550", savedFlight.getId());
        verify(flightRepository, times(1)).save(flight);  // Verifica que el save del vuelo se haya llamado
        verify(seatRepository, times(2)).save(any(Seat.class));  // Verifica que se hayan guardado los asientos
    }


}