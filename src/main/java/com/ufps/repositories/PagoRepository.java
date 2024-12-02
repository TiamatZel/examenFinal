package com.ufps.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ufps.entities.Pago;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {}

