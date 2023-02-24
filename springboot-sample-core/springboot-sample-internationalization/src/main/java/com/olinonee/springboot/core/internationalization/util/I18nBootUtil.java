package com.olinonee.springboot.core.internationalization.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * I18n boot 工具类（在工具类中使用 Spring 获取自动注入的 bean 或者属性）
 *
 * @author olinH, olinone666@gmail.com
 * @version v1.0.0
 * @since 2023-02-24
 */
@Component
public class I18nBootUtil {
    private static final Logger logger = LoggerFactory.getLogger(I18nBootUtil.class);

    private static MessageSource messageSource;

    // 注入 MessageSource 属性，方便上下文获取
    private I18nBootUtil(MessageSource messageSource) {
        I18nBootUtil.messageSource = messageSource;
    }

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

        String message = "";
        try {
            Locale locale = LocaleContextHolder.getLocale();
            message = messageSource.getMessage(translateMsg, null, locale);
        } catch (Exception e) {
            logger.error("[I18nBootUtil#get] - parse message error! ", e);
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
        String message = "";
        try {
            Locale locale = LocaleContextHolder.getLocale();
            message = messageSource.getMessage(translateMsg, params, locale);
        } catch (Exception e) {
            logger.error("[I18nBootUtil#get] - parse message error! ", e);
        }
        return message;
    }
}
