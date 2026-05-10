package org.master.diploma.backend.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public abstract class FileService {
    public abstract String uploadFile(String bucketName, String objectName, InputStream inputStream, long size, String contentType) throws IOException;
    public abstract InputStream downloadFile(String bucketName, String objectName) throws IOException;
    public abstract void deleteFile(String bucketName, String objectName) throws IOException;
    public abstract void deleteByFullRepoPath(String repoPath) throws IOException;
    public abstract File downloadRepository(String repoPath) throws IOException;
}
