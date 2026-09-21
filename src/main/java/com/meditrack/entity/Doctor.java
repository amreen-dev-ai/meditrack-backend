package com.meditrack.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "doctors")
@Data                  // Lombok: auto-generates getters, setters, toString, equals/hashCode
@NoArgsConstructor      // Lombok: generates a no-arg constructor (JPA requires this)
@AllArgsConstructor     // Lombok: generates a constructor with all fields
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)   // MySQL auto-increment
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String specialization;

    @Column(nullable = false, unique = true)
    private String email;
}
