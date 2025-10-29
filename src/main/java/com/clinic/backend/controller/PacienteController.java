package com.clinic.backend.controller;

import com.clinic.backend.model.Paciente;
import com.clinic.backend.repository.PacienteRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/pacientes")
@CrossOrigin(origins = "http://localhost:5173")
public class PacienteController {

    private final PacienteRepository pacienteRepo;

    public PacienteController(PacienteRepository pacienteRepo) {
        this.pacienteRepo = pacienteRepo;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public List<Paciente> getAll() {
        return pacienteRepo.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','PACIENTE')")
    public Paciente getById(@PathVariable Long id) {
        return pacienteRepo.findById(id).orElseThrow();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Paciente create(@RequestBody Paciente paciente) {
        return pacienteRepo.save(paciente);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PACIENTE')")
    public Paciente update(@PathVariable Long id, @RequestBody Paciente paciente) {
        paciente.setId(id);
        return pacienteRepo.save(paciente);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        pacienteRepo.deleteById(id);
    }
}
