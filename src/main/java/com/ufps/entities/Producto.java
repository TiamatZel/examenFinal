package com.ufps.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "producto")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tipo_producto_id", nullable = false)
    private TipoProducto tipoProducto;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private Integer cantidadExistente;

    @Column(nullable = false)
    private Double precio;

    // Getters, Setters, Constructor
}

