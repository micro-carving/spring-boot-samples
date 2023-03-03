package com.olinonee.springboot.core.task.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import org.springframework.util.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 任务调度器扩展 demo 业务类
 *
 * @author olinH, olinone666@gmail.com
 * @version v1.0.0
 * @since 2023-03-02
 */
@Service
public class TaskScheduleExtendedDemoService {

    private static final Logger logger = LoggerFactory.getLogger(TaskScheduleExtendedDemoService.class);

    private final TaskScheduler threadPoolTaskScheduler;

    private final Map<Long, ScheduledFutureFactory> scheduledFutureFactoryMap = new ConcurrentHashMap<>();
    private final IdGenerator idGenerator = new SimpleIdGenerator();
    private static final String DEFAULT_CRON_EXPRESSION = "0/5 * * * * ? ";

    @Autowired
    private TaskScheduleExtendedDemoService(TaskScheduler threadPoolTaskScheduler) {
        this.threadPoolTaskScheduler = threadPoolTaskScheduler;
    }

    /**
     * 启动定时任务，默认按照每 5 秒执行一次
     *
     * @return 返回执行结果（true-表示成功 false-表示失败）
     */
    public Boolean start() {
        return this.start(DEFAULT_CRON_EXPRESSION);
    }

    /**
     * 启动定时任务，自动生成默认的定时任务 id 并按照自定义的 cron 表达式执行定时任务
     *
     * @param cronExpression 自定义的 cron 表达式
     * @return 返回执行结果（true-表示成功 false-表示失败）
     */
    public Boolean start(String cronExpression) {
        if (!StringUtils.hasText(cronExpression)) {
            logger.error("[TaskScheduleExtendedDemoService#start] - cron 表达式为空而启动失败");
            return false;
        }

        try {
            final long taskId = this.generateTaskId();
            final ScheduledFuture<?> schedule = threadPoolTaskScheduler.schedule(() -> logger.info("id 为 {} 的定时任务，按照 cron 为 [{}] 的规则正在执行！", taskId, cronExpression), new CronTrigger(cronExpression));
            scheduledFutureFactoryMap.putIfAbsent(taskId, new ScheduledFutureFactory(schedule, taskId, cronExpression));
            return true;
        } catch (Exception e) {
            logger.error("[TaskScheduleExtendedDemoService#start] - 定时任务启动发生异常，异常信息为：", e);
            return false;
        }
    }

    /**
     * 启动指定 id 的定时任务，同时按照自定义的 cron 表达式执行定时任务
     *
     * @param taskId         任务 id
     * @param cronExpression 自定义的 cron 表达式
     * @return 返回执行结果（true-表示成功 false-表示失败）
     */
    public Boolean start(Long taskId, String cronExpression) {
        if (ObjectUtils.isEmpty(taskId) || !StringUtils.hasText(cronExpression)) {
            logger.error("[TaskScheduleExtendedDemoService#start] - 因为定时任务 id 或者 cron 表达式为空而启动失败");
            return false;
        }

        try {
            final ScheduledFuture<?> schedule = threadPoolTaskScheduler.schedule(() -> logger.info("id 为 {} 的定时任务，按照 cron 为 [{}] 的规则正在执行！", taskId, cronExpression), new CronTrigger(cronExpression));
            scheduledFutureFactoryMap.putIfAbsent(taskId, new ScheduledFutureFactory(schedule, taskId, cronExpression));
            return true;
        } catch (Exception e) {
            logger.error("[TaskScheduleExtendedDemoService#start] - 定时任务启动发生异常，异常信息为：", e);
            return false;
        }
    }

    /**
     * 查询所有定时任务，格式：任务名称 -> 任务
     *
     * @return 所有定时任务列表
     */
    public List<String> queryAll() {
        List<String> taskList = new ArrayList<>();
        scheduledFutureFactoryMap.forEach((k, v) -> taskList.add("taskId -> " + k + ", cronExpression -> [" + v.getCronExpression() +"]"));
        return taskList;
    }

    /**
     * 停止一个指定 id 的定时任务
     *
     * @param taskId 任务 id
     * @return 该 id 的定时任务停止情况（为空-表示不存在此任务；false-表示停止失败；true-表示停止成功）
     */
    public Boolean stop(Long taskId) {
        if (ObjectUtils.isEmpty(taskId) || CollectionUtils.isEmpty(scheduledFutureFactoryMap)) {
            logger.error("[TaskScheduleExtendedDemoService#stop] - 因为定时任务 id 为空而停止失败");
            return null;
        }
        if (!scheduledFutureFactoryMap.containsKey(taskId)) {
            return null;
        }
        final ScheduledFuture<?> scheduledFuture = scheduledFutureFactoryMap.get(taskId).getScheduledFuture();
        if (ObjectUtils.isEmpty(scheduledFuture)) {
            return false;
        }
        // 根据取消任务的状态来决定是否删除集合的任务
        if (scheduledFuture.cancel(true)) {
            return scheduledFutureFactoryMap.remove(taskId, scheduledFutureFactoryMap.get(taskId));
        } else {
            return false;
        }
    }

    /**
     * 重启指定 id 的定时任务
     *
     * @param taskId 定时任务 id
     * @return 返回执行结果（true-表示成功 false-表示失败）
     */
    public Boolean restart(Long taskId) {
        AtomicReference<String> restartCronExpression = new AtomicReference<>(DEFAULT_CRON_EXPRESSION);
        scheduledFutureFactoryMap.forEach((k, v) -> {
            if (taskId.equals(v.getTaskId())) {
                restartCronExpression.set(v.getCronExpression());
            }
        });
        return this.restart(taskId, restartCronExpression.get());
    }

    /**
     * 指定 id 的定时任务，同时按照自定义的 cron 表达式执行定时任务
     *
     * @param taskId         任务 id
     * @param cronExpression 自定义的 cron 表达式
     * @return 返回执行结果（true-表示成功 false-表示失败）
     */
    public Boolean restart(Long taskId, String cronExpression) {
        if (ObjectUtils.isEmpty(taskId) || !StringUtils.hasText(cronExpression)) {
            logger.error("[TaskScheduleExtendedDemoService#restart] - 因为定时任务 id 或者 cron 表达式为空而重启失败");
            return false;
        }

        final Boolean stopState = this.stop(taskId);
        if (ObjectUtils.isEmpty(stopState)) {
            logger.warn("[TaskScheduleExtendedDemoService#restart] - 不存 id 为 [{}] 的定时任务，请尝试启动一个新的定时任务！", taskId);
            return false;
        }
        if (!stopState) {
            logger.warn("[TaskScheduleExtendedDemoService#restart] - id 为 [{}]id 的定时任务停止失败！", taskId);
            return false;
        }

        final Boolean startState = this.start(taskId, cronExpression);
        if (startState) {
            logger.info("[TaskScheduleExtendedDemoService#restart] - id 为 [{}] 的定时任务重启成功！", taskId);
        } else {
            logger.info("[TaskScheduleExtendedDemoService#restart] - id 为 [{}] 的定时任务重启失败！", taskId);
        }
        return startState;
    }

    /**
     * 生成定时任务 id
     *
     * @return 定时任务 id
     */
    public Long generateTaskId() {
        return idGenerator.generateId().getLeastSignificantBits();
    }

    private static class ScheduledFutureFactory {
        private final ScheduledFuture<?> scheduledFuture;
        private final Long taskId;
        private final String cronExpression;

        private ScheduledFutureFactory(ScheduledFuture<?> scheduledFuture, Long taskId, String cronExpression) {
            this.scheduledFuture = scheduledFuture;
            this.taskId = taskId;
            this.cronExpression = cronExpression;
        }

        public ScheduledFuture<?> getScheduledFuture() {
            return scheduledFuture;
        }

        public Long getTaskId() {
            return taskId;
        }

        public String getCronExpression() {
            return cronExpression;
        }
    }
}
