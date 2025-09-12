package com.shirishkanoje.HospitalReservation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
public class PatientReservationDTO {
    private Long patientId;
    private String name;
    private Integer age;
    private String contactNumber;

    private LocalDate reservationDate;
    private LocalTime reservationTime;
    private Boolean paid;
    private String status;
}


