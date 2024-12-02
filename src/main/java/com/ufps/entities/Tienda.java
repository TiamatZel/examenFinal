package com.ufps.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "tienda")
public class Tienda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    @Column
    private String direccion;

    // Getters, Setters, Constructor
}

