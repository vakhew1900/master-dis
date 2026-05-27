package org.master.diploma.backend.service;

import io.minio.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class MinioService extends FileService {
    private final MinioClient minioClient;
    private final ZipProcessingService zipProcessingService;

    @Override
    public String uploadFile(String bucketName, String objectName, InputStream inputStream, long size, String contentType) throws IOException {
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(inputStream, size, -1)
                            .contentType(contentType)
                            .build());
            
            return "minio://" + bucketName + "/" + objectName;
        } catch (Exception e) {
            throw new IOException("Failed to upload file to MinIO", e);
        }
    }

    @Override
    public InputStream downloadFile(String bucketName, String objectName) throws IOException {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build());
        } catch (Exception e) {
            throw new IOException("Failed to download file from MinIO", e);
        }
    }

    @Override
    public void deleteFile(String bucketName, String objectName) throws IOException {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build());
        } catch (Exception e) {
            throw new IOException("Failed to delete file from MinIO", e);
        }
    }

    @Override
    public void deleteByFullRepoPath(String repoPath) throws IOException {
        if (repoPath == null || !repoPath.startsWith("minio://")) return;
        String pathWithoutProtocol = repoPath.replace("minio://", "");
        int firstSlash = pathWithoutProtocol.indexOf("/");
        if (firstSlash == -1) return;
        
        String bucketName = pathWithoutProtocol.substring(0, firstSlash);
        String objectName = pathWithoutProtocol.substring(firstSlash + 1);
        deleteFile(bucketName, objectName);
    }

    @Override
    public File downloadRepository(String repoPath) throws IOException {
        Path tempDir = Files.createTempDirectory("jserver_repo_");
        
        String pathWithoutProtocol = repoPath.replace("minio://", "");
        int firstSlash = pathWithoutProtocol.indexOf("/");
        String bucketName = pathWithoutProtocol.substring(0, firstSlash);
        String objectName = pathWithoutProtocol.substring(firstSlash + 1);

        try (InputStream is = downloadFile(bucketName, objectName)) {
            zipProcessingService.unzip(is, tempDir);
        }
        
        return tempDir.toFile();
    }
}
