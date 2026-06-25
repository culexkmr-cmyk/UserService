package com.culex.userService.service.auth.deleteAccount;

import com.culex.userService.DB.entities.User;
import com.culex.userService.DB.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeleteValidation {

    private void isUserDelete(User user){
        if (user.isDeleted()) {
            throw new IllegalStateException("User already deleted");
        }
    }
    public void allValidation(User user){
        isUserDelete(user);
    }
}
