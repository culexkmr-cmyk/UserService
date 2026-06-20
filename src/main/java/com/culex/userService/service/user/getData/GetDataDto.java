package com.culex.userService.service.user.getData;

import java.time.Instant;

public record GetDataDto(String username, Long userId, String email, String nickname, Instant createdAt) {
}
