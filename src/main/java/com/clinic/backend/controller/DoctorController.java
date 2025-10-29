package com.clinic.backend.controller;

import com.clinic.backend.model.Doctor;
import com.clinic.backend.repository.DoctorRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/doctores")
@CrossOrigin(origins = "http://localhost:5173")
public class DoctorController {

    private final DoctorRepository doctorRepo;

    public DoctorController(DoctorRepository doctorRepo) {
        this.doctorRepo = doctorRepo;
    }

    @GetMapping
    public List<Doctor> getAll() {
        return doctorRepo.findAll();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public Doctor create(@RequestBody Doctor doctor) {
        return doctorRepo.save(doctor);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public Doctor update(@PathVariable Long id, @RequestBody Doctor doctor) {
        doctor.setId(id);
        return doctorRepo.save(doctor);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        doctorRepo.deleteById(id);
    }
}
