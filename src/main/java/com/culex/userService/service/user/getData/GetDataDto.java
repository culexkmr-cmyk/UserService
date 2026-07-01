package com.culex.userService.service.user.getData;

import com.culex.userService.DB.entities.User;

import java.time.Instant;

public record GetDataDto(String username, Long userId, String email, String nickname, Instant createdAt) {
    public static GetDataDto fromUser(User user) {
        return new GetDataDto(
                user.getUsername(),
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getCreatedAt()
        );
    }
}
