package maratmingazovr.leetcode.neural_network;

import javafx.scene.paint.Color;
import javafx.util.Pair;
import lombok.NonNull;
import lombok.val;
import org.apache.commons.math3.linear.RealMatrix;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.DoubleUnaryOperator;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class Util {

    public static double dotProduct(@NonNull List<Double> xs,
                                    @NonNull List<Double> ys) {
        double sum = 0.0;
        for (int i = 0; i < xs.size(); i++) {
            sum += xs.get(i) * ys.get(i);
        }
        return sum;
    }


    public static double getMedian(double[] values) {
        double[] array = Arrays.copyOf(values, values.length);
        Arrays.sort(array);
        int size = array.length;
        if (size % 2 == 0) {
            return (array[size/2] + array[size/2-1])/2;
        } else {
            return array[size/2];
        }
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
    public static UnaryOperator<RealMatrix> getActivationFunctionMatrix(@NonNull ActivationFunction activationFunction) {
        switch (activationFunction) {
            case SIGMOID: return Util::sigmoidMatrix;
            case RELU: return Util::reluMatrix;
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

    public static RealMatrix sigmoidMatrix(RealMatrix matrix) {
        RealMatrix result = matrix.copy();
        for (int row = 0; row < matrix.getRowDimension(); row++) {
            for (int column = 0; column < matrix.getColumnDimension(); column++) {
                double value = matrix.getEntry(row, column);
                result.setEntry(row, column, 1 / (1 + Math.exp(-1 * value)));
            }
        }
        return result;
    }

    public static double sigmoidDerivative(double x) {
        double sig = sigmoid(x);
        return sig * (1.0 - sig);
    }

    public static double relu(double x) {
        return Math.max(0.1, x);
    }

    public static RealMatrix reluMatrix(RealMatrix matrix) {
        RealMatrix result = matrix.copy();
        for (int row = 0; row < matrix.getRowDimension(); row++) {
            for (int column = 0; column < matrix.getColumnDimension(); column++) {
                double value = matrix.getEntry(row, column);
                result.setEntry(row, column, Math.max(0.1, value));
            }
        }
        return result;
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
            bw.write(configuration.getLearningRateAsString());
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
        val learningRate = Double.parseDouble(data.get(1).get(0));
        val activationFunctions = data.get(2)
                                  .stream()
                                  .map(ActivationFunction::valueOf)
                                  .collect(Collectors.toList());
        List<List<Double>> layersWeights = new ArrayList<>();
        for (List<String> weights : data.subList(3, data.size())) {
            layersWeights.add(weights.stream().map(Double::parseDouble).collect(Collectors.toList()));
        }
        return new NetworkConfiguration(layersStructure, learningRate, activationFunctions, layersWeights);
    }

    // Find the maximum in an array of doubles
    @NonNull
    public static Integer getMaxValueIndex(@NonNull List<Double> numbers) {
        val maxValue = numbers.stream()
                              .mapToDouble(n -> n)
                              .max().orElse(Double.MIN_VALUE);
        return numbers.indexOf(maxValue);
    }

    public static int getMaxValueIndex(@NonNull double[] numbers) {
        Double maxValue = Arrays.stream(numbers).max().orElse(Double.MIN_VALUE);
        for (int i = 0; i < numbers.length; i++) {
            if (maxValue.equals(numbers[i])) {
                return i;
            }
        }
        return -1;
    }


    @NonNull
    public static List<Double> getSoftMax(@NonNull List<Double> input) {
        val total = input.stream().map(Math::exp).reduce(Double::sum).orElse(0.0);
        return input.stream()
                    .map(value -> Math.exp(value) / total)
                    .collect(Collectors.toList());
    }

    @NonNull
    public static List<Double> getProbabilityDistribution(@NonNull List<Integer> input) {
        val inputDouble = input.stream().map(value -> (double) value).collect(Collectors.toList());
        val total = inputDouble.stream().reduce(Double::sum).orElse(0.0);
        return inputDouble.stream()
                    .map(value -> value / total)
                    .collect(Collectors.toList());
    }

    @NonNull
    public static Integer RGBtoInt(@NonNull Integer r,
                                   @NonNull Integer g,
                                   @NonNull Integer b) {
        return b * 65536 + g * 256 + r;
    }

    @NonNull
    public static Color IntToColor(@NonNull Integer colorValue) {
        int b = colorValue / 65536;
        int g = (colorValue - b * 65536) / 256;
        int r = colorValue - b * 65536 - g * 256;
        return Color.color(((double)r)/255,((double)g)/255,((double)b)/255);
    }

    @NonNull
    public static Integer colorToInt(@NonNull Color color) {
        val r = (int)Math.round(color.getRed() * 255.0);
        val g = (int)Math.round(color.getGreen() * 255.0);
        val b = (int)Math.round(color.getBlue() * 255.0);
        return Util.RGBtoInt(r,g,b);
    }

    @NonNull
    public static Pair<Integer, Double> getProbabilityValue(@NonNull List<Integer> values,
                                           @NonNull List<Double> probabilities) {
        val random = new Random();
        double pick = random.nextDouble();
        for (int i = 0; i < probabilities.size(); i++) {
            pick -= probabilities.get(i);
            if (pick <= 0) { // we had one that took us over, leads to a pick
                return new Pair<>(values.get(i),probabilities.get(i));
            }
        }
        return new Pair<>(values.get(0),probabilities.get(0));
    }

    public static void randomize(double[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for(int j = 0; j < matrix[i].length; j++) {
                matrix[i][j] = Math.random()/10;
            }
        }
    }

    public static void randomize(double[] vector) {
        for (int i = 0; i < vector.length; i++) {
            vector[i] = Math.random();
        }
    }

}
