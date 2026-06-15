package com.culex.userService.service.auth.deleteAccount;

import com.culex.userService.DB.entities.User;
import com.culex.userService.DB.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeleteValidation {

    private final UserRepository userRepository;

    @Autowired
    public DeleteValidation(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User userIdValidation(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
    }
    public User isUserDelete(User user){
        if (user.isDeleted()) {
            throw new IllegalStateException("User already deleted");
        }
        return user;
    }
    public User allValidation(Long userId){
        return isUserDelete(userIdValidation(userId));
    }
}
