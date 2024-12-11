package com.example.pos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@Service
public class PosService {

    private final S3Service s3Service;

    @Autowired
    public PosService(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    public String uploadProductImage(MultipartFile file, String bucketName, String key) throws IOException {
        // Call the uploadImage method of S3Service with correct arguments
        return s3Service.uploadImage(key, file.getBytes());
    }
}
