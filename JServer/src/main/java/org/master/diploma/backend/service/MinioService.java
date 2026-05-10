package org.master.diploma.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;

@Service
public class MinioService {
    public String uploadFile(String bucketName, String objectName, InputStream inputStream, long size, String contentType) {
        // TODO: Real MinIO implementation
        return "minio://" + bucketName + "/" + objectName;
    }

    public InputStream downloadFile(String bucketName, String objectName) {
        // TODO: Real MinIO implementation
        return null;
    }
}
