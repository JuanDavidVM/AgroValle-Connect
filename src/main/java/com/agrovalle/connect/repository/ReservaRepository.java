package com.agrovalle.connect.repository;

import com.agrovalle.connect.model.EstadoReserva;
import com.agrovalle.connect.model.Reserva;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findByCompradorIdAndEstadoOrderByFechaReservaDesc(Long compradorId, EstadoReserva estado);

    List<Reserva> findByCompradorIdAndProductoIdAndEstado(Long compradorId, Long productoId, EstadoReserva estado);
}
