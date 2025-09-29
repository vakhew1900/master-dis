package org.master.diploma.git.graph;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@Setter
public class Branch<T extends Vertex> {

    private UUID uuid;
    private List<T> vertices;

    public Set<Integer> getVertexNumbers() {
       return vertices
                .stream()
                .map(Vertex::getNumber)
                .collect(Collectors.toSet());
    }
}
