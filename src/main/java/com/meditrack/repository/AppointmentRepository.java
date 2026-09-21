package com.meditrack.repository;

import com.meditrack.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // Spring Data JPA reads this method NAME and auto-generates the SQL query --
    // this is called a "derived query method". No @Query annotation needed here.
    // "findByPatientId" -> generates: SELECT * FROM appointments WHERE patient_id = ?
    List<Appointment> findByPatientId(Long patientId);

    List<Appointment> findByDoctorId(Long doctorId);
}
