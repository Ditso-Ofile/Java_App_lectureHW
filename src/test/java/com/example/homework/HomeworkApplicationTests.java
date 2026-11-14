package com.example.homework;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/** Checks that the whole Spring context starts (no network call happens at start-up). */
// in-memory database for the test, so it never touches (or locks) C:\data\townsdb
@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:testdb")
class HomeworkApplicationTests {

    @Test
    void contextLoads() {
    }
}
