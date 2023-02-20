package com.olinonee.springboot.core.externalconfig;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * 启动器
 *
 * @author olinH, olinone666@gmail.com
 * @version v1.0.0
 * @since 2023-02-20
 */
@SpringBootApplication
public class MyApplication {


    public static void main(String[] args) {
        final SpringApplication springApplication = new SpringApplication(MyApplication.class);
        // 禁用命令行参数
        // springApplication.setAddCommandLineProperties(false);
        final ConfigurableApplicationContext context = springApplication.run(args);
        final MyBean bean = context.getBean(MyBean.class);
        System.out.println("name：" + bean.getName());
    }
}
