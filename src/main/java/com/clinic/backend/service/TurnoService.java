package com.clinic.backend.service;

import com.clinic.backend.model.*;
import com.clinic.backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TurnoService {

    private final TurnoRepository turnoRepo;
    private final DoctorRepository doctorRepo;
    private final PacienteRepository pacienteRepo;
    private final UserRepository userRepo;

    public TurnoService(TurnoRepository turnoRepo, DoctorRepository doctorRepo,
                        PacienteRepository pacienteRepo, UserRepository userRepo) {
        this.turnoRepo = turnoRepo;
        this.doctorRepo = doctorRepo;
        this.pacienteRepo = pacienteRepo;
        this.userRepo = userRepo;
    }

    private User getUsuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (User) auth.getPrincipal();
    }

    public List<Turno> listarTurnos() {
        User user = getUsuarioActual();

        switch (user.getRole()) {
            case ADMIN:
                return turnoRepo.findAll();
            case DOCTOR:
                Doctor doctor = doctorRepo.findByUser(user)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Doctor no encontrado"));
                return turnoRepo.findByDoctorId(doctor.getId());
            case PACIENTE:
                Paciente paciente = pacienteRepo.findByUser(user)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paciente no encontrado"));
                return turnoRepo.findByPacienteId(paciente.getId());
            default:
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Rol no válido");
        }
    }

    public Turno crearTurno(Turno turno) {
        User user = getUsuarioActual();

        if (user.getRole() != Role.PACIENTE && user.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo pacientes o administradores pueden crear turnos");
        }

        if (turno.getDoctor() == null || turno.getDoctor().getId() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Doctor obligatorio");

        Doctor doctor = doctorRepo.findById(turno.getDoctor().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Doctor no encontrado"));

        if (user.getRole() == Role.PACIENTE) {
            Paciente paciente = pacienteRepo.findByUser(user)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paciente no encontrado"));
            turno.setPaciente(paciente);
        } else {
            if (turno.getPaciente() == null || turno.getPaciente().getId() == null)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe asignar un paciente");
        }

        if (turno.getFechaHora() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "fechaHora obligatoria");

        List<Turno> existentes = turnoRepo.findByDoctorIdAndFechaHora(doctor.getId(), turno.getFechaHora());
        if (!existentes.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El doctor ya tiene un turno en ese horario");

        if (turno.getEstado() == null) turno.setEstado("reservado");
        turno.setDoctor(doctor);

        return turnoRepo.save(turno);
    }

    public void eliminarTurno(Long id) {
        User user = getUsuarioActual();
        Turno t = turnoRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Turno no encontrado"));

        if (user.getRole() == Role.ADMIN ||
                (user.getRole() == Role.PACIENTE && t.getPaciente().getUser().getId().equals(user.getId()))) {
            turnoRepo.delete(t);
            return;
        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para eliminar este turno");
    }
}
