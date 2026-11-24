package com.agrovalle.connect.repository;

import com.agrovalle.connect.model.ItemCarrito;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemCarritoRepository extends JpaRepository<ItemCarrito, Long> {

    List<ItemCarrito> findByCompradorIdOrderByIdAsc(Long compradorId);

    Optional<ItemCarrito> findByCompradorIdAndProductoId(Long compradorId, Long productoId);
}
