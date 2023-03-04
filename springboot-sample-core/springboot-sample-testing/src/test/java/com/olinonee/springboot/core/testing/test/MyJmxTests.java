package com.olinonee.springboot.core.testing.test;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import javax.management.MBeanServer;

/**
 * Jmx 测试类
 *
 * @author olinH, olinone666@gmail.com
 * @version v1.0.0
 * @since 2023-03-04
 */
@SpringBootTest(properties = "spring.jmx.enabled=true")
@DirtiesContext
public class MyJmxTests {

    @Autowired
    private MBeanServer mBeanServer;

    @Test
    void exampleTest() {
        Assertions.assertThat(this.mBeanServer.getDomains()).contains("java.lang");
        // ...
    }
}
