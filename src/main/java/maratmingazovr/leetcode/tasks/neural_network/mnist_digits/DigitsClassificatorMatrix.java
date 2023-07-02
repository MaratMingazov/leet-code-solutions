package maratmingazovr.leetcode.tasks.neural_network.mnist_digits;

import lombok.NonNull;
import lombok.val;
import maratmingazovr.leetcode.neural_network.ActivationFunction;
import maratmingazovr.leetcode.neural_network.Util;
import maratmingazovr.leetcode.neural_network_matrix.NeuralNetworkMatrix;
import maratmingazovr.leetcode.tasks.neural_network.AbstractClassificatorMatrix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DigitsClassificatorMatrix extends AbstractClassificatorMatrix {

    public static void main(String[] args) {
        DigitsClassificatorMatrix classificator = new DigitsClassificatorMatrix();
        classificator.createDefaultNetworkAndTrain();
    }

    public DigitsClassificatorMatrix() {
        super(
                "src/main/java/maratmingazovr/leetcode/tasks/neural_network/mnist_digits/data/mnist_train_100.csv",
                "src/main/java/maratmingazovr/leetcode/tasks/neural_network/mnist_digits/data/mnist_test_10.csv",
                "src/main/java/maratmingazovr/leetcode/tasks/neural_network/mnist_digits/data/configuration_matrix.txt");
    }
    @Override
    public void createDefaultNetworkAndTrain() {
        network = new NeuralNetworkMatrix(784, 100, 10, 0.3, ActivationFunction.SIGMOID, ActivationFunction.SIGMOID);
        //loadNetwork();
        loadData();
        train(100);
        //saveNetworkConfiguration();
        validate();

    }

    @Override
    public @NonNull Boolean isExpectedEqualToOutput(double[] target,
                                                    double[] actual) {
        val targetMaxValueIndex = Util.getMaxValueIndex(target);
        val actualMaxValueIndex = Util.getMaxValueIndex(actual);
        return targetMaxValueIndex == actualMaxValueIndex;
    }

    @Override
    public void loadData(String datasetFile,
                         List<List<Double>> inputs,
                         List<List<Double>> targets) {

        val dataset = Util.loadCSV(datasetFile);
        //        Collections.shuffle(dataset);

        for (List<String> data : dataset) {
            List<Double> input = data.stream()
                                     .skip(1)
                                     .map(Double::parseDouble)
                                     .map(v -> (v/255)*0.99 + 0.01)
                                     .collect(Collectors.toList());
            inputs.add(input);

            val type = data.get(0);
            switch (type) {
                case "0":
                    targets.add(new ArrayList<>(Arrays.asList(0.99, 0.01, 0.01, 0.01, 0.01, 0.01, 0.01, 0.01, 0.01, 0.01)));
                    break;
                case "1":
                    targets.add(new ArrayList<>(Arrays.asList(0.01, 0.99, 0.01, 0.01, 0.01, 0.01, 0.01, 0.01, 0.01, 0.01)));
                    break;
                case "2":
                    targets.add(new ArrayList<>(Arrays.asList(0.01, 0.01, 0.99, 0.01, 0.01, 0.01, 0.01, 0.01, 0.01, 0.01)));
                    break;
                case "3":
                    targets.add(new ArrayList<>(Arrays.asList(0.01, 0.01, 0.01, 0.99, 0.01, 0.01, 0.01, 0.01, 0.01, 0.01)));
                    break;
                case "4":
                    targets.add(new ArrayList<>(Arrays.asList(0.01, 0.01, 0.01, 0.01, 0.99, 0.01, 0.01, 0.01, 0.01, 0.01)));
                    break;
                case "5":
                    targets.add(new ArrayList<>(Arrays.asList(0.01, 0.01, 0.01, 0.01, 0.01, 0.99, 0.01, 0.01, 0.01, 0.01)));
                    break;
                case "6":
                    targets.add(new ArrayList<>(Arrays.asList(0.01, 0.01, 0.01, 0.01, 0.01, 0.01, 0.99, 0.01, 0.01, 0.01)));
                    break;
                case "7":
                    targets.add(new ArrayList<>(Arrays.asList(0.01, 0.01, 0.01, 0.01, 0.01, 0.01, 0.01, 0.99, 0.01, 0.01)));
                    break;
                case "8":
                    targets.add(new ArrayList<>(Arrays.asList(0.01, 0.01, 0.01, 0.01, 0.01, 0.01, 0.01, 0.01, 0.99, 0.01)));
                    break;
                case "9":
                    targets.add(new ArrayList<>(Arrays.asList(0.01, 0.01, 0.01, 0.01, 0.01, 0.01, 0.01, 0.01, 0.01, 0.99)));
                    break;
                default:
                    throw new IllegalArgumentException("Invalid type");
            }
        }
        //        Util.normalizeByFeatureScaling(inputs);

    }
}
