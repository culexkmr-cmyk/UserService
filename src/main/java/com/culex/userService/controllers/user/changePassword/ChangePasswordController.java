package com.culex.userService.controllers.user.changePassword;

import com.culex.userService.DB.entities.User;
import com.culex.userService.service.user.changePassword.ChangePasswordService;
import com.culex.userService.service.user.getData.GetDataDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChangePasswordController {
    private final ChangePasswordService changePasswordService;
    @Autowired
    public ChangePasswordController(ChangePasswordService changePasswordService){
        this.changePasswordService=changePasswordService;
    }
    @PostMapping
    public ResponseEntity<?> createToken(@RequestBody Long userId){
        changePasswordService.savePasswordResetToken(userId);
        return ResponseEntity.status(HttpStatus.CREATED).body("Token successfully created");
    }
    @PostMapping
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequestDto requestDto){
        User user=changePasswordService.setNewPassword(requestDto.rawPassword, requestDto.userId, requestDto.token);
        return ResponseEntity.status(HttpStatus.CREATED).body(GetDataDto.fromUser(user));
    }
    public record ChangePasswordRequestDto(String rawPassword, Long userId, String token){};
}
