package com.olinonee.springboot.core.testing.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * 模拟测试类
 *
 * @author olinH, olinone666@gmail.com
 * @version v1.0.0
 * @since 2023-03-04
 */
// @SpringBootTest
@WebMvcTest
@AutoConfigureMockMvc
public class MyMockMvcTests {

    @Test
    void testWithMockMvc(@Autowired MockMvc mockMvc) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("HelloWorld"));
    }

    @Test
    void testWithWebTestClient(@Autowired WebTestClient webClient) {
        webClient
                .get()
                .uri("/")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class)
                .isEqualTo("HelloWorld");
    }
}
