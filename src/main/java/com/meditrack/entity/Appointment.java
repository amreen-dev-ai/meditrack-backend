package com.meditrack.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- Foreign key to Patient ---
    // @ManyToOne: many Appointments can belong to ONE Patient.
    // @JoinColumn creates the actual "patient_id" foreign key column in the appointments table.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    // --- Foreign key to Doctor ---
    // Same idea: many Appointments can belong to ONE Doctor.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Column(nullable = false)
    private LocalDateTime appointmentDateTime;

    @Enumerated(EnumType.STRING)   // stores the enum as text ("SCHEDULED") not a number, in the DB
    @Column(nullable = false)
    private AppointmentStatus status = AppointmentStatus.SCHEDULED;

    private String reason;

    public enum AppointmentStatus {
        SCHEDULED, COMPLETED, CANCELLED
    }
}
