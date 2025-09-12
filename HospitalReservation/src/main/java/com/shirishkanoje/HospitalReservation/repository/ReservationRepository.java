package com.shirishkanoje.HospitalReservation.repository;

import com.shirishkanoje.HospitalReservation.model.Reservation;
import com.shirishkanoje.HospitalReservation.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // Check if a time slot is already booked on a given date
    boolean existsByDateAndTime(LocalDate date, LocalTime time);

    // Find reservations for a specific date
//    List<Reservation> findByDate(LocalDate date);

    // Find reservations for a patient
    List<Reservation> findByPatient(Patient patient);

    @Query("SELECT DISTINCT r FROM Reservation r WHERE r.date = :date")
    List<Reservation> findByDate(@Param("date") LocalDate date);
}
