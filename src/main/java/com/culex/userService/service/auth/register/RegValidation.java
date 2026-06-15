    package com.culex.userService.service.auth.register;

    import org.apache.commons.validator.routines.EmailValidator;
    import com.culex.userService.DB.repositories.UserRepository;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.stereotype.Service;

    @Service
    public class RegValidation {

        private final UserRepository repository;

        private final int maxPasswordLength;
        private final int minPasswordLength;
        private final int maxUsernameLength;
        private final int minUsernameLength;
        private final int maxNicknameLength;

        @Autowired
        public RegValidation(UserRepository repository,
                            @Value("${app.auth.register.password.length.max}") int maxPasswordLength,
                            @Value("${app.auth.register.password.length.min}") int minPasswordLength,
                            @Value("${app.auth.register.username.length.max}") int maxUsernameLength,
                            @Value("${app.auth.register.username.length.min}") int minUsernameLength,
                            @Value("${app.auth.register.nickname.length.max}") int maxNicknameLength) {
            this.repository = repository;
            this.maxPasswordLength=maxPasswordLength;
            this.minPasswordLength=minPasswordLength;
            this.maxUsernameLength=maxUsernameLength;
            this.minUsernameLength=minUsernameLength;
            this.maxNicknameLength=maxNicknameLength;
        }

        public void usernameValidation(String username) {
            if (username == null || username.length() < minUsernameLength || username.length() > maxUsernameLength) {
                throw new IllegalArgumentException("Username must be longer than " + (minUsernameLength-1)+" and less than " + (maxUsernameLength-1)+ " character");
            }
            if (repository.findByUsername(username).isPresent()) {
                throw new IllegalArgumentException("User with this username already exists");
            }
            if (username.startsWith("deleted_")) {
                throw new IllegalArgumentException("Username cannot start with 'deleted_'");
            }
        }
        public void passwordValidation(String rawPassword) {
            if (rawPassword == null || rawPassword.length() < minPasswordLength || rawPassword.length() > maxPasswordLength) {
                throw new IllegalArgumentException("Password must be longer than " + (minPasswordLength-1)+" and less than " + (maxPasswordLength-1)+ " character");
            }
        }
        public void nicknameValidation(String nickname) {
            if (nickname == null || nickname.length() > maxNicknameLength) {
                throw new IllegalArgumentException("Nickname must be less than "+ (maxNicknameLength+1) +" character");
            }
        }
        public void emailValidation(String email) {
            if (email == null || !EmailValidator.getInstance().isValid(email)) {
                throw new IllegalArgumentException("Invalid email format");
            }
            if (email.startsWith("deleted_")) {
                throw new IllegalArgumentException("Email cannot start with 'deleted_'");
            }
            if (repository.findByEmail(email).isPresent()) {
                throw new IllegalArgumentException("Email already exists");
            }
        }
        public void allValidation(String nickname, String rawPassword, String username, String email) {
            nicknameValidation(nickname);
            passwordValidation(rawPassword);
            usernameValidation(username);
            emailValidation(email);
        }
    }