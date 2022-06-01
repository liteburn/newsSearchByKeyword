package com.scrap;

import com.scrap.constants.TimeConstants;
import com.scrap.scrap.CensorScrap;
import com.scrap.scrap.PravdaScrap;
import com.scrap.scrap.TSNScrap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import static com.scrap.constants.TimeConstants.TIMER;

@Configuration
public class TimerTasksConfiguration {

    @Autowired KafkaTemplate<String, String> template;
    @Bean
    public void runAllNews() {
        List<TimerTask> timerTasks =
                List.of(new CensorScrap(template), new TSNScrap(template), new PravdaScrap(template));
        for (TimerTask timerTask : timerTasks) {
            new Thread(() -> start(timerTask)).start();
        }
    }

    private void start(TimerTask timerTask) {
        Date executionDate = new Date();
        TIMER.scheduleAtFixedRate(timerTask, executionDate, TimeConstants.DELAY_IN_MS);
    }

}
