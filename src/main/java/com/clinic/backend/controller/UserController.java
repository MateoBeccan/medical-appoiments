package com.clinic.backend.controller;

import com.clinic.backend.model.Role;
import com.clinic.backend.model.User;
import com.clinic.backend.repository.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')") // Solo admin puede crear usuarios
    public User createUser(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword())); // encriptar contraseña
        if(user.getRole() == null) {
            user.setRole(Role.PACIENTE); // default
        }
        return userRepo.save(user);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public User updateUser(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        if(user.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepo.save(user);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(@PathVariable Long id) {
        userRepo.deleteById(id);
    }
}
