package com.ufps.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ufps.entities.TipoPago;

@Repository
public interface TipoPagoRepository extends JpaRepository<TipoPago, Long> {
    Optional<TipoPago> findByNombre(String nombre); // Consulta específica para buscar por nombre
}

