package com.agrovalle.connect.repository;

import com.agrovalle.connect.model.CompradorComercial;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompradorComercialRepository extends JpaRepository<CompradorComercial, Long> {

    boolean existsByNit(String nit);
}
