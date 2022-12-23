package maratmingazovr.leetcode.tasks.neural_network.numbers_multiplicator;

import lombok.NonNull;
import lombok.val;
import maratmingazovr.leetcode.neural_network.Util;
import maratmingazovr.leetcode.tasks.neural_network.AbstractClassificator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class DecimalMultiplicator extends AbstractClassificator {

    public DecimalMultiplicator() {
        super("src/main/java/maratmingazovr/leetcode/tasks/neural_network/numbers_multiplicator/data/numbers.txt",
              "src/main/java/maratmingazovr/leetcode/tasks/neural_network/numbers_multiplicator/data/configuration.txt");
    }

    @Override
    public void createDefaultNetworkAndTrain() {

    }

    @NonNull
    @Override
    public Boolean isExpectedEqualToOutput(@NonNull List<Double> expected,
                                           @NonNull List<Double> output) {

        val expectedMaxValueIndex = expected.get(0);
        val outputMaxValueIndex = output.get(0);
        return expectedMaxValueIndex.equals(outputMaxValueIndex);
    }

    @Override
    public void loadData() {

        val dataset = Util.loadCSV(datasetFile);
        Collections.shuffle(dataset);
        List<List<Double>> inputs = new ArrayList<>();
        List<List<Double>> expects = new ArrayList<>();

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
        Util.normalizeByFeatureScaling(inputs);

        inputsTrain.addAll(inputs.subList(0,150));
        expectsTrain.addAll(expects.subList(0,150));
        inputsValidate.addAll(inputs.subList(150,178));
        expectsValidate.addAll(expects.subList(150,178));
    }
}
