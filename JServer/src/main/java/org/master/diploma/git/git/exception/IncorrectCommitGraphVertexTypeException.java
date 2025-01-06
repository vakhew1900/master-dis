package org.master.diploma.git.git.exception;

public class IncorrectCommitGraphVertexTypeException extends IllegalArgumentException {

    public IncorrectCommitGraphVertexTypeException(){
        super("Incorrect type of Vertex: Vertex should be Commit.class");
    }
}
