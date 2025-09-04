package com.shirishkanoje.HospitalReservation.serviceimpl;

import com.shirishkanoje.HospitalReservation.model.Patient;
import com.shirishkanoje.HospitalReservation.model.Reservation;
import com.shirishkanoje.HospitalReservation.repository.ReservationRepository;
import com.shirishkanoje.HospitalReservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;

    @Override
    public Reservation bookReservation(Patient patient, LocalDate date, LocalTime time, boolean paid) {
        if (!isSlotAvailable(date, time)) {
            throw new RuntimeException("Time slot not available");
        }
        Reservation reservation = new Reservation();
        reservation.setPatient(patient);
        reservation.setDate(date);
        reservation.setTime(time);
        reservation.setStatus("pending");
        reservation.setPaid(paid);
        return reservationRepository.save(reservation);
    }

    @Override
    public boolean isSlotAvailable(LocalDate date, LocalTime time) {
        LocalTime startTime = time.minusMinutes(15);
        LocalTime endTime = time.plusMinutes(15);
        List<Reservation> reservations = reservationRepository.findByDate(date);

        return reservations.stream().noneMatch(r ->
                !r.getStatus().equals("completed") &&
                        (r.getTime().isAfter(startTime.minusSeconds(1)) && r.getTime().isBefore(endTime.plusSeconds(1)))
        );
    }

    @Override
    public List<Reservation> getReservationsByDate(LocalDate date) {
        return reservationRepository.findByDate(date);
    }

    @Override
    public List<Reservation> getReservationsByPatient(Patient patient) {
        return reservationRepository.findByPatient(patient);
    }

    @Override
    public Reservation getReservationById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
    }

    @Override
    public Reservation save(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    @Override
    public void clearPastReservations() {
        LocalDate today = LocalDate.now();
        List<Reservation> reservations = reservationRepository.findByDate(today.minusDays(1));
        reservations.forEach(r -> {
            if (!r.getStatus().equals("completed")) {
                r.setStatus("not_arrived");
                reservationRepository.save(r);
            }
        });
    }
}
