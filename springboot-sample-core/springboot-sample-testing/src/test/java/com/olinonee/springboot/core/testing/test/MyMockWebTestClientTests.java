package com.olinonee.springboot.core.testing.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * MockWebTestClient 测试类
 *
 * @author olinH, olinone666@gmail.com
 * @version v1.0.0
 * @since 2023-03-04
 */
@SpringBootTest
@AutoConfigureWebTestClient
public class MyMockWebTestClientTests {

    @Test
    void exampleTest(@Autowired WebTestClient webClient) {
        webClient
                .get()
                .uri("/")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class)
                .isEqualTo("Hello World");
    }
}
