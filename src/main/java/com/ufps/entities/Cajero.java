package com.ufps.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "cajero")
public class Cajero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String tokenAutorizacion;

    // Getters, Setters, Constructor
}
