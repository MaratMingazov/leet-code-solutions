package maratmingazovr.leetcode.tasks.neural_network.wine_classificator;

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

public class WineClassificator extends AbstractClassificator {


    public WineClassificator() {
        super("src/main/java/maratmingazovr/leetcode/tasks/neural_network/wine_classificator/data/wine.csv",
              "src/main/java/maratmingazovr/leetcode/tasks/neural_network/wine_classificator/data/configuration.csv");
    }

    @Override
    public void createDefaultNetworkAndTrain() {
        network = new Network(List.of(13, 7, 3), 0.9, List.of(ActivationFunction.SIGMOID,ActivationFunction.SIGMOID));
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
    public void loadData() {

        val dataset = Util.loadCSV(datasetFile);
        Collections.shuffle(dataset);
        List<List<Double>> inputs = new ArrayList<>();
        List<List<Double>> expects = new ArrayList<>();

        for (List<String> data : dataset) {
            List<Double> input = data.stream()
                                     .skip(1)
                                     .map(Double::parseDouble)
                                     .collect(Collectors.toList());
            inputs.add(input);

            val type = data.get(0);
            switch (type) {
                case "1":
                    expects.add(new ArrayList<Double>(Arrays.asList(1.0, 0.0, 0.0)));
                    break;
                case "2":
                    expects.add(new ArrayList<Double>(Arrays.asList(0.0, 1.0, 0.0)));
                    break;
                case "3":
                    expects.add(new ArrayList<Double>(Arrays.asList(0.0, 0.0, 1.0)));
                    break;
                default:
                    throw new IllegalArgumentException("Invalid type");
            }
        }
        Util.normalizeByFeatureScaling(inputs);

        inputsTrain.addAll(inputs.subList(0,150));
        expectsTrain.addAll(expects.subList(0,150));
        inputsValidate.addAll(inputs.subList(150,178));
        expectsValidate.addAll(expects.subList(150,178));
    }
}
