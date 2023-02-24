package com.olinonee.springboot.core.internationalization.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

/**
 * 国际化工具类
 *
 * @author olinH, olinone666@gmail.com
 * @version v1.0.0
 * @since 2023-02-24
 */
public class I18nUtil {
    private static final Logger logger = LoggerFactory.getLogger(I18nUtil.class);

    /**
     * 获取单个翻译值，固定参数格式
     * <p>
     * 例如：
     * <pre>
     *     operation.success=操作成功!
     *     String msg =getMessage("operation.success");
     * </pre>
     *
     * @param translateMsg 翻译的信息
     * @return 翻译之后的字符串
     */
    public static String get(String translateMsg) {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setCacheSeconds(-1);
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
        // 设置文件基础扫描路径名称
        messageSource.setBasenames("i18n/messages");

        String message = "";
        try {
            Locale locale = LocaleContextHolder.getLocale();
            message = messageSource.getMessage(translateMsg, null, locale);
        } catch (Exception e) {
            logger.error("[I18nUtil#get] - parse message error! ", e);
        }
        return message;
    }

    /**
     * 获取单个翻译值，动态参数格式
     * <p>
     * 例如：
     * <pre>
     *     start.ge.end = 开始日期{0}必须小于结束日期{1}！
     *     String [] param = {startDate, endDate};
     *     String msg =getMessage("start.ge.end", param);
     * </pre>
     *
     * @param translateMsg 翻译的信息，可能携带参数
     * @param params       填充的参数数组
     * @return 翻译之后的字符串
     */
    public static String get(String translateMsg, Object[] params) {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setCacheSeconds(-1);
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
        // 设置文件基础扫描路径名称
        messageSource.setBasenames("i18n/messages");

        String message = "";
        try {
            Locale locale = LocaleContextHolder.getLocale();
            message = messageSource.getMessage(translateMsg, params, locale);
        } catch (Exception e) {
            logger.error("[I18nUtil#get] - parse message error! ", e);
        }
        return message;
    }
}
