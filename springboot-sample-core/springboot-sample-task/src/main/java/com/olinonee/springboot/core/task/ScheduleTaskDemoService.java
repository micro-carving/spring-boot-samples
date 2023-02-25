package com.olinonee.springboot.core.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 执行器 demo 业务类
 *
 * @author olinH, olinone666@gmail.com
 * @version v1.0.0
 * @since 2023-02-25
 */
@Service
@EnableScheduling
public class ScheduleTaskDemoService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 从第 0 秒开始，每 5 秒输出一次信息
     */
    @Scheduled(cron = "0/5 * * * * ? ")
    public void execOnce5Second() {
        logger.info("[ScheduleTaskDemoService#execOnce5Second] - 每 5 秒输出一次，当前时间为 {}", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    }

    /**
     * 从第 5 秒开始，每 10 秒输出一次信息
     */
    @Scheduled(cron = "5/10 * * * * ? ")
    public void execOnce10Second() {
        logger.info("[ScheduleTaskDemoService#execOnce10Second] - 每 10 秒输出一次，当前时间为 {}", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    }
}
