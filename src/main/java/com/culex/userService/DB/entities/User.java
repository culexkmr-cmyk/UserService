package com.culex.userService.DB.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_users_username", columnNames = "username"),
                @UniqueConstraint(name = "uk_users_email", columnNames = "email")
        })
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted = false;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL,
            fetch = FetchType.LAZY, orphanRemoval = true)
    private List<RefreshToken> refreshTokens = new ArrayList<>();


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL,
            fetch = FetchType.LAZY, orphanRemoval = true)
    private List<PasswordResetToken> passwordResetTokens = new ArrayList<>();

    public User(String password, String username, String nickname, String email) {
        this.password = password;
        this.username = username;
        this.nickname = nickname;
        this.email = email;
        this.createdAt = Instant.now();
    }


    public void anonymize() {
        this.username = "deleted_" + this.id + "_" + UUID.randomUUID();
        this.email = "deleted_" + this.id + "_" + UUID.randomUUID() + "@example.com";
        this.nickname = "Deleted User";
        this.password = UUID.randomUUID().toString();
        this.deleted = true;
        this.deletedAt = Instant.now();
        this.refreshTokens.clear();
        this.passwordResetTokens.clear();
    }

    public boolean isActive() {
        return !deleted;
    }
}