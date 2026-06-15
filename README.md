# UserService

Microservice for manage of users' accounts.

## API

### DELETE /api/deleteAccount
Delete user account.
- **Request parameter**: `userId` (Long) – передаётся как query-параметр.
- **Response**: HTTP status only.

### POST /api/updateToken
Update refresh token.
- **Request body (JSON)**: `{ "userId": Long, "jti": String }`
- **Response (JSON)**: `{ "accessToken": String, "refreshToken": String }`

### POST /api/login
Get access token and refresh token.
- **Request body (JSON)**: `{ "password": String, "username": String }`
- **Response (JSON)**: `{ "accessToken": String, "refreshToken": String }`

### DELETE /api/logout
Delete refresh token.
- **Request body (JSON)**: `{ "userId": Long, "jti": String }`
- **Response**: HTTP status only.

### POST /api/register
Create new user.
- **Request body (JSON)**: `{ "password": String, "username": String, "nickname": String, "email": String }`
- **Response (JSON)**: `{ "userId": Long, "username": String }`

### PATCH /api/updateNickname
Change nickname.
- **Request body (JSON)**: `{ "newNickname": String, "userId": Long }`
- **Response**: HTTP status only.

### GET /api/getUsernameById
Get username by user id.
- **Request parameter**: `id` (Long) – query parameter.
- **Response (String)**: username.

### GET /api/getEmailById
Get email by user id.
- **Request parameter**: `id` (Long) – query parameter.
- **Response (String)**: email.

### GET /api/getNicknameById
Get nickname by user id.
- **Request parameter**: `id` (Long) – query parameter.
- **Response (String)**: nickname.

### GET /api/getCreatedAtById
Get creation date by user id.
- **Request parameter**: `id` (Long) – query parameter.
- **Response (Instant)**: creation timestamp.

### GET /api/getIdByUsername
Get username by username (returns the same string, but kept for consistency).
- **Request parameter**: `username` (String) – query parameter.
- **Response (String)**: id.

### GET /api/getEmailByUsername
Get email by username.
- **Request parameter**: `username` (String) – query parameter.
- **Response (String)**: email.

### GET /api/getNicknameByUsername
Get nickname by username.
- **Request parameter**: `username` (String) – query parameter.
- **Response (String)**: nickname.

### GET /api/getCreatedAtByUsername
Get creation date by username.
- **Request parameter**: `username` (String) – query parameter.
- **Response (Instant)**: creation timestamp.