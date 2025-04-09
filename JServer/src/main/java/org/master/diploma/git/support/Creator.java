package org.master.diploma.git.support;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class Creator {


    public static <T> List<List<T>> createMatrix(int row, int col, Supplier<T> supplier) {
        List<List<T>> matrix = new ArrayList<>();

        for(int i = 0; i < row; i++){
            List<T> rowList = new ArrayList<>();

            for (int j = 0; j < col; j++){
                rowList.add(supplier.get());
            }
            matrix.add(rowList);
        }

        return  matrix;
    }
}
