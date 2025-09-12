package com.shirishkanoje.HospitalReservation.service;

import com.shirishkanoje.HospitalReservation.model.Patient;
import com.shirishkanoje.HospitalReservation.model.Reservation;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ReservationService {

    Reservation bookReservation(Patient patient, LocalDate date, LocalTime time, boolean paid);

    boolean isSlotAvailable(LocalDate date, LocalTime time);

    List<Reservation> getReservationsByDate(LocalDate date);

    List<Reservation> getReservationsByPatient(Patient patient);

    // Additional methods for Razorpay payment confirmation
    Reservation getReservationById(Long id);

    Reservation save(Reservation reservation);

    void clearPastReservations(); // Optional, mark missed reservations
}
