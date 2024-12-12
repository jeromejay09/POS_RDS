package com.example.pos.controller;

import com.example.pos.service.ProductService;
import com.example.pos.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.AmazonS3Exception;




import java.util.List;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;

    // Mapping to show a list of products
    @GetMapping("/products")
    public String listProducts(Model model) {
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        return "productList";  // Thymeleaf template for displaying the list of products
    }

    // Mapping to show the product creation form
    @GetMapping("/products/create")
    public String showCreateProductForm(Model model) {
        model.addAttribute("product", new Product());
        return "createProduct";  // Thymeleaf template for the create product form
    }

    // Mapping to save the product
    @PostMapping("/products/save")
    public String saveProduct(@ModelAttribute Product product,
                              @RequestParam("image") MultipartFile image) {
        try {
            // Upload the image to S3 and get the image URL
            String imageUrl = productService.uploadImageToS3(image);
            product.setImageUrl(imageUrl); // Set the image URL in the Product

            // Save the product with the image URL
            productService.saveProduct(product);
        } catch (Exception e) {
            // Handle exception (e.g., file upload error)
            e.printStackTrace();
            return "error";  // Return an error page or message
        }

        return "redirect:/products";  // Redirect to the product listing after saving
    }
}
