package maratmingazovr.leetcode.neural_network;

import lombok.NonNull;
import lombok.val;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
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
            case RELU: return Util::relu;
            default: throw new IllegalArgumentException();
        }
    }

    @NonNull
    public static DoubleUnaryOperator getDerivativeActivationFunction(@NonNull ActivationFunction activationFunction) {
        switch (activationFunction) {
            case SIGMOID: return Util::sigmoidDerivative;
            case RELU: return Util::reluDerivative;
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

    public static double relu(double x) {
        return Math.max(0.1, x);
    }

    public static double reluDerivative(double x) {
        return x > 0 ? 1 : 0.1;
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
        return loadCSV(filename, ",");
    }

    @NonNull
    public static List<List<String>> loadCSV(@NonNull String filename, @NonNull String spliterator) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            return  br.lines()
                      .map(line -> line.split(spliterator))
                      .map(Arrays::asList)
                      .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveNetworkConfiguration(@NonNull String filename,
                                                @NonNull NetworkConfiguration configuration) {
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            bw.write(configuration.getLayersStructureAsString());
            bw.newLine();
            bw.write(configuration.getActivationFunctionsAsString());
            for (String weight : configuration.getLayersWeightsAsString()) {
                bw.newLine();
                bw.write(weight);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static NetworkConfiguration loadNetworkConfiguration(@NonNull String filename) {
        val data = loadCSV(filename, ",");
        val layersStructure = data.get(0)
                                  .stream()
                                  .map(Integer::parseInt)
                                  .collect(Collectors.toList());
        val activationFunctions = data.get(1)
                                  .stream()
                                  .map(ActivationFunction::valueOf)
                                  .collect(Collectors.toList());
        List<List<Double>> layersWeights = new ArrayList<>();
        for (List<String> weights : data.subList(2, data.size())) {
            layersWeights.add(weights.stream().map(Double::parseDouble).collect(Collectors.toList()));
        }
        return new NetworkConfiguration(layersStructure, activationFunctions, layersWeights);
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
