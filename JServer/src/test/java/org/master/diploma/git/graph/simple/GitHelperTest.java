package org.master.diploma.git.graph.simple;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.master.diploma.git.git.GitHelper;
import org.master.diploma.git.git.model.CommitGraph;

import java.io.File;

public class GitHelperTest {

    //------------------------------ createCommitGraph ------------------------------------
    private static final String REPOSITORIES_DIRECTORY ="src/test/resources/repositories/";

    @Test
    public void createCommitGraph() {
        String repositoryPath = "test-1";
        String path = getFullFilePath(repositoryPath);
        CommitGraph commitGraph = GitHelper.createCommitGraph(path);
        int commitCount = 15;
        Assertions.assertEquals(commitCount, commitGraph.getVertices().size());

    }

    private String getFullFilePath(String resourceRepositoriesFile) {
        return REPOSITORIES_DIRECTORY + resourceRepositoriesFile;
    }
}
