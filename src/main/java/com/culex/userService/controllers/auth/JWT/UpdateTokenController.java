package com.culex.userService.controllers.auth.JWT;

import com.culex.userService.service.auth.JWT.TokenUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UpdateTokenController {

    private final TokenUpdater tokenUpdater;

    @Autowired
    public UpdateTokenController(TokenUpdater tokenUpdater){
        this.tokenUpdater=tokenUpdater;
    }

    @PostMapping("/api/updateToken")
    public ResponseEntity<?> updateToken(@RequestBody UpdateTokenDto updateTokenDto){
        String jti=updateTokenDto.jti();
        Long userId=updateTokenDto.userId();
        return ResponseEntity.status(HttpStatus.OK).body(tokenUpdater.updateToken(userId, jti));
    }
}
