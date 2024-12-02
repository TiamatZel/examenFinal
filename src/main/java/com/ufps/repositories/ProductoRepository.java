package com.ufps.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ufps.entities.Producto;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {}

