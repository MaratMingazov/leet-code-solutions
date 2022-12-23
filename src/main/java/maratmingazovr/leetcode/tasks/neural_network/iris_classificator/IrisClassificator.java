package maratmingazovr.leetcode.tasks.neural_network.iris_classificator;

import lombok.NonNull;
import lombok.val;
import maratmingazovr.leetcode.neural_network.ActivationFunction;
import maratmingazovr.leetcode.neural_network.Network;
import maratmingazovr.leetcode.neural_network.Util;
import maratmingazovr.leetcode.tasks.neural_network.AbstractClassificator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class IrisClassificator extends AbstractClassificator {

    public IrisClassificator() {
        super(
                "src/main/java/maratmingazovr/leetcode/tasks/neural_network/iris_classificator/data/iris_train.csv",
                "src/main/java/maratmingazovr/leetcode/tasks/neural_network/iris_classificator/data/iris_test.csv",
              "src/main/java/maratmingazovr/leetcode/tasks/neural_network/iris_classificator/data/configuration.txt");
    }


    @Override
    public void createDefaultNetworkAndTrain() {
        network = new Network(List.of(4, 6, 3), 0.3, List.of(ActivationFunction.SIGMOID,ActivationFunction.SIGMOID));
        loadData();
        train(50L);
        saveNetworkConfiguration();
        loadNetwork();
        validate();
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
        Collections.shuffle(dataset);

        for (List<String> data : dataset) {
            List<Double> input = data.stream()
                                     .limit(4)
                                     .map(Double::parseDouble)
                                     .collect(Collectors.toList());
            inputs.add(input);

            val type = data.get(4);
            switch (type) {
                case "Iris-setosa":
                    expects.add(new ArrayList<Double>(Arrays.asList(1.0, 0.0, 0.0)));
                    break;
                case "Iris-versicolor":
                    expects.add(new ArrayList<Double>(Arrays.asList(0.0, 1.0, 0.0)));
                    break;
                case "Iris-virginica":
                    expects.add(new ArrayList<Double>(Arrays.asList(0.0, 0.0, 1.0)));
                    break;
                default:
                    throw new IllegalArgumentException("Invalid type");
            }
        }
        Util.normalizeByFeatureScaling(inputs);
    }
}
