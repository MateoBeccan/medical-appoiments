package com.clinic.backend.service;

import com.clinic.backend.model.*;
import com.clinic.backend.repository.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TurnoService {

    private final TurnoRepository turnoRepo;
    private final DoctorRepository doctorRepo;
    private final PacienteRepository pacienteRepo;

    public TurnoService(TurnoRepository turnoRepo, DoctorRepository doctorRepo, PacienteRepository pacienteRepo) {
        this.turnoRepo = turnoRepo;
        this.doctorRepo = doctorRepo;
        this.pacienteRepo = pacienteRepo;
    }

    public List<Turno> listarTurnos() {
        User user = getUsuarioActual();

        switch (user.getRole()) {
            case ADMIN:
                return turnoRepo.findAll();
            case DOCTOR:
                return turnoRepo.findByDoctorId(user.getId());
            case PACIENTE:
                return turnoRepo.findByPacienteId(user.getId());
            default:
                throw new RuntimeException("Rol no válido");
        }
    }

    public Turno crearTurno(Turno turno) {
        User user = getUsuarioActual();

        if (user.getRole() != Role.PACIENTE && user.getRole() != Role.ADMIN) {
            throw new RuntimeException("Solo los pacientes o administradores pueden crear turnos");
        }

        // Verificar doctor
        Doctor doctor = doctorRepo.findById(turno.getDoctor().getId())
                .orElseThrow(() -> new RuntimeException("Doctor no encontrado"));

        // Validar solapamiento
        var turnosExistentes = turnoRepo.findByDoctorIdAndFechaHora(doctor.getId(), turno.getFechaHora());
        if (!turnosExistentes.isEmpty()) {
            throw new RuntimeException("El doctor ya tiene un turno asignado en ese horario");
        }

        // Asignar paciente automáticamente si el rol es PACIENTE
        if (user.getRole() == Role.PACIENTE) {
            Paciente paciente = pacienteRepo.findById(user.getId())
                    .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
            turno.setPaciente(paciente);
        }

        turno.setEstado("reservado");
        return turnoRepo.save(turno);
    }

    public void eliminarTurno(Long id) {
        User user = getUsuarioActual();
        Turno turno = turnoRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Turno no encontrado"));

        if (user.getRole() == Role.ADMIN ||
                (user.getRole() == Role.PACIENTE && turno.getPaciente().getId().equals(user.getId()))) {
            turnoRepo.delete(turno);
        } else {
            throw new RuntimeException("No tenés permiso para eliminar este turno");
        }
    }

    private User getUsuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (User) auth.getPrincipal();
    }
}
