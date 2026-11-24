package com.agrovalle.connect.repository;

import com.agrovalle.connect.model.Orden;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdenRepository extends JpaRepository<Orden, Long> {

    Optional<Orden> findByNumeroOrden(String numeroOrden);
}
