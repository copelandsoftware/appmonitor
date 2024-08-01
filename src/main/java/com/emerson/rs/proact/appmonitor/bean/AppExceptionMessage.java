package com.emerson.rs.proact.appmonitor.bean;

import lombok.Data;

import java.util.Date;

@Data
public class AppExceptionMessage {
    private String appName;
    private String className;
    private String severity;
    private String errorMessage;
    private Date errorCreatedTime;
}
