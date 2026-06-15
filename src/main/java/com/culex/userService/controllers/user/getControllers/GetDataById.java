package com.culex.userService.controllers.user.getControllers;

import com.culex.userService.DB.entities.User;
import com.culex.userService.DB.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GetDataById {

    private final UserRepository repository;

    @Autowired
    public GetDataById(UserRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/api/getUsernameById")
    public ResponseEntity<?> getUsernameById(@RequestParam Long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        return ResponseEntity.ok(user.getUsername());
    }

    @GetMapping("/api/getEmailById")
    public ResponseEntity<?> getEmailById(@RequestParam Long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        return ResponseEntity.ok(user.getEmail());
    }

    @GetMapping("/api/getCreatedAtById")
    public ResponseEntity<?> getCreatedAtById(@RequestParam Long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        return ResponseEntity.ok(user.getCreatedAt());
    }

    @GetMapping("/api/getNicknameById")
    public ResponseEntity<?> getNicknameById(@RequestParam Long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        return ResponseEntity.ok(user.getNickname());
    }
}