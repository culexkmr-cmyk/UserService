package com.culex.userService.service.Auth.login;

import com.culex.userService.DB.entities.User;
import com.culex.userService.DB.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LoginValidation {
    private final PasswordEncoder encoder;
    private final UserRepository userRepository;

    @Autowired
    public LoginValidation(PasswordEncoder encoder, UserRepository userRepository) {
        this.encoder = encoder;
        this.userRepository = userRepository;
    }
    public User usernameValidation(String username){
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }
    public User passwordValidation(String password, User user) {
        if (!encoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Invalid password for user: " + user.getUsername());
        }
        return user;
    }
    public User isUserDelete(User user){
        if (user.isDeleted()) {
            throw new IllegalStateException("User is deleted");
        }
        return user;
    }

    public User allValidation(String password, String username){
        return passwordValidation(password, isUserDelete(usernameValidation(username)));

    }
}
