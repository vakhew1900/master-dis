package org.master.diploma.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class MinioService {
    private final ZipProcessingService zipProcessingService;

    public String uploadFile(String bucketName, String objectName, InputStream inputStream, long size, String contentType) {
        // TODO: Real MinIO implementation (e.g., using MinioClient)
        return "minio://" + bucketName + "/" + objectName;
    }

    public InputStream downloadFile(String bucketName, String objectName) {
        // TODO: Real MinIO implementation
        return null;
    }

    /**
     * Downloads a repository (as a ZIP) from MinIO and unzips it to a temporary directory.
     * @param repoPath Path in MinIO (e.g., minio://bucket/object)
     * @return File object pointing to the unzipped repository directory.
     */
    public File downloadRepository(String repoPath) throws IOException {
        // 1. Create a temporary directory
        Path tempDir = Files.createTempDirectory("jserver_repo_");
        
        // 2. Get InputStream from MinIO
        // String objectName = repoPath.substring(repoPath.lastIndexOf("/") + 1);
        // InputStream is = downloadFile("repositories", objectName);
        
        // FOR STUB: Assume we have some way to get the ZIP.
        // In real implementation:
        // zipProcessingService.unzip(is, tempDir);
        
        return tempDir.toFile();
    }
}
