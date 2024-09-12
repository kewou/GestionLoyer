package com.example.features.common.mail.application;

import com.example.features.common.mail.dto.MessageDto;

import javax.mail.MessagingException;

public interface MessageService {

    void sendMessage(MessageDto messageDto);

    void sendHtmlMessage(MessageDto messageDto) throws MessagingException;
}
