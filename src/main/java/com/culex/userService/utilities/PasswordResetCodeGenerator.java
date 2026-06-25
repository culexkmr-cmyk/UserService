package com.culex.userService.utilities;

import java.security.SecureRandom;

public class PasswordResetCodeGenerator {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final String ALPHANUMERIC_CHARS = 
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public static String generateNumericCode(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Длина кода должна быть положительным числом");
        }
        
        StringBuilder code = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            code.append(SECURE_RANDOM.nextInt(10));
        }
        return code.toString();
    }

    public static String generateSecureToken(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Длина токена должна быть положительным числом");
        }

        StringBuilder token = new StringBuilder(length);
        int charsLength = ALPHANUMERIC_CHARS.length();
        
        for (int i = 0; i < length; i++) {
            int randomIndex = SECURE_RANDOM.nextInt(charsLength);
            token.append(ALPHANUMERIC_CHARS.charAt(randomIndex));
        }
        return token.toString();
    }
}