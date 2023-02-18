package com.olinonee.springboot.core.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * 用于获取 ApplicationArguments 的 bean
 *
 * @author olinH, olinone666@gmail.com
 * @version v1.0.0
 * @since 2023-02-18
 */
@Component
public class MyBean {

    private final static Logger logger = LoggerFactory.getLogger(MyBean.class);

    @Autowired
    private ApplicationArguments arguments;

    /**
     * 端口号：方式一（注解方式，已被遗弃，底层其实使用方式二这种方式）
     */
    @LocalServerPort
    private int port1;

    /**
     * 端口号：方式二（使用@Value方式）
     */
    @Value("${local.server.port}")
    private int port2;

    /**
     * 端口号：方式三（使用Environment方式）
     */
    @Autowired
    private Environment environment;

    public void printArgs() {
        final int nonOptionArgsNum = arguments.getNonOptionArgs().size();
        final int optionNamesNum = arguments.getOptionNames().size();
        final int sourceArgsNum = arguments.getSourceArgs().length;
        logger.info("非选项参数数量为 {} 个", nonOptionArgsNum);
        logger.info("选项参数数量为 {} 个", optionNamesNum);
        logger.info("原参数数量为 {} 个", sourceArgsNum);

        if (nonOptionArgsNum > 0) {
            arguments.getNonOptionArgs().forEach(nonOptionArgs -> {
                logger.info("非可选项为 {}", nonOptionArgs);
            });
        }
        if (optionNamesNum > 0) {
            arguments.getOptionNames().forEach(optionName -> {
                logger.info("可选项名称为 {}，选项值为 {}", optionName, arguments.getOptionValues(optionName));
            });
            final boolean isExistAA = arguments.containsOption("aa");
            logger.info("是否包含 “aa” 可选项？ {}", isExistAA);
        }
        if (sourceArgsNum > 0) {
            for (String sourceArg : arguments.getSourceArgs()) {
                logger.info("原参数为 {}", sourceArg);
            }
        }
    }

    /**
     * 获取端口
     */
    public void getPort() {
        logger.info("方式一（注解方式，已被遗弃），port -> {}", port1);
        logger.info("方式二（使用@Value方式），port -> {}", port2);
        logger.info("方式三（使用Environment方式），port -> {}", environment.getProperty("local.server.port"));
    }
}
