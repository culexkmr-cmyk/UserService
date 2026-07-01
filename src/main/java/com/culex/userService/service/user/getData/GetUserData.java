package com.culex.userService.service.user.getData;

import com.culex.userService.DB.entities.User;
import com.culex.userService.DB.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.culex.userService.utilities.EntityUtils.findEntity;
import static com.culex.userService.utilities.UserUtils.findUserByUsername;

@Service
public class GetUserData {

    private final UserRepository userRepository;
    private final GetDataValidation validation;

    @Autowired
    public GetUserData(UserRepository userRepository, GetDataValidation validation) {
        this.userRepository = userRepository;
        this.validation = validation;
    }

    public GetDataDto getDataById(Long userId) {
        validation.allValidation(userId);
        User user = findEntity(userRepository, userId);
        return GetDataDto.fromUser(user);
    }

    public GetDataDto getDataByUsername(String username){
        validation.allValidation(username);
        User user=findUserByUsername(userRepository, username);
        return GetDataDto.fromUser(user);
    }
}
