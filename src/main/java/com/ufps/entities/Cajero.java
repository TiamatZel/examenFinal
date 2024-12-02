package com.ufps.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cajero {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String documento;
    private String email;
    private String token;

    @ManyToOne
    @JoinColumn(name = "tienda_id")
    private Tienda tienda;
}

