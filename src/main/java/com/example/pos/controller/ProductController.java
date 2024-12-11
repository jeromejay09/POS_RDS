package com.example.pos.controller;

import org.springframework.stereotype.Controller;
import com.example.pos.service.ProductService;
import com.example.pos.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Controller
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products")
    public String getAllProducts(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "products"; // Thymeleaf template name
    }


    @GetMapping("/products/create")
    public String showProductForm(Model model) {
        model.addAttribute("product", new Product());
        return "productDetail"; // Thymeleaf template name
    }
}
