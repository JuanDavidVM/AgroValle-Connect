package com.agrovalle.connect.repository;

import com.agrovalle.connect.model.Finca;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FincaRepository extends JpaRepository<Finca, Long> {

    boolean existsByAgricultorIdAndNombreIgnoreCase(Long agricultorId, String nombre);
}
