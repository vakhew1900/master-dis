package org.git_tutor.git_logic.graph.simple;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.git_tutor.git_logic.GitHelper;
import org.git_tutor.git_logic.model.CommitGraph;

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
