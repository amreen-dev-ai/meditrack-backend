package com.meditrack.repository;

import com.meditrack.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

// Extending JpaRepository<Patient, Long> gives you save(), findById(), findAll(),
// delete(), etc. for free -- no implementation needed, Spring generates it at runtime.
public interface PatientRepository extends JpaRepository<Patient, Long> {
}
