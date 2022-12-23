package maratmingazovr.leetcode.tasks.neural_network.pushkin_classificator;

import lombok.NonNull;
import lombok.val;
import maratmingazovr.leetcode.neural_network.ActivationFunction;
import maratmingazovr.leetcode.neural_network.Network;
import maratmingazovr.leetcode.neural_network.Util;
import maratmingazovr.leetcode.tasks.neural_network.AbstractClassificator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class PushkinClassificator extends AbstractClassificator {

    private List<String> words = new ArrayList<>();

    public PushkinClassificator() {
        super(
                "src/main/java/maratmingazovr/leetcode/tasks/neural_network/pushkin_classificator/data/data_train.txt",
                "src/main/java/maratmingazovr/leetcode/tasks/neural_network/pushkin_classificator/data/data_test.txt",
              "src/main/java/maratmingazovr/leetcode/tasks/neural_network/pushkin_classificator/data/configuration.txt");
    }
    @Override
    public void createDefaultNetworkAndTrain() {
        network = new Network(List.of(words.size(), words.size(), words.size()), 0.3, List.of(ActivationFunction.SIGMOID, ActivationFunction.SIGMOID));
    }

    @Override
    public @NonNull Boolean isExpectedEqualToOutput(@NonNull List<Double> expected,
                                                    @NonNull List<Double> output) {
        return false;
    }

    @Override
    public void loadData(@NonNull String datasetFile,
                         @NonNull List<List<Double>> inputs,
                         @NonNull List<List<Double>> expects) {
        var dataset = Util.loadCSV(datasetFile);
        dataset = removeCharacters(dataset);

        addToTotalList(dataset);

        for (int i = 0; i < dataset.size()-1; i++) {
            val inputStr = dataset.get(i);
            inputs.add(convertToDouble(inputStr));
            val outputStr = dataset.get(i+1);
            expects.add(convertToDouble(outputStr));
        }
    }

    @NonNull
    private List<List<String>> removeCharacters(@NonNull List<List<String>> dataset) {
        return dataset.stream()
                      .map(list -> list.stream()
                                       .map(str -> str.replace(",",""))
                                       .map(str -> str.replace(".",""))
                                       .map(str -> str.replace("!",""))
                                       .map(str -> str.replace(";",""))
                                       .map(str -> str.replace(":",""))
                                       .map(str -> str.replace("—",""))
                                       .map(String::toLowerCase)
                                       .collect(Collectors.toList())
                          )
                      .collect(Collectors.toList());
    }

    private void addToTotalList(@NonNull List<List<String>> data) {
        for (List<String> datum : data) {
            words.addAll(datum);
        }
        Set<String> set = new HashSet<>(words);
        words = new ArrayList<>(set);
        Collections.sort(words);
    }

    @NonNull
    private List<Double> convertToDouble(@NonNull List<String> data) {
        double[] inputs = new double[words.size()];
        Arrays.fill(inputs, 0.1);
        for (String datum : data) {
            inputs[words.indexOf(datum)] = 0.9;
        }
        return Arrays.stream(inputs).boxed().collect(Collectors.toList());
    }

    @NonNull
    private List<String> convertToString(@NonNull List<Double> data) {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i) > 0.8) {
                result.add(words.get(i));
            }
        }
        return result;
    }

    @NonNull
    private Map<String, Double> convertToStringMap(@NonNull List<Double> data) {
        Map<String, Double> result = new HashMap<>();
        int iterations = 5;
        while (iterations > 0) {
            val max = data.stream().max(Double::compare).get();
            val maxIndex = data.indexOf(max);
            result.put(words.get(maxIndex), max);
            data.set(maxIndex, 0.0);
            iterations--;
        }
        return result;
    }
}
