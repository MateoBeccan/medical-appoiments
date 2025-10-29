package com.clinic.backend.repository;

import com.clinic.backend.model.Turno;
import java.time.LocalDateTime;
import java.sql.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TurnoRepository extends JpaRepository<Turno, Long> {
    List<Turno> findByDoctorIdAndFechaHora(Long doctorId, LocalDateTime fechaHora);
    List<Turno> findByDoctorId(Long doctorId);
    List<Turno> findByPacienteId(Long pacienteId);
    @Query("SELECT t FROM Turno t WHERE DATE(t.fechaHora) = :fecha")
    List<Turno> findByFecha(Date fecha);
}
