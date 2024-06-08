package com.example.features.common.mail.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.mail.SimpleMailMessage;

import java.util.List;

@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MessageDto {

    String message;
    String sender;

    String subject;
    List<String> recipients;

    public SimpleMailMessage convertToSimpleMessage() {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setText(message);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setFrom(sender);
        String to = String.join(",", recipients);
        simpleMailMessage.setTo(to);
        return simpleMailMessage;
    }

}
