package com.olinonee.springboot.core.externalconfig;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * MyBean
 *
 * @author olinH, olinone666@gmail.com
 * @version v1.0.0
 * @since 2023-02-20
 */
@Component
public class MyBean {

    @Value("${name}")
    private String name;

    public String getName() {
        return name;
    }
}
