package com.xi;

import com.xi.service.ProdService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ECommerceApplicationTests {

    @Resource
    private ProdService prodService;

    @Test
    void contextLoads() {

    }

}
