package maratmingazovr.leetcode.neural_network;

import lombok.NonNull;
import lombok.val;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.DoubleUnaryOperator;
import java.util.stream.Collectors;

public class Util {

    @NonNull
    public static double dotProduct(@NonNull List<Double> xs,
                                    @NonNull List<Double> ys) {
        double sum = 0.0;
        for (int i = 0; i < xs.size(); i++) {
            sum += xs.get(i) * ys.get(i);
        }
        return sum;
    }

    @NonNull
    public static DoubleUnaryOperator getActivationFunction(@NonNull ActivationFunction activationFunction) {
        switch (activationFunction) {
            case SIGMOID: return Util::sigmoid;
            default: throw new IllegalArgumentException();
        }
    }

    @NonNull
    public static DoubleUnaryOperator getDerivativeActivationFunction(@NonNull ActivationFunction activationFunction) {
        switch (activationFunction) {
            case SIGMOID: return Util::sigmoidDerivative;
            default: throw new IllegalArgumentException();
        }
    }

    // the classic sigmoid activation function
    public static double sigmoid(double x) {
        return 1.0 / (1.0 + Math.exp(-x));
    }

    public static double sigmoidDerivative(double x) {
        double sig = sigmoid(x);
        return sig * (1.0 - sig);
    }

    // Assume all rows are of equal length
    // and feature scale each column to be in the range 0 - 1
    public static void normalizeByFeatureScaling(List<List<Double>> dataset) {
        for (int colNum = 0; colNum < dataset.get(0).size(); colNum++) {
            List<Double> column = new ArrayList<>();
            for (List<Double> row : dataset) {
                column.add(row.get(colNum));
            }
            double maximum = Collections.max(column);
            double minimum = Collections.min(column);
            double difference = maximum - minimum;
            for (List<Double> row : dataset) {
                row.set(colNum, (row.get(colNum) - minimum) / difference);
            }
        }
    }

    // Load a CSV file into a List of String arrays
    public static List<List<String>> loadCSV(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            return  br.lines()
                      .map(line -> line.split(","))
                      .map(Arrays::asList)
                      .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Find the maximum in an array of doubles
    @NonNull
    public static Integer getMaxValueIndex(@NonNull List<Double> numbers) {
        val maxValue = numbers.stream()
                              .mapToDouble(n -> n)
                              .max().orElse(Double.MIN_VALUE);
        return numbers.indexOf(maxValue);
    }

}
