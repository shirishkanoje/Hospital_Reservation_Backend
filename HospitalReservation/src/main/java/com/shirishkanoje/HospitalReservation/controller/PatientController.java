package com.shirishkanoje.HospitalReservation.controller;

import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.shirishkanoje.HospitalReservation.dto.PatientReservationDTO;
import com.shirishkanoje.HospitalReservation.model.Patient;
import com.shirishkanoje.HospitalReservation.model.Reservation;
import com.shirishkanoje.HospitalReservation.service.PatientService;
import com.shirishkanoje.HospitalReservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/patient")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;
    private final ReservationService reservationService;
    private final RazorpayClient razorpayClient;

    // 🔹 New merged endpoint: Add patient + Book + Razorpay
    @PostMapping("/book-with-payment")
    public Map<String, Object> addAndBookPatient(@RequestBody Map<String, String> payload) throws RazorpayException {
        String name = payload.get("name");
        String contact = payload.get("contact");
        String dateStr = payload.get("date");
        String timeStr = payload.get("time");

        // 1️⃣ Create patient
        Patient patient = new Patient();
        patient.setName(name);
        patient.setContactNumber(contact);
        patient = patientService.createPatient(patient);

        // 2️⃣ Parse date & time
        LocalDate localDate = LocalDate.parse(dateStr);
        LocalTime localTime = LocalTime.parse(timeStr);

        // 3️⃣ Book reservation
        Reservation reservation = reservationService.bookReservation(patient, localDate, localTime, false);

        // 4️⃣ Create Razorpay Payment Link
        int amountInPaise = 10 * 100;
        JSONObject paymentLinkRequest = new JSONObject();
        paymentLinkRequest.put("amount", amountInPaise);
        paymentLinkRequest.put("currency", "INR");
        paymentLinkRequest.put("reference_id", reservation.getId().toString());
        paymentLinkRequest.put("description", "Hospital Reservation Payment");

        JSONObject customer = new JSONObject();
        customer.put("name", patient.getName());
        customer.put("contact", patient.getContactNumber() != null ? patient.getContactNumber() : "");
        paymentLinkRequest.put("customer", customer);

        JSONObject notify = new JSONObject();
        notify.put("email", true);
        notify.put("sms", true);
        paymentLinkRequest.put("notify", notify);

        paymentLinkRequest.put("callback_url",
                "https://hospital-reservation-backend-1.onrender.com/api/patient/payment-success?reservationId=" + reservation.getId());
        paymentLinkRequest.put("callback_method", "get");

        PaymentLink paymentLink = razorpayClient.paymentLink.create(paymentLinkRequest);

        // 5️⃣ Return Razorpay link to frontend
        Map<String, Object> response = new HashMap<>();
        response.put("reservationId", reservation.getId());
        response.put("paymentLink", paymentLink.get("short_url"));
        response.put("patientId", patient.getId());

        return response;
    }

    // Razorpay callback
    @GetMapping("/payment-success")
    public ResponseEntity<String> confirmPayment(@RequestParam Long reservationId) {
        Reservation reservation = reservationService.getReservationById(reservationId);
        reservation.setPaid(true);
        reservationService.save(reservation);

        String html = """
        <html>
          <head>
            <meta http-equiv="refresh" content="3;url=http://localhost:5173" />
            <style>
              body { font-family: sans-serif; text-align: center; padding-top: 50px; }
              .message { font-size: 1.5rem; color: green; }
            </style>
          </head>
          <body>
            <div class="message"> Payment successful, reservation confirmed!</div>
            <p>Redirecting to dashboard...</p>
          </body>
        </html>
        """;

        return ResponseEntity.ok().header("Content-Type", "text/html").body(html);
    }


    // Available slots
    @GetMapping("/available-slots")
    public List<LocalTime> getAvailableSlots(@RequestParam String date) {
        LocalDate localDate = LocalDate.parse(date);
        List<LocalTime> slots = List.of(
                LocalTime.of(9,0), LocalTime.of(9,15), LocalTime.of(9,30), LocalTime.of(9,45),
                LocalTime.of(10,0), LocalTime.of(10,15), LocalTime.of(10,30), LocalTime.of(10,45),
                LocalTime.of(11,0), LocalTime.of(11,15), LocalTime.of(11,30), LocalTime.of(11,45)
        );
        return slots.stream()
                .filter(t -> reservationService.isSlotAvailable(localDate, t))
                .toList();
    }

    // Patient history
    @GetMapping("/history")
    public List<PatientReservationDTO> getPatientHistory(@RequestParam Long patientId) {
        Patient patient = patientService.getPatientById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        return reservationService.getReservationsByPatient(patient).stream()
                .map(res -> new PatientReservationDTO(
                        patient.getId(),
                        patient.getName(),
                        patient.getAge(),
                        patient.getContactNumber(),
                        res.getDate(),
                        res.getTime(),
                        res.getPaid(),
                        res.getStatus()
                ))
                .toList();
    }


}

