package com.culex.userService.DB.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;

@Entity
@Table(name = "refreshTokens")
@Getter @Setter
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true, nullable = false)
    private String jti;

    @Column(nullable = false)
    private Instant expiresAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public RefreshToken(String jti, Instant expiresAt, User user){
        this.expiresAt=expiresAt;
        this.user=user;
        this.jti=jti;
        if (user != null) {
            user.getRefreshTokens().add(this);
        }

    }
    protected RefreshToken(){}
}