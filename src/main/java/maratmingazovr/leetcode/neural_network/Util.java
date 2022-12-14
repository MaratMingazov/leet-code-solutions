package maratmingazovr.leetcode.neural_network;

import java.util.Arrays;

public class Util {

    public static double dotProduct(double[] xs, double[] ys) {
        double sum = 0.0;
        for (int i = 0; i < xs.length; i++) {
            sum += xs[i] * ys[i];
        }
        return sum;
    }

    // the classic sigmoid activation function
    public static double sigmoid(double x) {
        return 1.0 / (1.0 + Math.exp(-x));
    }

    public static double sigmoidDerivative(double x) {
        double sig = sigmoid(x);
        return sig * (1.0 - sig);
    }

    // Find the maximum in an array of doubles
    public static double max(double[] numbers) {
        return Arrays.stream(numbers)
                     .max()
                     .orElse(Double.MIN_VALUE);
    }

}
