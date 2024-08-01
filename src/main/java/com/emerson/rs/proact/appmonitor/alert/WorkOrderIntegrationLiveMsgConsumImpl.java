package com.emerson.rs.proact.appmonitor.alert;

import com.emerson.rs.proact.appmonitor.bean.AppKeepLiveMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;

@Service
@Slf4j
public class WorkOrderIntegrationLiveMsgConsumImpl implements AppKeepLiveMessageConsume {


    @Autowired
    protected ObjectMapper mapper;
    @Autowired
    private AppKeepLiveStore appKeepLiveStore;

    @Override
//    @RabbitListener(queues = {"${app.live.message.queue:appLiveMessageQueue}"})
//    @RabbitHandler
    public void liveMessageConsumer(String appLiveMessage) {
        log.info(appLiveMessage);
        AppKeepLiveMessage appKeepLiveMessageObj = convertJsonToObj(appLiveMessage);
        appKeepLiveStore.appLiveMessageMap.put(AppKeepLiveStore.WORK_ORDER_INTEGRATION_APP, appKeepLiveMessageObj);
    }

    private AppKeepLiveMessage convertJsonToObj(String errorMessage) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        mapper.setDateFormat(sdf);
        try {
            return mapper.readValue(errorMessage, AppKeepLiveMessage.class);
        } catch (IOException e) {
            log.error(e.getMessage());
            return null;
        }
    }
}
