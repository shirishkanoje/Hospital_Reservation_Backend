package com.shirishkanoje.HospitalReservation.service;

import com.shirishkanoje.HospitalReservation.dto.PatientReservationDTO;
import com.shirishkanoje.HospitalReservation.model.Admin;
import com.shirishkanoje.HospitalReservation.model.Patient;
import com.shirishkanoje.HospitalReservation.model.Reservation;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AdminService {

    Admin createAdmin(Admin admin);

    Admin getDefaultAdmin();

    List<Patient> getAllPatients(); // backward compatible

    List<PatientReservationDTO> getAllPatientsWithReservations(); // today’s list only

    List<PatientReservationDTO> getPatientsByDate(LocalDate date); // get pending by date

    List<Reservation> getReservationsByDate(LocalDate date);

    Reservation updateReservationStatus(Long reservationId, String status);

    Reservation reservePatientForFree(Patient patient, Admin admin, LocalDate date, LocalTime time);
}
