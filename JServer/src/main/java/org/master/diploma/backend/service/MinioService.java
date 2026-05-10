package org.master.diploma.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class MinioService extends FileService {
    private final ZipProcessingService zipProcessingService;

    @Override
    public String uploadFile(String bucketName, String objectName, InputStream inputStream, long size, String contentType) throws IOException {
        // TODO: Real MinIO implementation (e.g., minioClient.putObject(...))
        return "minio://" + bucketName + "/" + objectName;
    }

    @Override
    public InputStream downloadFile(String bucketName, String objectName) throws IOException {
        // TODO: Real MinIO implementation (e.g., minioClient.getObject(...))
        return new ByteArrayInputStream(new byte[0]); // Stub
    }

    @Override
    public void deleteFile(String bucketName, String objectName) throws IOException {
        // TODO: Real MinIO implementation (e.g., minioClient.removeObject(...))
        System.out.println("Deleting file from MinIO: " + bucketName + "/" + objectName);
    }

    @Override
    public File downloadRepository(String repoPath) throws IOException {
        Path tempDir = Files.createTempDirectory("jserver_repo_");
        
        // Example repoPath parsing: minio://bucket/object
        String pathWithoutProtocol = repoPath.replace("minio://", "");
        String bucketName = pathWithoutProtocol.substring(0, pathWithoutProtocol.indexOf("/"));
        String objectName = pathWithoutProtocol.substring(pathWithoutProtocol.indexOf("/") + 1);

        try (InputStream is = downloadFile(bucketName, objectName)) {
            zipProcessingService.unzip(is, tempDir);
        }
        
        return tempDir.toFile();
    }
    
    public void deleteByFullRepoPath(String repoPath) throws IOException {
        if (repoPath == null || !repoPath.startsWith("minio://")) return;
        String pathWithoutProtocol = repoPath.replace("minio://", "");
        String bucketName = pathWithoutProtocol.substring(0, pathWithoutProtocol.indexOf("/"));
        String objectName = pathWithoutProtocol.substring(pathWithoutProtocol.indexOf("/") + 1);
        deleteFile(bucketName, objectName);
    }
}
