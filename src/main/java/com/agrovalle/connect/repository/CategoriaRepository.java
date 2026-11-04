package com.agrovalle.connect.repository;

import com.agrovalle.connect.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}
