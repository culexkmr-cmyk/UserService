package com.culex.userService.service.user.getData;

import com.culex.userService.DB.entities.User;
import com.culex.userService.DB.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class GetUserData {

    private final UserRepository repository;
    private final GetDataValidation validation; // Инъекция валидации

    @Autowired
    public GetUserData(UserRepository repository, GetDataValidation validation) {
        this.repository = repository;
        this.validation = validation;
    }

    public GetDataDto getDataById(Long userId) {
        validation.allValidation(userId);
        User user = repository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        return createDto(user);
    }

    public GetDataDto getDataByUsername(String username){
        validation.allValidation(username);
        User user=repository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        return createDto(user);
    }
    private GetDataDto createDto(User user){
        String username = user.getUsername();
        Long userId = user.getId();
        String nickname = user.getNickname();
        String email = user.getEmail();
        Instant created_at = user.getCreatedAt();
        return new GetDataDto(username, userId, email,nickname,created_at);
    }
}
