package com.agrovalle.connect.repository;

import com.agrovalle.connect.model.Agricultor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgricultorRepository extends JpaRepository<Agricultor, Long> {

    boolean existsByDocumento(String documento);
}
