package com.culex.userService.service.auth.deleteAccount;

import com.culex.userService.DB.entities.User;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Transactional
@Service
public class DeleteService {

    private final DeleteValidation deleteValidation;

    @Autowired
    public DeleteService(DeleteValidation deleteValidation) {
        this.deleteValidation = deleteValidation;
    }
    @Transactional
    public void deleteUser(Long userId){
        User user=deleteValidation.allValidation(userId);
        user.anonymize();
    }
}