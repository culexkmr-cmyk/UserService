package com.culex.userService.controllers.user.getControllers;

import com.culex.userService.service.user.getData.GetUserData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GetProfile {

    private final GetUserData getUserData;

    @Autowired
    public GetProfile(GetUserData getUserData){
        this.getUserData=getUserData;
    }

    @GetMapping("/api/account/ProfileByUsername/{username}")
    public ResponseEntity<?> getProfileByUsername(@PathVariable String username) {
        return ResponseEntity.status(HttpStatus.OK).body(getUserData.getDataByUsername(username));
    }

    @GetMapping("/api/account/ProfileById/{id}")
    public ResponseEntity<?> getProfileById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(getUserData.getDataById(id));
    }
}