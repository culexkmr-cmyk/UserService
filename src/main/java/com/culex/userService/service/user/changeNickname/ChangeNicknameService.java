package com.culex.userService.service.user.changeNickname;

import com.culex.userService.DB.entities.User;
import com.culex.userService.DB.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static com.culex.userService.utilities.EntityUtils.findEntity;

@Service
public class ChangeNicknameService {

    private final ChangeNicknameValidation validation;
    private final UserRepository userRepository;

    @Autowired
    public ChangeNicknameService(ChangeNicknameValidation validation, UserRepository userRepository,
                                 @Value("${app.auth.register.nickname.length.max:}") int maxNicknameLength) {
        this.validation = validation;
        this.userRepository = userRepository;
    }

    @Transactional
    public User changeNickname(String newNickname, Long userId) {
        User user=findEntity(userRepository,userId);
        validation.allValidation(newNickname, user);
        user.setNickname(newNickname);
        return userRepository.save(user);
    }
}
