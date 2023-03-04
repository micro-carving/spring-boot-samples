package com.olinonee.springboot.core.testing.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 测试类
 *
 * @author olinH, olinone666@gmail.com
 * @version v1.0.0
 * @since 2023-03-04
 */
@SpringBootTest(args = "--app.test=one")
// @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, args = "--app.test=one")
public class MyApplicationTests {

    @Test
    public void testHelloWorld() {
        System.out.println("hello, world!");
    }

    @Test
    void applicationArgumentsPopulated(@Autowired ApplicationArguments args) {
        assertThat(args.getOptionNames()).containsOnly("app.test");
        assertThat(args.getOptionValues("app.test")).containsOnly("one");
    }
}
