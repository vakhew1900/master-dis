package org.master.diploma.git;

import org.master.diploma.git.git.GitHelper;

public class Main {

    public static void main(String[] args) {

        String path = "E:\\univer\\5_course\\diploma\\CAP\\soft\\master-dis\\JServer\\src\\test\\resources\\repositories\\test-1";
        var commits = GitHelper.getAllCommits(path);
        System.out.println(commits.size());


        commits.forEach(
                commit ->   { System.out.println(commit.getShortMessage()); GitHelper.printDiff(commit, path); System.out.println("-----------------------------------------------------------"); }
        );
    }
}
