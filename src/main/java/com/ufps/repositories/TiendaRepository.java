package com.ufps.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ufps.entities.Tienda;

@Repository
public interface TiendaRepository extends JpaRepository<Tienda, Long> {}

