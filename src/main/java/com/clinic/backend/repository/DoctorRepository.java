package com.clinic.backend.repository;

import com.clinic.backend.model.Doctor;
import com.clinic.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByUser(User user);
}
