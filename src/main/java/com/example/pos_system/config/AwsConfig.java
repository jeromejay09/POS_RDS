package com.example.pos_system.config;  // Use your desired package structure

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class AwsConfig {

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.AP_SOUTHEAST_1)  // You can update this to the appropriate region
                .credentialsProvider(DefaultCredentialsProvider.create())  // Uses the default AWS credentials provider chain
                .build();
    }
}
