package com.ufps.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ufps.entities.Cliente;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {}

