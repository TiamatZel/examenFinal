package com.ufps.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ufps.entities.Cajero;

@Repository
public interface CajeroRepository extends JpaRepository<Cajero, Long> {}

