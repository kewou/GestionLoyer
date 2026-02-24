package com.example.features.common.mail;

import jakarta.mail.MessagingException;

public interface MessageService {

    void sendMessage(MessageDto messageDto);

    void sendHtmlMessage(MessageDto messageDto) throws MessagingException;
}


