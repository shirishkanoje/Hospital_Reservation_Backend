package com.shirishkanoje.HospitalReservation.repository;

import com.shirishkanoje.HospitalReservation.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    // Additional custom queries can be added here if needed
}
