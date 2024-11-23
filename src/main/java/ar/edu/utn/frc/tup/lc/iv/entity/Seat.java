package ar.edu.utn.frc.tup.lc.iv.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity
@Data
@RequiredArgsConstructor
public class Seat {
    @Id
    private String seat;

    @Column
    private String status;  // Puede ser 'available', 'reserved', etc.

    @ManyToOne
    @JoinColumn(name = "flight_id")
    @JsonBackReference
    private Flight flight;
}
