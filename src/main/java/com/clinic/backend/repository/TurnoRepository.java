package com.clinic.backend.repository;

import com.clinic.backend.model.Turno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface TurnoRepository extends JpaRepository<Turno, Long> {

    List<Turno> findByDoctorIdAndFechaHora(Long doctorId, LocalDateTime fechaHora);

    List<Turno> findByDoctorId(Long doctorId);

    List<Turno> findByPacienteId(Long pacienteId);

    // Buscar por día (fecha)
    @Query("SELECT t FROM Turno t WHERE DATE(t.fechaHora) = :fecha")
    List<Turno> findByFecha(java.sql.Date fecha);

    // Buscar por doctor y día
    @Query("SELECT t FROM Turno t WHERE t.doctor.id = :doctorId AND DATE(t.fechaHora) = :fecha")
    List<Turno> findByDoctorIdAndFecha(Long doctorId, java.sql.Date fecha);

    // Buscar por paciente y día
    @Query("SELECT t FROM Turno t WHERE t.paciente.id = :pacienteId AND DATE(t.fechaHora) = :fecha")
    List<Turno> findByPacienteIdAndFecha(Long pacienteId, java.sql.Date fecha);
}
