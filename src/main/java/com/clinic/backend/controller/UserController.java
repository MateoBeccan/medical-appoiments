package com.clinic.backend.controller;

import com.clinic.backend.model.*;
import com.clinic.backend.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    private final UserRepository userRepo;
    private final PacienteRepository pacienteRepo;
    private final DoctorRepository doctorRepo;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepo, PacienteRepository pacienteRepo,
                          DoctorRepository doctorRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.pacienteRepo = pacienteRepo;
        this.doctorRepo = doctorRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public List<User> listarUsuarios() {
        return userRepo.findAll();
    }

    @PostMapping("/register")
    public User crearUsuario(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User saved = userRepo.save(user);

        if (user.getRole() == Role.PACIENTE) {
            Paciente p = new Paciente();
            p.setUser(saved);
            p.setNombre(saved.getUsername());
            pacienteRepo.save(p);
        } else if (user.getRole() == Role.DOCTOR) {
            Doctor d = new Doctor();
            d.setUser(saved);
            d.setNombre(saved.getUsername());
            doctorRepo.save(d);
        }
        return saved;
    }
}
