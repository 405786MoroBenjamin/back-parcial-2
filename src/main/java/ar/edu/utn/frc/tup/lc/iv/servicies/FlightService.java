package ar.edu.utn.frc.tup.lc.iv.servicies;

import ar.edu.utn.frc.tup.lc.iv.entity.Flight;
import ar.edu.utn.frc.tup.lc.iv.entity.Seat;
import ar.edu.utn.frc.tup.lc.iv.repositories.AirportRepository;
import ar.edu.utn.frc.tup.lc.iv.repositories.FlightRepository;
import ar.edu.utn.frc.tup.lc.iv.repositories.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FlightService {

    private final FlightRepository flightRepository;

    private final SeatRepository seatRepository;

    private final AirportRepository airportRepository;

    public Flight saveFlight(Flight flight) {
        if (flight.getAirport() != null && flight.getAirport().getId() == null) {
            airportRepository.save(flight.getAirport());
        }

        // Guardar el vuelo
        //validateFlight(flight);
        flightRepository.save(flight);

        // Guardar los asientos del vuelo
        flight.getSeat_map().forEach(seat -> {
            seat.setFlight(flight);
            seatRepository.save(seat);
        });

        return flight;
    }


    private void validateFlight(Flight flight) {
        // validar que la fecha sea x lo menos 6 hrs mas que la fecha actual
        // Todo: REvisar la validacion
//        if (flight.getDeparture().isBefore(flight.getDeparture().plusHours(6))) {
//            throw new IllegalArgumentException("La fecha de salida debe ser al menos 6 horas mayor a la fecha actual");
//        }
    }

    public Flight getFlightById(String id) {
        List<Flight> flights = flightRepository.findAll();
        return flightRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontro el vuelo"));
    }
}