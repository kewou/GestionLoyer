package com.example.features.common.mail.application;

import com.example.features.common.mail.dto.MessageDto;

public interface MessageService {

    void sendMessage(MessageDto messageDto);
}
