package com.clinic.backend.controller;

import com.clinic.backend.model.Turno;
import com.clinic.backend.service.TurnoService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/turnos")
@CrossOrigin(origins = "http://localhost:5173")
public class TurnoController {

    private final TurnoService turnoService;

    public TurnoController(TurnoService turnoService) {
        this.turnoService = turnoService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','PACIENTE')")
    public List<TurnoResponseDTO> listarTurnos(
            @RequestParam(required = false) Long doctorId,
            @RequestParam(required = false) Long pacienteId,
            @RequestParam(required = false) String fecha // yyyy-MM-dd
    ) {
        // delega a servicio la lógica de filtros
        return turnoService.listarTurnosConFiltros(doctorId, pacienteId, fecha)
                .stream()
                .map(TurnoResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','PACIENTE')")
    public TurnoResponseDTO crearTurno(@RequestBody Turno turno) {
        Turno creado = turnoService.crearTurno(turno);
        return TurnoResponseDTO.fromEntity(creado);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PACIENTE')")
    public void eliminarTurno(@PathVariable Long id) {
        turnoService.eliminarTurno(id);
    }
}
