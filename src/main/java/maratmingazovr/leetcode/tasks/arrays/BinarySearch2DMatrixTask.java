package maratmingazovr.leetcode.tasks.arrays;

public class BinarySearch2DMatrixTask {

    public boolean searchMatrix(int[][] matrix, int target) {

        if (matrix.length < 1) {
            return false;
        }

        int rows = matrix.length;
        int columns = matrix[0].length;

        int leftPoint = 0;
        int rightPoint = matrix.length * matrix[0].length-1;
        int middlePoint = rightPoint/2;

        while (leftPoint <= rightPoint) {
            int x = middlePoint / columns;
            int y = middlePoint % columns;
            int actual = matrix[x][y];
            if (actual == target) {
                return true;
            } else if (actual > target) {
                rightPoint = middlePoint - 1;
            } else {
                leftPoint = middlePoint + 1;
            }
            middlePoint = leftPoint + (rightPoint-leftPoint)/2;
        }
        return false;
    }
}
