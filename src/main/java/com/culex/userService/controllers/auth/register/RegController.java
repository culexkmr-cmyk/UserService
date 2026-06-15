package com.culex.userService.controllers.auth.register;

import com.culex.userService.DB.entities.User;
import com.culex.userService.service.auth.register.RegService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegController {

    private final RegService regService;

    @Autowired
    public RegController(RegService regService){
        this.regService=regService;
    }

    @PostMapping("/api/register")
    public ResponseEntity<?> register(@RequestBody RegDto dto){
        String password=dto.password();
        String username=dto.username();
        String nickname=dto.nickname();
        String email=dto.email();
        User user=regService.createUser(password, username, nickname,email);
        Long userId=user.getId();
        return ResponseEntity.status(HttpStatus.CREATED).body(new RegResponse(username, userId));
    }
}
