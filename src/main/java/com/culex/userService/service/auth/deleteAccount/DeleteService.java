package com.culex.userService.service.auth.deleteAccount;

import com.culex.userService.DB.entities.User;
import com.culex.userService.DB.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.culex.userService.utilities.Test.findEntity;

@Transactional
@Service
public class DeleteService {

    private final DeleteValidation deleteValidation;
    private final UserRepository userRepository;

    @Autowired
    public DeleteService(DeleteValidation deleteValidation, UserRepository userRepository) {
        this.deleteValidation = deleteValidation;
        this.userRepository=userRepository;
    }
    @Transactional
    public void deleteUser(Long userId){
        User user=findEntity(userRepository, userId);
        deleteValidation.allValidation(user);
        user.anonymize();
    }
}