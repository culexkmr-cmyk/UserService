package com.culex.userService.service.user.changeNickname;

import com.culex.userService.DB.entities.User;
import com.culex.userService.DB.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ChangeNicknameValidation {

    private final int maxNicknameLength;


    @Autowired
    public ChangeNicknameValidation(
                                    @Value("${app.auth.register.nickname.length.max:}") int maxNicknameLength){
        this.maxNicknameLength=maxNicknameLength;
    }

    public void nicknameValidation(String nickname) {
        if (nickname == null || nickname.length() > maxNicknameLength) {
            throw new IllegalArgumentException("Nickname must be less than "+ (maxNicknameLength+1) +" character");
        }
    }

    private void isUserDelete(User user){
        if (user.isDeleted()) {
            throw new IllegalStateException("User is deleted");
        }
    }

    public void allValidation(String nickname, User user){
        nicknameValidation(nickname);
        isUserDelete(user);
    }
}

