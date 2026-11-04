package com.agrovalle.connect.repository;

import com.agrovalle.connect.model.Finca;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FincaRepository extends JpaRepository<Finca, Long> {

    boolean existsByAgricultorIdAndNombreIgnoreCase(Long agricultorId, String nombre);

    Optional<Finca> findByIdAndAgricultorId(Long id, Long agricultorId);
}
