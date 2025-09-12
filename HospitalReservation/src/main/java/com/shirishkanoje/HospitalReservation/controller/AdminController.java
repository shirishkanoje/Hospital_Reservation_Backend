package com.shirishkanoje.HospitalReservation.controller;

import com.shirishkanoje.HospitalReservation.dto.PatientReservationDTO;
import com.shirishkanoje.HospitalReservation.model.Admin;
import com.shirishkanoje.HospitalReservation.model.Patient;
import com.shirishkanoje.HospitalReservation.model.Reservation;
import com.shirishkanoje.HospitalReservation.service.AdminService;
import com.shirishkanoje.HospitalReservation.service.PatientService;
import com.shirishkanoje.HospitalReservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final ReservationService reservationService;
    private final PatientService patientService;

    // Get today’s pending patients or by date
    @GetMapping("/patients")
    public List<PatientReservationDTO> getPatientsByDate(@RequestParam(required = false) String date) {
        LocalDate queryDate = (date == null || date.isEmpty())
                ? LocalDate.now()
                : LocalDate.parse(date);

        return adminService.getPatientsByDate(queryDate);
    }


    // Get all reservations by date (all statuses)
    @GetMapping("/reservations")
    public List<PatientReservationDTO> getReservationsByDate(@RequestParam String date) {

        return adminService.getAllPatientsWithReservations(); // returns all reservations for all patients
    }

    // Update reservation status
    @PutMapping("/reservation/{id}/status")
    public Reservation updateStatus(@PathVariable Long id, @RequestParam String status) {
        return adminService.updateReservationStatus(id, status);
    }

    // Reserve patient for free (admin assigned automatically)
    @PostMapping("/reserve-free")
    public Reservation reservePatientForFree(@RequestBody Patient patient,
                                             @RequestParam String date,
                                             @RequestParam String time) {
        Admin admin = adminService.getDefaultAdmin();

        LocalDate reservationDate = LocalDate.parse(date);
        LocalTime reservationTime = LocalTime.parse(time);

        return adminService.reservePatientForFree(patient, admin, reservationDate, reservationTime);
    }
}
