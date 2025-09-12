package com.shirishkanoje.HospitalReservation.serviceimpl;

import com.shirishkanoje.HospitalReservation.dto.PatientReservationDTO;
import com.shirishkanoje.HospitalReservation.model.Admin;
import com.shirishkanoje.HospitalReservation.model.Patient;
import com.shirishkanoje.HospitalReservation.model.Reservation;
import com.shirishkanoje.HospitalReservation.repository.AdminRepository;
import com.shirishkanoje.HospitalReservation.repository.PatientRepository;
import com.shirishkanoje.HospitalReservation.repository.ReservationRepository;
import com.shirishkanoje.HospitalReservation.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final PatientRepository patientRepository;
    private final ReservationRepository reservationRepository;

    @Override
    public Admin createAdmin(Admin admin) {
        return adminRepository.save(admin);
    }

    @Override
    public Admin getDefaultAdmin() {
        return adminRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No admin found"));
    }

    @Override
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    @Override
    public List<PatientReservationDTO> getAllPatientsWithReservations() {
        List<Patient> patients = patientRepository.findAll();
        List<PatientReservationDTO> result = new ArrayList<>();

        for (Patient patient : patients) {
            // Check if patient has reservations
            if (patient.getReservations() != null && !patient.getReservations().isEmpty()) {
                for (Reservation res : patient.getReservations()) {
                    result.add(new PatientReservationDTO(
                            patient.getId(),
                            patient.getName(),
                            patient.getAge(),
                            patient.getContactNumber(),
                            res.getDate(),
                            res.getTime(),
                            res.getPaid(),
                            res.getStatus()
                    ));
                }
            } else {
                // Patient without any reservation
                result.add(new PatientReservationDTO(
                        patient.getId(),
                        patient.getName(),
                        patient.getAge(),
                        patient.getContactNumber(),
                        null,
                        null,
                        null,
                        null
                ));
            }
        }

        return result;
    }


    @Override
    public List<PatientReservationDTO> getPatientsByDate(LocalDate date) {
        List<Reservation> reservations = reservationRepository.findByDate(date);
        List<PatientReservationDTO> result = new ArrayList<>();

        for (Reservation res : reservations) {
            // Only include pending reservations
            if (!res.getStatus().equalsIgnoreCase("pending")) continue;

            Patient patient = res.getPatient();
            result.add(new PatientReservationDTO(
                    patient.getId(),
                    patient.getName(),
                    patient.getAge(),
                    patient.getContactNumber(),
                    res.getDate(),
                    res.getTime(),
                    res.getPaid(),
                    res.getStatus()
            ));
        }

        return result;
    }

    @Override
    public List<Reservation> getReservationsByDate(LocalDate date) {
        return reservationRepository.findByDate(date); // DISTINCT query avoids duplicates

    }

    @Override
    public Reservation updateReservationStatus(Long reservationId, String status) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
        reservation.setStatus(status);
        return reservationRepository.save(reservation); // completed/not_arrived won't show in today list
    }

    @Override
    public Reservation reservePatientForFree(Patient patient, Admin admin, LocalDate date, LocalTime time) {
        Patient savedPatient;

        if (patient.getId() != null) {
            savedPatient = patientRepository.findById(patient.getId())
                    .orElseThrow(() -> new RuntimeException("Patient not found"));
        } else {
            savedPatient = patientRepository.save(patient);
        }

        Reservation reservation = new Reservation();
        reservation.setPatient(savedPatient);
        reservation.setAdmin(admin);
        reservation.setDate(date);
        reservation.setTime(time);
        reservation.setStatus("pending");
        reservation.setPaid(false);

        return reservationRepository.save(reservation);
    }
}
