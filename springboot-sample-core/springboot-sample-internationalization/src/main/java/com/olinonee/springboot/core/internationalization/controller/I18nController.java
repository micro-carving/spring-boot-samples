package com.olinonee.springboot.core.internationalization.controller;

import com.olinonee.springboot.core.internationalization.util.I18nBootUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 国际化控制器
 *
 * @author olinH, olinone666@gmail.com
 * @version v1.0.0
 * @since 2023-02-24
 */
@RestController
@RequestMapping("/i18n")
public class I18nController {

    @RequestMapping("/osm")
    public String getOperationSuccessMessage() {
        // return I18nUtil.get("operation.success");
        return I18nBootUtil.get("operation.success");
    }

    @RequestMapping("/sgm")
    public String getStartGeEndMessage() {
        String [] param = {"20230224", "20230228"};
        // return I18nUtil.get("start.ge.end", param);
        return I18nBootUtil.get("start.ge.end", param);
    }
}
