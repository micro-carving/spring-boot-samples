package com.olinonee.springboot.core.task.controller;

import com.olinonee.springboot.core.task.service.TaskScheduleExtendedDemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 任务调度器扩展 demo 控制器
 *
 * @author olinH, olinone666@gmail.com
 * @version v1.0.0
 * @since 2023-03-02
 */
@RestController
@RequestMapping("/task")
public class TaskScheduleExtendedDemoController {

    private final TaskScheduleExtendedDemoService taskScheduleExtendedDemoService;

    @Autowired
    private TaskScheduleExtendedDemoController(TaskScheduleExtendedDemoService taskScheduleExtendedDemoService) {
        this.taskScheduleExtendedDemoService = taskScheduleExtendedDemoService;
    }

    @PostMapping("/start")
    public String startTask(@RequestParam(value = "cronExpression", required = false) String cronExpression) {
        Boolean resultState;
        if (StringUtils.hasText(cronExpression)) {
            resultState = taskScheduleExtendedDemoService.start(cronExpression);
        } else {
            resultState = taskScheduleExtendedDemoService.start();
        }
        return resultState ? "定时任务启动成功！" : "定时任务启动失败！";
    }

    @GetMapping("/queryAll")
    public List<String> queryAllTask() {
        return taskScheduleExtendedDemoService.queryAll();
    }

    @PostMapping("/stop")
    public String stopTask(@RequestParam("taskId") Long taskId) {
        final Boolean stopState = taskScheduleExtendedDemoService.stop(taskId);
        if (ObjectUtils.isEmpty(stopState)) {
            return "不存在该 id 的定时任务，请尝试启动一个新的定时任务！";
        }
        return stopState ? "该 id 的定时任务停止成功！" : "该 id 的定时任务停止失败！";
    }

    @PostMapping("/restart")
    public String restartTask(@RequestParam(value = "taskId") Long taskId,
                              @RequestParam(value = "cronExpression", required = false) String cronExpression) {
        Boolean resultState;
        if (StringUtils.hasText(cronExpression)) {
            resultState = taskScheduleExtendedDemoService.restart(taskId, cronExpression);
        } else {
            resultState = taskScheduleExtendedDemoService.restart(taskId);
        }
        return resultState ? "定时任务重启成功！" : "定时任务重启失败！";
    }
}
