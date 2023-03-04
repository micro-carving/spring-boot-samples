package com.olinonee.springboot.core.testing.test;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * RandomPortWebTestClient 测试类
 *
 * @author olinH, olinone666@gmail.com
 * @version v1.0.0
 * @since 2023-03-04
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MyRandomPortWebTestClientTests {

    @Test
    void exampleTest(@Autowired WebTestClient webTestClient) {
        webTestClient
                .get().uri("/")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Hello World");
    }

    @Test
    void exampleTest(@Autowired TestRestTemplate restTemplate) {
        String body = restTemplate.getForObject("/", String.class);
        Assertions.assertThat(body).isEqualTo("Hello World");
    }
}
