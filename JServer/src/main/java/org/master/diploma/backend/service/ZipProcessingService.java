package org.master.diploma.backend.service;

import org.springframework.stereotype.Service;
import java.io.*;
import java.nio.file.*;
import java.util.zip.*;

@Service
public class ZipProcessingService {
    public static final long MAX_ZIP_SIZE = 10 * 1024 * 1024; // 10 MB
    public static final long MAX_UNZIPPED_SIZE = 100 * 1024 * 1024; // 100 MB
    public static final double MAX_COMPRESSION_RATIO = 100.0; // Protection against zip bombs

    public Path unzip(InputStream zipInputStream, Path targetDir) throws IOException {
        long totalSize = 0;
        try (ZipInputStream zis = new ZipInputStream(zipInputStream)) {
            ZipEntry entry = zis.getNextEntry();
            while (entry != null) {
                Path newPath = zipSlipProtect(entry, targetDir);
                if (entry.isDirectory()) {
                    Files.createDirectories(newPath);
                } else {
                    if (newPath.getParent() != null) {
                        if (Files.notExists(newPath.getParent())) {
                            Files.createDirectories(newPath.getParent());
                        }
                    }
                    try (OutputStream os = Files.newOutputStream(newPath)) {
                        byte[] buffer = new byte[8192];
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            totalSize += len;
                            if (totalSize > MAX_UNZIPPED_SIZE) {
                                throw new IOException("Unzipped size exceeds limit: " + MAX_UNZIPPED_SIZE);
                            }
                            os.write(buffer, 0, len);
                        }
                    }
                }
                entry = zis.getNextEntry();
            }
            zis.closeEntry();
        }
        return targetDir;
    }

    private Path zipSlipProtect(ZipEntry entry, Path targetDir) throws IOException {
        Path targetDirResolved = targetDir.toAbsolutePath().normalize();
        Path entryPath = Paths.get(entry.getName());
        Path resolvedPath = targetDirResolved.resolve(entryPath).normalize();

        if (!resolvedPath.startsWith(targetDirResolved)) {
            throw new IOException("Entry is outside of the target dir: " + entry.getName());
        }

        return resolvedPath;
    }
}
