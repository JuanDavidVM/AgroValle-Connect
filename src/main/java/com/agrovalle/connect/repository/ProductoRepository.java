package com.agrovalle.connect.repository;

import com.agrovalle.connect.model.Categoria;
import com.agrovalle.connect.model.EstadoOferta;
import com.agrovalle.connect.model.Municipio;
import com.agrovalle.connect.model.Producto;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByEstadoOrderByFechaPublicacionDesc(EstadoOferta estado);

    List<Producto> findByEstadoAndFincaMunicipioOrderByFechaPublicacionDesc(
            EstadoOferta estado, Municipio municipio);

    List<Producto> findByEstadoAndCategoriaOrderByFechaPublicacionDesc(
            EstadoOferta estado, Categoria categoria);

    List<Producto> findByEstadoAndFincaMunicipioAndCategoriaOrderByFechaPublicacionDesc(
            EstadoOferta estado, Municipio municipio, Categoria categoria);
}
