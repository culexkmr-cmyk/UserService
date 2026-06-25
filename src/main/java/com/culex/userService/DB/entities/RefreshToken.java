package com.culex.userService.DB.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
@NoArgsConstructor
public class RefreshToken {


    @Id
    @Column(name = "jti", updatable = false, nullable = false, length = 36)
    private String jti;


    @Column(nullable = false)
    private Instant expiresAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public RefreshToken(String jti, Instant expiresAt, User user) {
        this.jti = jti;
        this.expiresAt = expiresAt;
        this.user = user;
        if (user != null) {
            user.getRefreshTokens().add(this);
        }
    }
}