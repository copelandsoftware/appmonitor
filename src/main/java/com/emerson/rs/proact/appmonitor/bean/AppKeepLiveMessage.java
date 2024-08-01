package com.emerson.rs.proact.appmonitor.bean;

import lombok.Data;

import java.util.Date;

@Data
public class AppKeepLiveMessage {
    private String appName;
    private String componentName;
    private Date appLiveMsgCreatedTime;
}
