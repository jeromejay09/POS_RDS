package com.example.pos.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.core.sync.RequestBody;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class S3ServiceTest {

    @Mock
    private S3Client s3Client;

    @InjectMocks
    private S3Service s3Service;

    @Test
    public void testUploadImage() {
        byte[] fileBytes = "dummy content".getBytes();
        String key = "test-key";

        // Mocking the S3 client's putObject method
        PutObjectResponse mockResponse = PutObjectResponse.builder().build();
        Mockito.when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(mockResponse);

        String result = s3Service.uploadImage(key, fileBytes);

        // Verifying the result
        assertEquals("File uploaded successfully with key: test-key", result);

        // Verify that putObject was called with correct arguments
        Mockito.verify(s3Client, Mockito.times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }
}
