package com.emerson.rs.proact.appmonitor;

import com.emerson.rs.proact.appmonitor.sendgrid.SendGridHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class AppmonitorApplicationTests {
    @Autowired
    private SendGridHelper sendGridHelper;

    @Test
    void testSendGrid() throws IOException {
        sendGridHelper.sample();
    }
}
