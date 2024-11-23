package ar.edu.utn.frc.tup.lc.iv.models;

import ar.edu.utn.frc.tup.lc.iv.entity.Passenger;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
public class ReservationDto {
    public String id;
    public String flight;
    public String status;
    public List<PassengetDto> passengers;
}
