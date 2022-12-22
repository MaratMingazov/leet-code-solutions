package maratmingazovr.leetcode.neural_network.words_classification;

import lombok.NonNull;
import lombok.val;
import maratmingazovr.leetcode.neural_network.ActivationFunction;
import maratmingazovr.leetcode.neural_network.Network;
import maratmingazovr.leetcode.neural_network.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class WordsClassificator {

    private List<String> words = new ArrayList<>();

    List<List<Double>> inputs = new ArrayList<>();
    List<List<Double>> expects = new ArrayList<>();

    public WordsClassificator() {
        var ruslanLudmilaData = Util.loadCSV("src/main/java/maratmingazovr/leetcode/neural_network/words_classification/data/RuslanLudmila.txt", " ");
        ruslanLudmilaData = removeCharacters(ruslanLudmilaData);


        addToTotalList(ruslanLudmilaData);
        generateInputsAndExpects(ruslanLudmilaData);


       // val list = inputs.stream().map(this::convertToString).collect(Collectors.toList());

        Network<Integer> wineNetwork = new Network<>(List.of(words.size(), words.size(), words.size()), 0.4, ActivationFunction.SIGMOID);
        wineNetwork.train(inputs, expects, 40000L);

//convertToStringMap(wineNetwork.calculateOutputs(convertToDouble(ruslanLudmilaData.get(0))))
//convertToStringMap(wineNetwork.calculateOutputs(convertToDouble(List.of("у","лукоморья","дуб","златая"))))

    }

    private void generateInputsAndExpects(@NonNull List<List<String>> data) {
        for (int i = 0; i < data.size()-1; i++) {
            val inputStr = data.get(i);
            inputs.add(convertToDouble(inputStr));
            val outputStr = data.get(i+1);
            expects.add(convertToDouble(outputStr));
        }
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

    private void addToTotalList(@NonNull List<List<String>> data) {
        for (List<String> datum : data) {
            words.addAll(datum);
        }
        Set<String> set = new HashSet<>(words);
        words = new ArrayList<>(set);
        Collections.sort(words);
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
}
