package com.culex.userService.client;

import org.springframework.stereotype.Service;

@Service
public class NotificationStub implements NotificationDispatcher{
    @Override
    public void sendNotification(NotificationType type, String recipient, String text) {

    }
}
