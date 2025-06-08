package org.master.diploma.git.graph;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
@Setter
public class Branch<T extends Vertex> {

    private UUID uuid;
    private List<T> vertices;
}
