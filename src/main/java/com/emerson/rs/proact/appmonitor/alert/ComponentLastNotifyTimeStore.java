package com.emerson.rs.proact.appmonitor.alert;

import com.emerson.rs.proact.appmonitor.bean.AppKeepLiveMessage;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ComponentLastNotifyTimeStore {
    public Map<String, Date> componentLastNotifyTimeMap = new ConcurrentHashMap();
}
