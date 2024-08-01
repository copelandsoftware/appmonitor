package com.emerson.rs.proact.appmonitor.sendgrid;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class SendGridHelper {
    @Autowired
    private SendGrid sendGrid;

    public void sample() throws IOException {
        Email from = new Email("garys.sun@emerson.com");
        String subject = "Test SendGrid from java";
        Email to = new Email("garys.sun@emerson.com");
        Content content = new Content("text/plain", "test SendGrid from application monitor app.");
        Mail mail = new Mail(from, subject, to, content);
        send(mail);
    }

    public void sendTextEmailWithSingleRecipient(String from, String to, String subject, String content) throws IOException {
        Email fromE = new Email(from);
        Email toE = new Email(to);
        Content contentE = new Content("text/plain", content);
        Mail mail = new Mail(fromE, subject, toE, contentE);
        send(mail);
    }

    public void sendEmailWithMultipleRecipients(String from, String tos, String subject, String content) throws IOException {
        final Mail mail = new Mail();
        mail.setFrom(new Email(from, "Sender User"));
        mail.setSubject(subject);
        String[] recipients = tos.split(";");
        for (int i = 0; i < recipients.length; i++) {
            final Personalization personalization = new Personalization();
            personalization.addTo(new Email(recipients[i], "Recipient" + i));
            mail.addPersonalization(personalization);
        }
        mail.addContent(new Content("text/html", content));
        send(mail);
    }

    private void send(final Mail mail) throws IOException {
        final Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());

        final Response response = sendGrid.api(request);
        System.out.println(response.getStatusCode());
        System.out.println(response.getBody());
        System.out.println(response.getHeaders());
    }
}
