package org.master.diploma.backend.service;

import org.springframework.stereotype.Service;

@Service
public class ComparisonService {
    public String compareRepositories(String referenceRepoPath, String studentRepoPath) {
        // TODO: Integration with GitGraphComparisonApp
        return "{\"status\": \"success\", \"message\": \"Stub comparison result\"}";
    }
}
