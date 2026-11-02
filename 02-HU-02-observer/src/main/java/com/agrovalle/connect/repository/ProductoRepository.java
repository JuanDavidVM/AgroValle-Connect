package com.agrovalle.connect.repository;

import com.agrovalle.connect.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
}
