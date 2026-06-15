package com.culex.userService.controllers.auth.logout;

import com.culex.userService.service.auth.logout.LogoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LogoutController {

    private final LogoutService logoutService;

    @Autowired
    public LogoutController(LogoutService logoutService){
        this.logoutService=logoutService;
    }

    @DeleteMapping("api/logout")
    public ResponseEntity<?> logout(@RequestBody LogoutDto dto){
        Long userId=dto.userId();
        String jti=dto.jti();
        logoutService.logout(userId, jti);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
