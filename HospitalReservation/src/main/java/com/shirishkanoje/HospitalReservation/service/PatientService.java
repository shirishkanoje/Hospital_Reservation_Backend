package com.shirishkanoje.HospitalReservation.service;

import com.shirishkanoje.HospitalReservation.model.Patient;
import java.util.List;
import java.util.Optional;

public interface PatientService {

    Patient createPatient(Patient patient);  // Add new patient

    List<Patient> getAllPatients();
    Optional<Patient> getPatientById(Long id);  // List all patients
}
