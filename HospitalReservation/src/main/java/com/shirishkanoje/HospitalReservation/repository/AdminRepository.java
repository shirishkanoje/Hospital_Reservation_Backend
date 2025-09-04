package com.shirishkanoje.HospitalReservation.repository;

import com.shirishkanoje.HospitalReservation.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByUsername(String username);

}
