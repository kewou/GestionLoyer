package com.example.features.common.mail.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.mail.SimpleMailMessage;

import java.util.List;

@AllArgsConstructor
@Builder
@Getter
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
        simpleMailMessage.setReplyTo(sender);
        String to = String.join(",", recipients);
        simpleMailMessage.setTo(to);
        return simpleMailMessage;
    }

}
