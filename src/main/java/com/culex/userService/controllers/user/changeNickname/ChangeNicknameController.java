package com.culex.userService.controllers.user.changeNickname;

import com.culex.userService.DB.entities.User;
import com.culex.userService.service.user.changeNickname.ChangeNicknameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ChangeNicknameController {

    private final ChangeNicknameService changeNicknameService;

    @Autowired
    public ChangeNicknameController(ChangeNicknameService changeNicknameService){
        this.changeNicknameService=changeNicknameService;
    }

    @PatchMapping("/api/account/{id}/nickname")
    public ResponseEntity<?> updateNickname(@RequestBody ChangeNicknameDto changeNicknameDto, @PathVariable Long id){
        String newNickname = changeNicknameDto.newNickname();
        User user=changeNicknameService.changeNickname(newNickname, id);
        return ResponseEntity.status(HttpStatus.OK).body("success nickname change for user "+ user.getUsername());
    }
}
