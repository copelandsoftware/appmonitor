package com.emerson.rs.proact.appmonitor.sendgrid;

import com.sendgrid.SendGrid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SendGridConfig {
    @Value("${sendgrid.auth.key}")
    private String sendGridAuthKey;

    @Bean
    public SendGrid sendGrid() {
        return new SendGrid(sendGridAuthKey);
    }
}
