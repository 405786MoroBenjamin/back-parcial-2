package ar.edu.utn.frc.tup.lc.iv.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@RequiredArgsConstructor
public class Flight {
    @Id
    private String id;

    @Column
    private String aircraft;

    @Column
    private LocalDateTime departure;

    @ManyToOne
    @JoinColumn(name = "airport_id")
    private Airport airport;

    @OneToMany(mappedBy = "flight", cascade = CascadeType.ALL)
    @JsonManagedReference  // Se maneja la serialización de los asientos desde el lado de Flight
    private List<Seat> seat_map;

    @OneToMany(mappedBy = "flight", cascade = CascadeType.ALL)
    @JsonBackReference  // Evita la serialización infinita desde Flight
    private List<Reservation> reservations;
}
