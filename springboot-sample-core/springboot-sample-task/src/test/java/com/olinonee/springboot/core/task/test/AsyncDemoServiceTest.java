package com.olinonee.springboot.core.task.test;

import com.olinonee.springboot.core.task.AsyncDemoService;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * TODO
 *
 * @author olinH, olinone666@gmail.com
 * @version v1.0.0
 * @since 2023-02-25
 */
@SpringBootTest
public class AsyncDemoServiceTest {

    private final Logger logger = LoggerFactory.getLogger(AsyncDemoServiceTest.class);

    @Autowired
    AsyncDemoService service;

    @Autowired
    ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Test
    void testSayHello() {
        logger.info("[AsyncDemoServiceTest#testSayHello] - 执行开始！");
        service.sayHello();
        logger.info("[AsyncDemoServiceTest#testSayHello] - 执行结束！");
    }

    @Test
    void testSayHi() {
        logger.info("[AsyncDemoServiceTest#testSayHi] - 执行开始！");
        service.sayHi();
        logger.info("[AsyncDemoServiceTest#testSayHi] - 执行结束！");
    }

    @Test
    void testThreadPoolTaskExecutor() {
        logger.info("[AsyncDemoServiceTest#testThreadPoolTaskExecutor] - 执行开始！");
        // 调用异步 sayHi 方法
        service.sayHi();
        // 直接执行异步
        threadPoolTaskExecutor.submit(() -> {
            logger.info("[AsyncDemoServiceTest#testThreadPoolTaskExecutor] - 当前线程名称为 [{}] 异步执行",
                    Thread.currentThread().getName());
        });
        // 调用异步 sayHello 方法
        service.sayHello();
        logger.info("[AsyncDemoServiceTest#testThreadPoolTaskExecutor] - 执行结束！");
    }
}
