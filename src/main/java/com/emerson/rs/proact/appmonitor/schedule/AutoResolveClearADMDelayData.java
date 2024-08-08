package com.emerson.rs.proact.appmonitor.schedule;

import com.emerson.rs.proact.appmonitor.config.SnowFlakeConnection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;
import java.sql.Statement;

@Component
@Slf4j
@RestController
@RequestMapping("/operation")
public class AutoResolveClearADMDelayData {

  @Value("${adm.splitQueue2.name:adm.splitQueue2}")
  private String splitQueue2Name;
  @Value("${adm.splitQueue2.purge.threshold:2000}")
  private Integer purgeThreshold = 2000;

  @Value("${query.input.table}")
  private String queryTable;

  @Value("${adm.clear.snowflake.flag:true}")
  private Boolean clearSnowflakeFlag;

  @Autowired
  private AmqpAdmin amqpAdmin;

  @Autowired
  private SnowFlakeConnection snowFlakeConnection;


  @Scheduled(initialDelay = 10000, fixedDelayString = "${adm.checkSplitQueue2.frequency:300000}")
  @GetMapping("/clearSnowflake")
  public void checkSplitQueue2() {
    //check if the queue number
    Integer count = (Integer) amqpAdmin.getQueueProperties(splitQueue2Name).get("QUEUE_MESSAGE_COUNT");
    log.info("check split queue2 count number:{}",count);
    //connect snowflake
    if (count > purgeThreshold && clearSnowflakeFlag) {
      log.info("The queue adm.splitQueue2 count is {}, need purge the queue and clear Snowflake.", count);
      //purge this queue
      amqpAdmin.purgeQueue(splitQueue2Name);
      //clear Snowflake
      clearSnowflake();
    }
  }

  private void clearSnowflake() {
    //clear Snowflake data
    try {
      Connection connection = snowFlakeConnection.getConnection();
      Statement statement = connection.createStatement();
      String sql = "UPDATE " + queryTable + " SET PROCESSED='YES' WHERE DELAYED_FLAG ='FALSE'";

      log.info("SQL :" + sql);
      int r = statement.executeUpdate(sql);
      log.info("Clear Snowflake data successfully. Update count:{}", r);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
