package com.culex.userService.client;

public interface NotificationDispatcher {
    void sendNotification(NotificationType type, String recipient, String text);
}
