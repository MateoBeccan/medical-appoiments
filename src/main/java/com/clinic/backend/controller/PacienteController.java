package com.clinic.backend.controller;

import com.clinic.backend.model.Paciente;
import com.clinic.backend.repository.PacienteRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/pacientes")
@CrossOrigin(origins = "http://localhost:5173")
public class PacienteController {

    private final PacienteRepository repo;

    public PacienteController(PacienteRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Paciente> getAll() {
        return repo.findAll();
    }

    @PostMapping
    public Paciente create(@RequestBody Paciente p) {
        return repo.save(p);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repo.deleteById(id);
    }
}
