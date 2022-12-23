package maratmingazovr.leetcode.tasks.neural_network.numbers_multiplicator;

import lombok.NonNull;
import lombok.val;
import maratmingazovr.leetcode.neural_network.ActivationFunction;
import maratmingazovr.leetcode.neural_network.Network;
import maratmingazovr.leetcode.neural_network.Util;
import maratmingazovr.leetcode.tasks.neural_network.AbstractClassificator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class DecimalMultiplicator extends AbstractClassificator {

    public DecimalMultiplicator() {
        super(
                "src/main/java/maratmingazovr/leetcode/tasks/neural_network/numbers_multiplicator/data/numbers_train.txt",
                "src/main/java/maratmingazovr/leetcode/tasks/neural_network/numbers_multiplicator/data/numbers_test.txt",
              "src/main/java/maratmingazovr/leetcode/tasks/neural_network/numbers_multiplicator/data/decimal_configuration.txt"
             );
    }

    @Override
    public void createDefaultNetworkAndTrain() {
//        network = new Network(List.of(2,6,5,1), 0.9, List.of(ActivationFunction.SIGMOID, ActivationFunction.SIGMOID, ActivationFunction.SIGMOID));
//        saveNetworkConfiguration();
        loadNetwork();
        loadData();
        train(10000L);
    }

    @NonNull
    @Override
    public Boolean isExpectedEqualToOutput(@NonNull List<Double> expected,
                                           @NonNull List<Double> output) {

        val expectedMaxValueIndex = expected.get(0);
        val outputMaxValueIndex = output.get(0);

        return Math.abs(expectedMaxValueIndex - outputMaxValueIndex) < 2.0;
    }

    @Override
    public void loadData(@NonNull String datasetFile,
                         @NonNull List<List<Double>> inputs,
                         @NonNull List<List<Double>> expects) {

        val dataset = Util.loadCSV(datasetFile);
//        Collections.shuffle(dataset);
        inputs.clear();
        expects.clear();

        for (List<String> data : dataset) {
            val inputList = data.stream()
                                .limit(2)
                                .map(Double::parseDouble)
                                .map(value -> value / 10)
                                .collect(Collectors.toList());
            List<Double> input = new ArrayList<>();
            input.add(inputList.get(0));
            input.add(inputList.get(1));
            inputs.add(input);

            val expectString = data.get(2);
            val expectedDouble = Double.parseDouble(expectString) / 100;
            expects.add(List.of(expectedDouble));
        }
//        Util.normalizeByFeatureScaling(inputs);
    }
}
