package com.emerson.rs.proact.appmonitor.mail;

public interface EmailService {
    void sendSimpleMail(String to, String subject, String content);
}
