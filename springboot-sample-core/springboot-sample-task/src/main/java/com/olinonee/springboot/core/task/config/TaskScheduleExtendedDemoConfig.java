package com.olinonee.springboot.core.task.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

/**
 * 任务调度器扩展 demo 配置类
 *
 * @author olinH, olinone666@gmail.com
 * @version v1.0.0
 * @since 2023-03-02
 */
@Configuration
@EnableScheduling
public class TaskScheduleExtendedDemoConfig implements SchedulingConfigurer {

    private final Logger logger = LoggerFactory.getLogger(TaskScheduleExtendedDemoConfig.class);

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setTaskScheduler(threadPoolTaskScheduler());
    }

    @Bean
    public TaskScheduler threadPoolTaskScheduler() {
        final ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        // 定时任务执行线程池核心线程数
        threadPoolTaskScheduler.setPoolSize(5);
        // 如果设置为 true，则目标执行器将切换到取消时删除模式
        threadPoolTaskScheduler.setRemoveOnCancelPolicy(true);
        // 自定义错误处理器
        threadPoolTaskScheduler.setErrorHandler(t -> {
            logger.error("[TaskScheduleExtendedDemoConfig#threadPoolTaskScheduler] - 执行定时任务发生了异常，异常信息为：{} ", t.getMessage());
        });
        // 初始化
        threadPoolTaskScheduler.initialize();
        return threadPoolTaskScheduler;
    }
}
