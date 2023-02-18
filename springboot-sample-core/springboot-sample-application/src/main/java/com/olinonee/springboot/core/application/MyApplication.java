package com.olinonee.springboot.core.application;

import org.springframework.boot.Banner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * 启动类
 *
 * @author olinH, olinone666@gmail.com
 * @version v1.0.0
 * @since 2023-02-17
 */
@SpringBootApplication
public class MyApplication {

    @Bean
    public ExitCodeGenerator exitCodeGenerator() {
        return () -> 42;
    }

    public static void main(String[] args) {
        // SpringApplication.run(MyApplication.class, args);

        final SpringApplication application = new SpringApplication(MyApplication.class);
        // 关闭 banner 展示
        application.setBannerMode(Banner.Mode.OFF);
        application.setApplicationStartup(new BufferingApplicationStartup(2048));
        final ConfigurableApplicationContext context = application.run(args);
        final MyBean myBean = context.getBean(MyBean.class);
        myBean.printArgs();
        myBean.getPort();

        // final int exitCode = SpringApplication.exit(context);
        // System.exit(exitCode);
    }
}
