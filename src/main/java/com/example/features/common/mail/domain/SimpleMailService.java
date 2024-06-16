package com.example.features.common.mail.domain;

import com.example.features.common.mail.application.MessageService;
import com.example.features.common.mail.dto.MessageDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Profile("!test")
public class SimpleMailService implements MessageService {

    @Autowired
    private JavaMailSender emailSender;

    @Override
    public void sendMessage(MessageDto messageDto) {
        emailSender.send(messageDto.convertToSimpleMessage());
    }
}
