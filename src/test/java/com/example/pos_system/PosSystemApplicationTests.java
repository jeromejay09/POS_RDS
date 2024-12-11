package com.example.pos_system;

import com.example.pos.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class PosSystemApplicationTests {

	@Autowired
	private ProductService productService;

	@Test
	void contextLoads() {
		// Test if the application context loads and the beans are available
		assert (productService != null);
	}
}
