package com.emerson.rs.proact.appmonitor.alert;

import com.emerson.rs.proact.appmonitor.bean.AppExceptionMessage;
import com.emerson.rs.proact.appmonitor.mail.EmersonSMTPService;
import com.emerson.rs.proact.appmonitor.sendgrid.SendGridHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;

@Service
@Slf4j
public class WorkOrderIntegrationExceptionAlertImpl implements ExceptionConsumeAndAlert {

    @Autowired
    private SendGridHelper sendGridHelper;
    @Autowired
    private EmersonSMTPService smtpService;

    @Value("${app.monitor.notify.email.subject:App error alert}")
    private String subject;

    @Value("${app.monitor.notify.email.from}")
    private String from;

    @Value("${app.monitor.notify.email.to}")
    private String to;

    @Autowired
    protected ObjectMapper mapper;

    @Override
//    @RabbitListener(queues = {"${app.exception.queue:appExceptionQueue}"})
//    @RabbitHandler
    public void exceptionMessageConsumer(String errorMessage) {
        log.info(errorMessage);
        AppExceptionMessage appExceptionMessage = convertJsonToObj(errorMessage);
        //filter some exception, not send email
        //TODO
        //snooze some time if one type error send many times
        //TODO
        notifyAlert(appExceptionMessage);
    }

    @Override
    public void notifyAlert(AppExceptionMessage exceptionBean) {
        String content = "<Strong>App name:</Strong>" + exceptionBean.getAppName()
                + "<br>"
                + "<Strong>Caller Class Name:</Strong>" + exceptionBean.getClassName()
                + "<br>"
                + "<Strong>Error message:</Strong>" + exceptionBean.getErrorMessage()
                + "<br>"
                + "More information, please check the log[" + exceptionBean.getAppName() + ".log]!";
        try {
            sendGridHelper.sendEmailWithMultipleRecipients(from, to, subject,content);

        } catch (IOException e) {
            log.error("SendGrid request failed, try to use internal SMTP server!", e);
            try {
                smtpService.sendSimpleMail(to, subject, content);
            } catch (Exception ex) {
                log.error("Send email failed by internal SMTP, please contact IT and fix it!", ex);
            }
        }
    }

    private AppExceptionMessage convertJsonToObj(String errorMessage) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        mapper.setDateFormat(sdf);
        try {
            return mapper.readValue(errorMessage, AppExceptionMessage.class);
        } catch (IOException e) {
            log.error(e.getMessage());
            return null;
        }
    }
}
