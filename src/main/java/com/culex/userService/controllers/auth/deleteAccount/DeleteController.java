package com.culex.userService.controllers.auth.deleteAccount;

import com.culex.userService.service.auth.deleteAccount.DeleteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class DeleteController {

    private final DeleteService deleteService;

    @Autowired
    public DeleteController(DeleteService deleteService){
        this.deleteService=deleteService;
    }

    @DeleteMapping("/api/account")
    public ResponseEntity<?> deleteAccount(@RequestHeader("X-User-Id") Long userId){
        deleteService.deleteUser(userId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
