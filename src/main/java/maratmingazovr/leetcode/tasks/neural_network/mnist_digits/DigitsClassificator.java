package maratmingazovr.leetcode.tasks.neural_network.mnist_digits;

import lombok.NonNull;
import lombok.val;
import maratmingazovr.leetcode.neural_network.Util;
import maratmingazovr.leetcode.tasks.neural_network.AbstractClassificator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DigitsClassificator extends AbstractClassificator {

    public DigitsClassificator() {
        super(
                "src/main/java/maratmingazovr/leetcode/tasks/neural_network/mnist_digits/data/mnist_train_100.csv",
                "src/main/java/maratmingazovr/leetcode/tasks/neural_network/mnist_digits/data/mnist_test_10.csv",
                "src/main/java/maratmingazovr/leetcode/tasks/neural_network/mnist_digits/data/configuration.txt");
    }

    @Override
    public void createDefaultNetworkAndTrain() {
//        network = new Network(List.of(784, 100, 10), 0.3, List.of(ActivationFunction.SIGMOID, ActivationFunction.SIGMOID));
//        loadNetwork();
//        loadData();
//        train(100L);
//        saveNetworkConfiguration();
//        validate();
    }

    @NonNull
    @Override
    public Boolean isExpectedEqualToOutput(@NonNull List<Double> expected,
                                           @NonNull List<Double> output) {

        val expectedMaxValueIndex = Util.getMaxValueIndex(expected);
        val outputMaxValueIndex = Util.getMaxValueIndex(output);
        return expectedMaxValueIndex.equals(outputMaxValueIndex);
    }

    @Override
    public void loadData(@NonNull String datasetFile,
                         @NonNull List<List<Double>> inputs,
                         @NonNull List<List<Double>> expects) {

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
                    expects.add(new ArrayList<>(Arrays.asList(0.99, 0.01, 0.01, 0.01, 0.01, 0.01, 0.01, 0.01, 0.01, 0.01)));
                    break;
                case "1":
                    expects.add(new ArrayList<>(Arrays.asList(0.01, 0.99, 0.01, 0.01, 0.01, 0.01, 0.01, 0.01, 0.01, 0.01)));
                    break;
                case "2":
                    expects.add(new ArrayList<>(Arrays.asList(0.01, 0.01, 0.99, 0.01, 0.01, 0.01, 0.01, 0.01, 0.01, 0.01)));
                    break;
                case "3":
                    expects.add(new ArrayList<>(Arrays.asList(0.01, 0.01, 0.01, 0.99, 0.01, 0.01, 0.01, 0.01, 0.01, 0.01)));
                    break;
                case "4":
                    expects.add(new ArrayList<>(Arrays.asList(0.01, 0.01, 0.01, 0.01, 0.99, 0.01, 0.01, 0.01, 0.01, 0.01)));
                    break;
                case "5":
                    expects.add(new ArrayList<>(Arrays.asList(0.01, 0.01, 0.01, 0.01, 0.01, 0.99, 0.01, 0.01, 0.01, 0.01)));
                    break;
                case "6":
                    expects.add(new ArrayList<>(Arrays.asList(0.01, 0.01, 0.01, 0.01, 0.01, 0.01, 0.99, 0.01, 0.01, 0.01)));
                    break;
                case "7":
                    expects.add(new ArrayList<>(Arrays.asList(0.01, 0.01, 0.01, 0.01, 0.01, 0.01, 0.01, 0.99, 0.01, 0.01)));
                    break;
                case "8":
                    expects.add(new ArrayList<>(Arrays.asList(0.01, 0.01, 0.01, 0.01, 0.01, 0.01, 0.01, 0.01, 0.99, 0.01)));
                    break;
                case "9":
                    expects.add(new ArrayList<>(Arrays.asList(0.01, 0.01, 0.01, 0.01, 0.01, 0.01, 0.01, 0.01, 0.01, 0.99)));
                    break;
                default:
                    throw new IllegalArgumentException("Invalid type");
            }
        }
//        Util.normalizeByFeatureScaling(inputs);
    }
}
