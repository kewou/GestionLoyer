package com.example.features.common.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@Profile("!test")
public class SimpleMailService implements MessageService {

    @Autowired
    private JavaMailSender emailSender;

    @Override
    public void sendMessage(MessageDto messageDto) {
        emailSender.send(messageDto.convertToSimpleMessage());
    }

    public void sendHtmlMessage(MessageDto messageDto) throws MessagingException {
        MimeMessage mimeMessage = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

        helper.setFrom(messageDto.getSender());
        helper.setSubject(messageDto.getSubject());
        helper.setTo(messageDto.getRecipients().toArray(new String[0]));
        helper.setReplyTo(messageDto.getSender());

        // Set the email content (HTML format)
        helper.setText(messageDto.getMessage(), true);
        emailSender.send(mimeMessage);
    }
}
