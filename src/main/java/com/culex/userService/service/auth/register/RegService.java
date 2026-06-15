package com.culex.userService.service.auth.register;

import com.culex.userService.DB.entities.User;
import com.culex.userService.DB.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class RegService {
    private final RegValidation validation;
    private final UserRepository repository;
    private final PasswordEncoder encoder;
    @Autowired
    public RegService(RegValidation validation, UserRepository repository,PasswordEncoder encoder){
        this.validation=validation;
        this.repository=repository;
        this.encoder=encoder;
    }
    public User createUser(String password, String username, String nickname, String email){
        validation.allValidation(nickname, password,username,email);
        String passwordHash=encoder.encode(password);
        return repository.save(new User(passwordHash, username, nickname, email));
    }
}
