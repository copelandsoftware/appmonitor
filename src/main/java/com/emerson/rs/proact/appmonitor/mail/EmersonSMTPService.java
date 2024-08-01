package com.emerson.rs.proact.appmonitor.mail;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

@Service
public class EmersonSMTPService implements EmailService{
    @Resource
    private JavaMailSender javaMailSender;
    @Value("${app.monitor.notify.email.from}")
    private  String from;
    @Override
    public void sendSimpleMail(String to, String subject, String content) {
        String[] recipients = to.split(";");
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(recipients);
        message.setSubject(subject);
        message.setText(content);
        javaMailSender.send(message);
    }
}
