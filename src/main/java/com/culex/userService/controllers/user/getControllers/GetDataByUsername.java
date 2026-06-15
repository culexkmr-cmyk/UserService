package com.culex.userService.controllers.user.getControllers;

import com.culex.userService.DB.entities.User;
import com.culex.userService.DB.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GetDataByUsername {

    private final UserRepository repository;

    @Autowired
    public GetDataByUsername(UserRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/api/getIdByUsername")
    public ResponseEntity<?> getIdByUsername(@RequestParam String username) {
        User user = repository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found with username: " + username));
        return ResponseEntity.ok(user.getId());
    }

    @GetMapping("/api/getEmailByUsername")
    public ResponseEntity<?> getEmailByUsername(@RequestParam String username) {
        User user = repository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found with username: " + username));
        return ResponseEntity.ok(user.getEmail());
    }

    @GetMapping("/api/getCreatedAtByUsername")
    public ResponseEntity<?> getCreatedAtByUsername(@RequestParam String username) {
        User user = repository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found with username: " + username));
        return ResponseEntity.ok(user.getCreatedAt());
    }

    @GetMapping("/api/getNicknameByUsername")
    public ResponseEntity<?> getNicknameByUsername(@RequestParam String username) {
        User user = repository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found with username: " + username));
        return ResponseEntity.ok(user.getNickname());
    }
}