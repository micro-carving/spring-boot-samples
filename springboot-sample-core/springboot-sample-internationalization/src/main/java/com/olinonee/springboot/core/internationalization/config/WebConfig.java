package com.olinonee.springboot.core.internationalization.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

/**
 * web 配置
 *
 * @author olinH, olinone666@gmail.com
 * @version v1.0.0
 * @since 2023-02-24
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {


    /**
     * 默认拦截器，其中 lang 表示切换语言的参数名
     *
     * 比如当请求的 url 为：<a href="https://ip:port/?lang=zh_CN">https://ip:port/?lang=zh_CN</a>，表示读取国际化文件 messages_zh_CN.properties。
     *
     * @param registry 拦截器注册
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        final LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName("lang");
        registry.addInterceptor(localeChangeInterceptor);
    }
}
