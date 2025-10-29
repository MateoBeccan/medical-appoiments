package com.clinic.backend.controller;

import com.clinic.backend.model.Turno;
import com.clinic.backend.service.TurnoService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/turnos")
@CrossOrigin(origins = "http://localhost:5173")
public class TurnoController {

    private final TurnoService turnoService;

    public TurnoController(TurnoService turnoService) {
        this.turnoService = turnoService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR') or hasRole('PACIENTE')")
    public List<Turno> listarTurnos() {
        return turnoService.listarTurnos();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('PACIENTE')")
    public Turno crearTurno(@RequestBody Turno turno) {
        return turnoService.crearTurno(turno);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PACIENTE')")
    public void eliminarTurno(@PathVariable Long id) {
        turnoService.eliminarTurno(id);
    }
}
