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

    private final UserRepository repository;

    @Autowired
    public ChangeNicknameValidation(UserRepository repository,
                                    @Value("${app.auth.register.nickname.length.max:}") int maxNicknameLength){
        this.repository=repository;
        this.maxNicknameLength=maxNicknameLength;
    }

    public void nicknameValidation(String nickname) {
        if (nickname == null || nickname.length() > maxNicknameLength) {
            throw new IllegalArgumentException("Nickname must be less than "+ (maxNicknameLength+1) +" character");
        }
    }
    public User userIdValidation(Long userId){
        return repository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
    }
    public User isUserDelete(User user){
        if (user.isDeleted()) {
            throw new IllegalStateException("User is deleted");
        }
        return user;
    }
    public User allValidation(String nickname, Long userid){
        nicknameValidation(nickname);
        return isUserDelete(userIdValidation(userid));
    }
}

