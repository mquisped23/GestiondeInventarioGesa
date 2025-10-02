package com.edu.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;



//El campo fechaRegistro debe poblarse en la capa de servicio al crear el proveedor

@Entity
@Table(name = "proveedores",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_proveedor_ruc", columnNames = "ruc"),
                @UniqueConstraint(name = "uk_proveedor_correo", columnNames = "correo")
        })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150, message = "El nombre no puede superar los 150 caracteres")
    @Column(nullable = false, length = 150)
    private String nombre;


    //RUC (Perú = 11 dígitos)

    @NotBlank(message = "El RUC es obligatorio")
    @Pattern(regexp = "\\d{11}", message = "RUC inválido. Debe contener 11 dígitos (ajusta si tu formato es otro).")
    @Column(nullable = false, unique = true, length = 11)
    private String ruc;

    @Size(max = 150, message = "La dirección no puede superar los 150 caracteres")
    @Column(length = 150)
    private String direccion;

    @Size(max = 20, message = "El teléfono no puede superar los 20 caracteres")
    @Column(length = 20)
    private String telefono;

    @Email(message = "Debe ser un correo válido")
    @Size(max = 100, message = "El correo no puede superar los 100 caracteres")
    @Column(unique = true, length = 100)
    private String correo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private EstadoProveedor estado = EstadoProveedor.ACTIVO;


    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;


}
