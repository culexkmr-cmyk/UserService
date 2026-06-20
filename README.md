# UserService

### Microservice for manage of users' accounts.

## API

## DELETE /api/account

### Delete user account.

- Request header: X-User-Id (Long) – user identifier.
- Response: HTTP status only (200 OK).

## POST /api/auth/updateToken

### update access and refresh tokens.

- Request header: X-User-Id (Long) – user identifier.
- Request body (JSON): { "jti": String }
- Response (JSON): { "accessToken": String, "refreshToken": String }

## POST /api/auth/login
### Authenticate user and get tokens.

- Request body (JSON): { "password": String, "username": String }
- Response (JSON): { "accessToken": String, "refreshToken": String }

## DELETE /api/auth/logout
### Invalidate refresh token (logout).

- Request header: X-User-Id (Long) – user identifier.
- Request body (JSON): { "jti": String }
- Response: HTTP status only (200 OK).

## POST /api/account
### Create a new user account.

- Request body (JSON): { "password": String, "username": String, "nickname": String, "email": String }
- Response (JSON): { "userId": Long, "username": String } (201 Created)

## PATCH /api/account/{userId}/nickname
### Change user's nickname.

- Path variable: userId (Long) – user identifier.
- Request body (JSON): { "newNickname": String }
- Response: "success nickname change for user <username>" (200 OK)

## GET /api/account/ProfileByUsername/{username}
### get full user profile by username.

- Path variable: username (String) – login.
- Response (JSON): { "username": String, "userId": Long, "email": String, "nickname": String, "createdAt": Instant }

## GET /api/account/ProfileById/{userId}
### Get full user profile by user ID.

- Path variable: id (Long) – user identifier.
- Response (JSON): { "username": String, "userId": Long, "email": String, "nickname": String, "createdAt": Instant }