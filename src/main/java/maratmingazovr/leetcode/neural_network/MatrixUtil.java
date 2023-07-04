package maratmingazovr.leetcode.neural_network;

import java.util.List;

public class MatrixUtil {

    public static double[][] convertToMatrix(List<List<Double>> elements) {
        double[][] matrix = new double[elements.size()][elements.get(0).size()];
        for (int row = 0; row < elements.size(); row++) {
            for (int col = 0; col < elements.get(0).size(); col++) {
                matrix[row][col] = elements.get(row).get(col);
            }
        }
        return matrix;
    }
}
