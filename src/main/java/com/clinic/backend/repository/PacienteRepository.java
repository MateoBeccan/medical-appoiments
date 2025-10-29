package com.clinic.backend.repository;

import com.clinic.backend.model.Paciente;
import com.clinic.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    Optional<Paciente> findByUser(User user);
}
