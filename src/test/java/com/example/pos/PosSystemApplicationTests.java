package com.example.pos;

import com.example.pos.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class PosSystemApplicationTests {

    @Autowired
    private ProductService productService;

    @Test
    void contextLoads() {
        assertNotNull(productService, "ProductService bean should be loaded in the context");
    }
}
