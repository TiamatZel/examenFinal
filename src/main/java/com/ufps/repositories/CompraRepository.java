package com.ufps.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ufps.entities.Compra;

@Repository
public interface CompraRepository extends JpaRepository<Compra, Long> {}

