package com.example;

import com.example.features.common.mail.application.MessageService;
import com.example.features.common.mail.dto.MessageDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Profile("test")
@Service
public class LoggingMailService implements MessageService {
    @Override
    public void sendMessage(MessageDto messageDto) {
        log.info("Fake mail to ");
    }
}
