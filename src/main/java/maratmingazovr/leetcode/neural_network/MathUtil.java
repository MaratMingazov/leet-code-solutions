package maratmingazovr.leetcode.neural_network;

public class MathUtil {

    public static double round2digits(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    /**
     * [5, 10, 4] -> [7.3433, 9.53453, 5.002, 9.645645]
     *
     */
    public static double[] generateUniformDistribution(int startValue, int finishValue, int count) {
        double[] result = new double[count];
        for (int i = 0; i < count; i++) {
            result[i] = (finishValue - startValue) * Math.random() + startValue;
        }
        return result;
    }

    /**
     * [5, 10, 4] -> [7.34, 9.53, 5.00, 9.64]
     *
     */
    public static double[] generateUniformDistributionRound2Digits(int startValue, int finishValue, int count) {
        double[] result = generateUniformDistribution(startValue, finishValue, count);
        for (int i = 0; i < count; i++) {
            result[i] = Math.round(result[i] * 100.0) / 100.0;
        }
        return result;
    }
}
