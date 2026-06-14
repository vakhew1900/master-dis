package org.git_tutor.server.service;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.springframework.stereotype.Service;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

@Service
public class ZipProcessingService {
    public static final long MAX_ZIP_SIZE = 10 * 1024 * 1024; // 10 MB
    public static final long MAX_UNZIPPED_SIZE = 100 * 1024 * 1024; // 100 MB

    public Path unzip(InputStream zipInputStream, Path targetDir) throws IOException {
        try (ZipArchiveInputStream zis = new ZipArchiveInputStream(zipInputStream, StandardCharsets.UTF_8.name())) {
            ZipArchiveEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.isDirectory()) continue;

                // Flatten structure: resolve entry name relative to targetDir
                Path entryPath = Paths.get(entry.getName());
                // Remove root folder if present to flatten structure
                Path relativePath = entryPath.getNameCount() > 1 ? entryPath.subpath(1, entryPath.getNameCount()) : entryPath;
                Path newPath = targetDir.resolve(relativePath).normalize();

                if (!newPath.startsWith(targetDir)) {
                    throw new IOException("Zip Slip vulnerability: " + entry.getName());
                }

                if (newPath.getParent() != null) {
                    Files.createDirectories(newPath.getParent());
                }

                Files.copy(zis, newPath, StandardCopyOption.REPLACE_EXISTING);
            }
        }
        return targetDir;
    }
}
