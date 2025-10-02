package com.edu.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "clientes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nombre;

    @Size(max = 150, message = "La dirección no puede superar los 150 caracteres")
    @Column(length = 150)
    private String direccion;

    @Size(max = 15, message = "El teléfono no puede superar los 15 caracteres")
    @Column(length = 15)
    private String telefono;

    @Email(message = "Debe ser un correo válido")
    @Size(max = 100, message = "El correo no puede superar los 100 caracteres")
    @Column(unique = true, length = 100)
    private String correo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoCliente estado;

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;


}