package org.master.diploma.git.graph.dto;

import org.master.diploma.git.git.model.Commit;
import org.master.diploma.git.git.model.CommitGraph;
import org.master.diploma.git.label.GitLabel;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Post-processor for Git comparison results.
 * Identifies "movable" nodes that were not matched by the main algorithm
 * but can be matched based on isolated structure and identical content.
 * <p>
 * Постпроцессор для результатов сравнения Git.
 * Идентифицирует "подвижные" (movable) узлы, которые не были сопоставлены основным алгоритмом,
 * но могут быть сопоставлены на основе изолированной структуры и идентичного содержимого.
 */
public class GitComparisonPostProcessor {

    /**
     * Processes the comparison DTO to find and mark MOVABLE nodes.
     *
     * @param dto the DTO to update / DTO для обновления
     * @param first  the first commit graph / первый граф коммитов
     * @param second  the second commit graph / второй граф коммитов
     */
    public void postProcess(GitComparisonResultDto dto, CommitGraph first, CommitGraph second) {
        List<NodeDto> firstNodes = dto.getFirstGraph().getNodes();
        List<NodeDto> secondNodes = dto.getSecondGraph().getNodes();
        
        Set<String> matchedInSecond = new HashSet<>(dto.getCompareResult().getMatchedHashes1To2().values());

        for (NodeDto node : firstNodes) {
            // Only consider nodes that are currently EXTRA
            if (!NodeDto.SEVERITY_EXTRA.equals(node.getSeverity())) {
                continue;
            }

            Commit commit = first.getVertex(node.getNumber());

            for (NodeDto otherNode : secondNodes) {
                // Only consider nodes that are currently EXTRA and not yet matched by this post-processor
                if (!NodeDto.SEVERITY_EXTRA.equals(otherNode.getSeverity()) || matchedInSecond.contains(otherNode.getHash())) {
                    continue;
                }

                Commit otherCommit = second.getVertex(otherNode.getNumber());

                if (commit.canRelate(otherCommit)) {
                    node.setSeverity(NodeDto.SEVERITY_MOVABLE);
                    otherNode.setSeverity(NodeDto.SEVERITY_MOVABLE);
                    dto.getCompareResult().getMatchedHashes1To2().put(node.getHash(), otherNode.getHash());
                    matchedInSecond.add(otherNode.getHash());
                    break;
                }
            }
        }
    }
}
