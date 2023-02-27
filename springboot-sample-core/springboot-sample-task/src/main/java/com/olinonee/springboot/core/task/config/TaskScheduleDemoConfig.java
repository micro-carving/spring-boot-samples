package com.olinonee.springboot.core.task.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.config.TriggerTask;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.util.ErrorHandler;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 任务调度器 demo 配置类
 *
 * @author olinH, olinone666@gmail.com
 * @version v1.0.0
 * @since 2023-02-27
 */
@Configuration
@EnableScheduling
public class TaskScheduleDemoConfig implements SchedulingConfigurer {

    private final Logger logger = LoggerFactory.getLogger(TaskScheduleDemoConfig.class);

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setTaskScheduler(getCustomizerAsyncScheduler());

        taskRegistrar.addCronTask(new CronTask(() -> {
            logger.info("[TaskScheduleDemoConfig#configureTasks$addCronTask] - 每 3 秒输出一次，当前时间为 {}", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        }, "0/3 * * * * ? "));

        taskRegistrar.addTriggerTask(new TriggerTask(() -> {
            logger.info("[TaskScheduleDemoConfig#configureTasks$addTriggerTask] - 每 6 秒输出一次，当前时间为 {}", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        }, triggerContext -> new CronTrigger("0/6 * * * * ? ").nextExecutionTime(triggerContext)));
    }

    /**
     * 获取自定义异步调度器
     *
     * @return TaskScheduler
     */
    private TaskScheduler getCustomizerAsyncScheduler() {
        final ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(2);
        threadPoolTaskScheduler.setErrorHandler(getErrorHandler());
        threadPoolTaskScheduler.initialize();
        return threadPoolTaskScheduler;
    }

    /**
     * 获取错误处理器
     *
     * @return ErrorHandler
     */
    private ErrorHandler getErrorHandler() {
        return t -> {
            logger.warn("[TaskScheduleDemoConfig#getErrorHandler] - 执行异常");
            logger.error("异常信息为：{} ", t.getMessage());
        };
    }
}
