package com.meditrack.controller;

import com.meditrack.dto.AppointmentRequest;
import com.meditrack.entity.Appointment;
import com.meditrack.entity.Doctor;
import com.meditrack.entity.Patient;
import com.meditrack.repository.AppointmentRepository;
import com.meditrack.repository.DoctorRepository;
import com.meditrack.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @GetMapping
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    @GetMapping("/patient/{patientId}")
    public List<Appointment> getAppointmentsByPatient(@PathVariable Long patientId) {
        return appointmentRepository.findByPatientId(patientId);
    }

    // We take an AppointmentRequest DTO (just IDs) instead of a raw Appointment entity here,
    // because the client only knows "patientId" and "doctorId" as numbers --
    // it's OUR job on the backend to look up the actual Patient/Doctor objects
    // and wire up the real relationship before saving.
    @PostMapping
    public ResponseEntity<?> createAppointment(@RequestBody AppointmentRequest request) {
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElse(null);
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElse(null);

        if (patient == null) {
            return ResponseEntity.badRequest().body("Patient not found with id: " + request.getPatientId());
        }
        if (doctor == null) {
            return ResponseEntity.badRequest().body("Doctor not found with id: " + request.getDoctorId());
        }

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAppointmentDateTime(request.getAppointmentDateTime());
        appointment.setReason(request.getReason());
        // status defaults to SCHEDULED (set in the entity itself)

        Appointment saved = appointmentRepository.save(appointment);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Appointment> updateStatus(@PathVariable Long id,
                                                      @RequestParam Appointment.AppointmentStatus status) {
        return appointmentRepository.findById(id)
                .map(appt -> {
                    appt.setStatus(status);
                    return ResponseEntity.ok(appointmentRepository.save(appt));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable Long id) {
        if (!appointmentRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        appointmentRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
