package com.emerson.rs.proact.appmonitor.schedule;

import com.emerson.rs.proact.appmonitor.alert.AppKeepLiveStore;
import com.emerson.rs.proact.appmonitor.bean.AppKeepLiveMessage;
import com.emerson.rs.proact.appmonitor.mail.EmersonSMTPService;
import com.emerson.rs.proact.appmonitor.sendgrid.SendGridHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
@Slf4j
public class MonitorAppKeepLiveScheduledJob {

    @Autowired
    private SendGridHelper sendGridHelper;
    @Autowired
    private EmersonSMTPService smtpService;

    @Autowired
    private AppKeepLiveStore appKeepLiveStore;

    @Value("${app.monitor.notify.email.subject:App error alert}")
    private String subject;

    @Value("${app.monitor.notify.email.from}")
    private String from;

    @Value("${app.monitor.notify.email.to}")
    private String to;


    @Value("${app.live.default.time:10}")
    private int liveDefaultTime;

    //@Scheduled(initialDelay = 10000, fixedDelayString = "30000")
    public void monitorAppLive() {
        //Check work order integration app live
        AppKeepLiveMessage appKeepLiveMessage = appKeepLiveStore.appLiveMessageMap.get(AppKeepLiveStore.WORK_ORDER_INTEGRATION_APP);
        if (appKeepLiveMessage != null) {
            if (((System.currentTimeMillis() - appKeepLiveMessage.getAppLiveMsgCreatedTime().getTime()) / (60 * 1000)) > liveDefaultTime) {
                notifyAlert(appKeepLiveMessage);
            }
        }
    }

    public void notifyAlert(AppKeepLiveMessage appKeepLiveMessageObj) {
        String content = appKeepLiveMessageObj.getAppName()
                + "---" + appKeepLiveMessageObj.getComponentName()
                + " doesn't work more than " + liveDefaultTime + "minutes, please check as soon as possible.";
        try {
            sendGridHelper.sendEmailWithMultipleRecipients(from, to, subject, content);
        } catch (IOException e) {
            log.error("SendGrid request failed, try to use internal SMTP server!", e);
            try {
                smtpService.sendSimpleMail(to, subject, content);
            } catch (Exception ex) {
                log.error("Send email failed by internal SMTP, please contact IT and fix it!", ex);
            }
        }
    }
}
