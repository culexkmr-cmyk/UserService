package com.culex.userService.service.user.changeNickname;

import com.culex.userService.DB.entities.User;
import com.culex.userService.DB.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChangeNicknameService {

    private final ChangeNicknameValidation validation;
    private final UserRepository repository;

    @Autowired
    public ChangeNicknameService(ChangeNicknameValidation validation, UserRepository repository) {
        this.validation = validation;
        this.repository = repository;
    }

    @Transactional
    public User changeNickname(String newNickname, Long userId) {
        User user = validation.allValidation(newNickname, userId);
        user.setNickname(newNickname);
        return repository.save(user);
    }
}
