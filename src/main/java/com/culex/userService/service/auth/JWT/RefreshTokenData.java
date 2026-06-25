package com.culex.userService.service.auth.JWT;

import java.time.Instant;

public record RefreshTokenData(String token, Instant expiresAt, String jti) {}