package com.clinic.backend.controller;

import com.clinic.backend.model.Turno;
import java.time.LocalDateTime;

public record TurnoResponseDTO(
        Long id,
        String estado,
        LocalDateTime fechaHora,
        Long doctorId,
        String doctorNombre,
        String doctorEspecialidad,
        Long pacienteId,
        String pacienteUsername
) {
    public static TurnoResponseDTO fromEntity(Turno t) {
        Long docId = t.getDoctor() != null ? t.getDoctor().getId() : null;
        String docNombre = t.getDoctor() != null ? t.getDoctor().getNombre() : null;
        String docEsp = t.getDoctor() != null ? t.getDoctor().getEspecialidad() : null;
        Long pacId = t.getPaciente() != null ? t.getPaciente().getId() : null;
        String pacUser = t.getPaciente() != null && t.getPaciente().getUser() != null
                ? t.getPaciente().getUser().getUsername()
                : null;
        return new TurnoResponseDTO(
                t.getId(),
                t.getEstado(),
                t.getFechaHora(),
                docId,
                docNombre,
                docEsp,
                pacId,
                pacUser
        );
    }
}
