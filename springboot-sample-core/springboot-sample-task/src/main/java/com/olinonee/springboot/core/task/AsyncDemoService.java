package com.olinonee.springboot.core.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 异步 demo 服务
 *
 * @author olinH, olinone666@gmail.com
 * @version v1.0.0
 * @since 2023-02-25
 */
@Service
public class AsyncDemoService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Async
    public void sayHello() {
        logger.info("[AsyncDemoService#sayHello] - 当前线程名称为 [{}] " +
                        "异步执行了 sayHello 方法",
                Thread.currentThread().getName());
    }

    @Async(value = "getCustomizerAsyncExecutor")
    public void sayHi() {
        logger.info("[AsyncDemoService#sayHi] - 当前线程名称为 [{}] " +
                        "异步执行了 sayHi 方法",
                Thread.currentThread().getName());
        throw new RuntimeException("手动抛出一个异常");
    }
}
