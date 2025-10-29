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
    private final UserRepository userRepo;
    private final PacienteRepository pacienteRepo;

    public TurnoService(TurnoRepository turnoRepo, DoctorRepository doctorRepo,
                        UserRepository userRepo, PacienteRepository pacienteRepo) {
        this.turnoRepo = turnoRepo;
        this.doctorRepo = doctorRepo;
        this.userRepo = userRepo;
        this.pacienteRepo = pacienteRepo;
    }

    // Nuevo: listar con filtros opcionales
    public List<Turno> listarTurnosConFiltros(Long doctorId, Long pacienteId, String fecha) {
        User user = getUsuarioActual();

        // permiso básico: ADMIN ve todo; DOCTOR/PACIENTE deben filtrar sobre su id si envían null
        if (user.getRole() == Role.ADMIN) {
            // aplicar filtros
            if (doctorId != null && fecha != null) {
                LocalDate d = LocalDate.parse(fecha);
                return turnoRepo.findByDoctorIdAndFecha(doctorId, java.sql.Date.valueOf(d));
            } else if (doctorId != null) {
                return turnoRepo.findByDoctorId(doctorId);
            } else if (pacienteId != null) {
                return turnoRepo.findByPacienteId(pacienteId);
            } else if (fecha != null) {
                LocalDate d = LocalDate.parse(fecha);
                return turnoRepo.findByFecha(java.sql.Date.valueOf(d));
            } else {
                return turnoRepo.findAll();
            }
        }

        // DOCTOR
        if (user.getRole() == Role.DOCTOR) {
            Long myId = user.getId();
            if (doctorId != null && !doctorId.equals(myId)) {
                // doctor pidiendo turnos de otro doctor -> forbidden
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes ver turnos de otro doctor");
            }
            if (fecha != null) {
                LocalDate d = LocalDate.parse(fecha);
                return turnoRepo.findByDoctorIdAndFecha(myId, java.sql.Date.valueOf(d));
            }
            return turnoRepo.findByDoctorId(myId);
        }

        // PACIENTE
        if (user.getRole() == Role.PACIENTE) {
            Long myId = user.getId();
            if (pacienteId != null && !pacienteId.equals(myId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes ver turnos de otro paciente");
            }
            if (fecha != null) {
                LocalDate d = LocalDate.parse(fecha);
                return turnoRepo.findByPacienteIdAndFecha(myId, java.sql.Date.valueOf(d));
            }
            return turnoRepo.findByPacienteId(myId);
        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Rol no válido");
    }

    public Turno crearTurno(Turno turno) {
        User user = getUsuarioActual();

        if (user.getRole() != Role.PACIENTE && user.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo pacientes o administradores pueden crear turnos");
        }

        // verificar doctor existe
        if (turno.getDoctor() == null || turno.getDoctor().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Doctor obligatorio");
        }
        Doctor doctor = doctorRepo.findById(turno.getDoctor().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Doctor no encontrado"));

        // si admin crea, paciente debe venir en el body (id)
        if (user.getRole() == Role.ADMIN) {
            if (turno.getPaciente() == null || turno.getPaciente().getId() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe asignar un paciente al crear turno como ADMIN");
            }
            pacienteRepo.findById(turno.getPaciente().getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paciente no encontrado"));
        } else {
            // si paciente crea, asignarlo
            Paciente paciente = pacienteRepo.findByUserId(user.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paciente no encontrado"));
            turno.setPaciente(paciente);
        }

        // validar fecha/hora
        if (turno.getFechaHora() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "fechaHora obligatoria");
        }

        // validar solapamiento del doctor (mismo doctor y misma fechaHora)
        List<Turno> existentes = turnoRepo.findByDoctorIdAndFechaHora(doctor.getId(), turno.getFechaHora());
        if (!existentes.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El doctor ya tiene un turno en ese horario");
        }

        if (turno.getEstado() == null) turno.setEstado("reservado");
        // asignar doctor entity (ya cargado)
        turno.setDoctor(doctor);

        return turnoRepo.save(turno);
    }

    public void eliminarTurno(Long id) {
        User user = getUsuarioActual();
        Turno t = turnoRepo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Turno no encontrado"));

        if (user.getRole() == Role.ADMIN) {
            turnoRepo.delete(t);
            return;
        }
        if (user.getRole() == Role.PACIENTE) {
            if (t.getPaciente() == null || !t.getPaciente().getId().equals(user.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No podés eliminar este turno");
            }
            turnoRepo.delete(t);
            return;
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tenés permiso para eliminar este turno");
    }

    private User getUsuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (User) auth.getPrincipal();
    }
}
