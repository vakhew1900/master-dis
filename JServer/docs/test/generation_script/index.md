# Script Generation Workflow

- The user specifies the number of vertices and the number of modifications to be applied to the graphs.
-  A graph with the specified number of vertices is generated.
- A copy of this graph is created.
- A mapping entity is established between the two graphs, associating each vertex in one graph with a corresponding vertex in the other.
- The specified number of modifications (vertex addition/removal, label addition/removal) is applied to the second graph. After each modification, the vertex-mapping entity is updated accordingly.

The classes responsible for file generation are located at: `/src/main/java/org/master/diploma/git/generator`.