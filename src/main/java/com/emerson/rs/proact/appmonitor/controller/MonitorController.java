package com.emerson.rs.proact.appmonitor.controller;

import com.emerson.rs.proact.appmonitor.alert.ComponentLastNotifyTimeStore;
import com.emerson.rs.proact.appmonitor.domain.AllSchedulerJobsBean;
import com.emerson.rs.proact.appmonitor.repository.AllSchedulerJobsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/monitor")
public class MonitorController {
  @Autowired
  private AllSchedulerJobsRepository allSchedulerJobsRepository;

  @Autowired
  private ComponentLastNotifyTimeStore componentLastNotifyTimeStore;

  @Value("${cdm.job.notification.time}")
  private long notificationTime;

  @Value("${app.monitor.repeat.notify.interval:30}")
  private int repeatInterval;

  @Value("${app.monitor.env:dev}")
  private String env;

  @GetMapping("/cdmAutoProcess")
  String monitorCdmAutoProcess() {
    log.info("Start to monitor SF_AUTOPROCESS_PROGRAM job:");
    String jobName = "SF_AUTOPROCESS_NON_ADM_CUST";
    List<AllSchedulerJobsBean> autoProcessJob = allSchedulerJobsRepository.findJobByName(jobName);
    if (autoProcessJob.size() > 0) {
      if (isNotification(jobName, autoProcessJob.get(0).getLastStartDate())) {
        return "Fail";
      }
    }
    return "OK";
  }

  @GetMapping("/cdmAutoProcessADM")
  String monitorCdmAutoProcessADM() {
    log.info("Start to monitor SF_AUTOPROCESS_ADM_ALARMS_JOB job:");
    String jobName = "SF_AUTOPROCESS_ADM_CUST";
    List<AllSchedulerJobsBean> autoProcessJob = allSchedulerJobsRepository.findJobByName(jobName);
    if (autoProcessJob.size() > 0) {
      if (isNotification(jobName, autoProcessJob.get(0).getLastStartDate())) {
        return "Fail";
      }
    }
    return "OK";
  }

  @GetMapping("/cdmAutoChannelling")
  String monitorCdmChannelling() {
    log.info("Start to monitor channelling:");
    String jobName = "MSS_AUTO1";
    List<AllSchedulerJobsBean> autoProcessJob = allSchedulerJobsRepository.findJobByName(jobName);
    if (autoProcessJob.size() > 0) {
      if (isNotification(jobName, autoProcessJob.get(0).getLastStartDate())) {
        return "Fail";
      }
    }
    return "OK";
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
