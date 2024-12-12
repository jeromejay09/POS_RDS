package com.example.pos.service;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.example.pos.entity.Product;
import com.example.pos.repository.ProductRepository;  // Import the ProductRepository


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

// ProductService.java

@Service
public class ProductService {

    private final S3Client s3Client;
    private final String bucketName = "pos-system-bucket2"; // Your bucket name
    private final ProductRepository productRepository; // Add the repository to fetch products

    @Autowired
    public ProductService(S3Client s3Client, ProductRepository productRepository) {
        this.s3Client = s3Client;
        this.productRepository = productRepository; // Inject the repository
    }

    // Method to upload image to S3 and return the image URL
    public String uploadImageToS3(MultipartFile file) throws IOException {
        Path tempFile = Files.createTempFile("upload-", file.getOriginalFilename());
        file.transferTo(tempFile.toFile());

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(file.getOriginalFilename())
                    .build();
            s3Client.putObject(putObjectRequest, tempFile);

            return s3Client.utilities().getUrl(builder -> builder.bucket(bucketName).key(file.getOriginalFilename())).toString();
        } catch (S3Exception e) {
            throw new IOException("Error uploading to S3", e);
        } finally {
            Files.delete(tempFile);
        }
    }

    // Implement the getAllProducts method to fetch products from the database
    public List<Product> getAllProducts() {
        return productRepository.findAll(); // Assuming you have a ProductRepository for database access
    }

    // Method to save product (to be implemented)
    public void saveProduct(Product product) {
        // Your code to save the product to the database
    }
}
