package org.git_tutor.git_logic.exception;

public class IncorrectCommitGraphVertexTypeException extends IllegalArgumentException {

    public IncorrectCommitGraphVertexTypeException(){
        super("Incorrect type of Vertex: Vertex should be Commit.class");
    }
}
