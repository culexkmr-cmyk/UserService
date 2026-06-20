package com.culex.userService.controllers.auth.login;

import com.culex.userService.service.auth.login.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class LoginController {

    public final LoginService loginService;

    @Autowired
    public LoginController(LoginService loginService){
        this.loginService=loginService;
    }

    @PostMapping("/api/auth/login")
    public ResponseEntity<?> login(@RequestBody LoginDto dto){
        String password=dto.password();
        String username=dto.username();
        return ResponseEntity.status(HttpStatus.OK).body(loginService.login(password, username));
    }
}
