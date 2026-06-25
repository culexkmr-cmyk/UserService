package com.culex.userService.service.user.changePassword;

import com.culex.userService.DB.entities.PasswordResetToken;
import com.culex.userService.DB.entities.User;
import com.culex.userService.DB.repositories.PasswordResetTokenRepository;
import com.culex.userService.DB.repositories.UserRepository;
import com.culex.userService.client.NotificationDispatcher;
import com.culex.userService.client.NotificationType;
import com.culex.userService.utilities.PasswordResetCodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

import static com.culex.userService.utilities.PasswordResetCodeGenerator.generateNumericCode;
import static com.culex.userService.utilities.Test.findEntity;

@Service
public class ChangePasswordService {
    private final long resetTokenExpirationMs;
    private final ChangePasswordValidation validation;;
    private final PasswordResetTokenRepository resetTokenRepository;
    private final NotificationDispatcher notificationDispatcher;
    private final UserRepository userRepository;

    private final PasswordEncoder encoder;
    @Autowired
    public ChangePasswordService(PasswordResetTokenRepository resetTokenRepository, ChangePasswordValidation validation, NotificationDispatcher notificationDispatcher,
                                 UserRepository userRepository,PasswordEncoder encoder, @Value("${app.auth.reset.password.reset-expiration-ms: 900000}") long resetExp){
        this.validation=validation;
        this.encoder=encoder;
        this.resetTokenRepository=resetTokenRepository;
        this.resetTokenExpirationMs=resetExp;
        this.notificationDispatcher=notificationDispatcher;
        this.userRepository=userRepository;

    }
    public PasswordResetToken savePasswordResetToken(Long userId){
        Instant now = Instant.now();

        User user = findEntity(userRepository, userId);

        resetTokenRepository.deleteAllByUser(user);

        String rawCode = generateNumericCode(10);
        String hashedCode = encoder.encode(rawCode);

        PasswordResetToken token = new PasswordResetToken(hashedCode, user, Date.from(now.plusMillis(resetTokenExpirationMs)).toInstant());
        PasswordResetToken savedToken = resetTokenRepository.save(token);
        notificationDispatcher.sendNotification(NotificationType.EMAIL,user.getEmail(), rawCode);
        return savedToken;
    }
    public User setNewPassword(String rawPassword, Long userId, String token){
        User user=findEntity(userRepository, userId);
        PasswordResetToken resetToken=findEntity(resetTokenRepository, token);
        validation.allValidation(rawPassword,user, resetToken);
        user.setPassword(encoder.encode(rawPassword));
        return userRepository.save(user);
    }
}
