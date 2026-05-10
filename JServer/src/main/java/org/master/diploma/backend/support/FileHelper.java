package org.master.diploma.backend.support;

public class FileHelper {
    public static String createLabFileName(Long labId, Integer taskNumber) {
        return String.format("lab_%d_task_%d_%d.zip", labId, taskNumber, System.currentTimeMillis());
    }

    public static String createSubmissionFileName(String username, Long taskId) {
        return String.format("%s/%d_%d.zip", username, taskId, System.currentTimeMillis());
    }

    public static void deleteRecursive(java.io.File file) {
        if (file.isDirectory()) {
            java.io.File[] children = file.listFiles();
            if (children != null) {
                for (java.io.File child : children) {
                    deleteRecursive(child);
                }
            }
        }
        file.delete();
    }
}
