package com.emerson.rs.proact.appmonitor.domain;

import lombok.Data;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "all_scheduler_jobs")
@Data
public class AllSchedulerJobsBean {

    @Id
    @Column(name = "JOB_NAME",updatable = false)
    private String jobName;

    @Column(name = "LAST_START_DATE",updatable = false)
    private Date lastStartDate;

}