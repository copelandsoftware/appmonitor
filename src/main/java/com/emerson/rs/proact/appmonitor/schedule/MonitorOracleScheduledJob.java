package com.emerson.rs.proact.appmonitor.schedule;

import com.emerson.rs.proact.appmonitor.alert.ComponentLastNotifyTimeStore;
import com.emerson.rs.proact.appmonitor.domain.AllSchedulerJobsBean;
import com.emerson.rs.proact.appmonitor.mail.EmailService;
import com.emerson.rs.proact.appmonitor.repository.AllSchedulerJobsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class MonitorOracleScheduledJob {
    @Autowired
    private AllSchedulerJobsRepository allSchedulerJobsRepository;

    @Autowired
    private ComponentLastNotifyTimeStore componentLastNotifyTimeStore;

    @Autowired
    private EmailService emailService;

    @Value("${app.monitor.notify.email.to}")
    private String to;

    @Value("${cdm.job.notification.time}")
    private long notificationTime;

    @Value("${app.monitor.repeat.notify.interval:30}")
    private int repeatInterval;

    @Value("${app.monitor.env:dev}")
    private String env;

    //@Scheduled(initialDelay = 1000, fixedDelayString = "180000")
    public void monitorAutoProcessedJob() {
        log.info("Start to monitor SF_AUTOPROCESS_PROGRAM job:");
        String jobName = "SF_AUTOPROCESS_NON_ADM_CUST";
        List<AllSchedulerJobsBean> autoProcessJob = allSchedulerJobsRepository.findJobByName(jobName);
        if (autoProcessJob.size() > 0) {
            if (isNotification(jobName, autoProcessJob.get(0).getLastStartDate())) {
                log.info("send email alert to developer:{}", to);
                notificationByEmail(jobName, autoProcessJob.get(0).getLastStartDate());
            }
        }
    }

    //@Scheduled(initialDelay = 1000, fixedDelayString = "180000")
    public void monitorADMAutoProcessedJob() {
        log.info("Start to monitor SF_AUTOPROCESS_ADM_ALARMS_JOB job:");
        String jobName = "SF_AUTOPROCESS_ADM_CUST";
        List<AllSchedulerJobsBean> autoProcessJob = allSchedulerJobsRepository.findJobByName("SF_AUTOPROCESS_ADM_ALARMS_JOB");
        if (autoProcessJob.size() > 0) {
            if (isNotification(jobName, autoProcessJob.get(0).getLastStartDate())) {
                log.info("send email alert to developer:{}", to);
                notificationByEmail(jobName, autoProcessJob.get(0).getLastStartDate());
            }
        }
    }

    public void notificationByEmail(String jobName, Date jobLastStartTime) {
        String emailSubject = "MSSR " + env + " CDM scheduler job hung-" + jobName;
        long duration = (System.currentTimeMillis() - jobLastStartTime.getTime()) / (1000 * 60);
        String emailBody = "The CDM Oracle Job=" + jobName + " has hung there for more than " + duration + " minutes, the last start time is " + jobLastStartTime
                + ". Please check and restart manually.";
        emailService.sendSimpleMail(to, emailSubject, emailBody);
    }

    private Boolean isNotification(String jobName, Date jobLastStartTime) {
        log.info("Last start time:{}", jobLastStartTime);
        long duration = (System.currentTimeMillis() - jobLastStartTime.getTime()) / (1000 * 60);
        log.info("Last run to now:{} minutes", duration);
        Boolean isNotificaiton = (duration > notificationTime);
        Date lastNotfiyDate = this.componentLastNotifyTimeStore.componentLastNotifyTimeMap.get(jobName);
        if (lastNotfiyDate == null && isNotificaiton) {
            componentLastNotifyTimeStore.componentLastNotifyTimeMap.put(jobName, new Date());
        }

        //repeat notification?
        Date nowTime = new Date();
        Boolean repeatFlag = false;
        if (lastNotfiyDate != null) {
            if ((nowTime.getTime() - lastNotfiyDate.getTime()) / (1000 * 60) > repeatInterval && isNotificaiton) {
                repeatFlag = true;
                componentLastNotifyTimeStore.componentLastNotifyTimeMap.put(jobName, new Date());
            }
        } else {
            repeatFlag = true;
        }


        return isNotificaiton && repeatFlag;
    }
}
