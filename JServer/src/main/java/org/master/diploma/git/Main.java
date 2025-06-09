package org.master.diploma.git;

import org.apache.logging.log4j.core.util.IOUtils;
import org.master.diploma.git.git.GitHelper;
import org.master.diploma.git.git.model.Commit;
import org.master.diploma.git.support.PermutationHelper;

import java.util.*;

public class Main {

    public static void main(String[] args) {


//        String path = "E:\\univer\\5_course\\diploma\\CAP\\soft\\master-dis\\JServer\\src\\test\\resources\\repositories\\test-1";
//        var gitGraph = GitHelper.createCommitGraph(path);
//        gitGraph.getVertices().forEach(
//                vertex -> {
//                    ((Commit) vertex).getDiffs().forEach(
//                            System.out::println
//                    );
//                }
//        );
//
//        gitGraph.getVertices().forEach(
//                vertex -> {
//                    ((Commit) vertex).getDiffEntries().forEach(
//                            diffEntry -> {
//                             System.out.println("old" + diffEntry.getOldPath());
//                             System.out.println("new" + diffEntry.getNewPath());
//                            }
//                    );
//                }
//        );
//
//        System.out.println("Ffff");

        while (true) {
            Scanner scanner = new Scanner(System.in);
            int n = scanner.nextInt();
            int k = scanner.nextInt();

         //   System.out.println(PermutationHelper.generatePermutations(n, k));
        }
    }
}