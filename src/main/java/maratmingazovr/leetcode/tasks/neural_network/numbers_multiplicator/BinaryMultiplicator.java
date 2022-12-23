package maratmingazovr.leetcode.tasks.neural_network.numbers_multiplicator;

import lombok.NonNull;
import lombok.val;
import maratmingazovr.leetcode.neural_network.Util;
import maratmingazovr.leetcode.tasks.neural_network.AbstractClassificator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class BinaryMultiplicator extends AbstractClassificator {

    public BinaryMultiplicator() {
        super("src/main/java/maratmingazovr/leetcode/tasks/neural_network/numbers_multiplicator/data/numbers.txt",
              "src/main/java/maratmingazovr/leetcode/tasks/neural_network/numbers_multiplicator/data/configuration.txt");
    }

    @Override
    public void createDefaultNetworkAndTrain() {

    }

    @Override
    public @NonNull Boolean isExpectedEqualToOutput(@NonNull List<Double> expected,
                                                    @NonNull List<Double> output) {
        return false;
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
                            .map(Integer::valueOf)
                            .map(Integer::toBinaryString)
                            .map(v -> String.format("%6s", v))
                            .map(this::convertBinaryStringToDouble)
                            .collect(Collectors.toList());
            List<Double> input = new ArrayList<>();
            input.addAll(inputList.get(0));
            input.addAll(inputList.get(1));
            inputs.add(input);

            val expectString = data.get(2);
            val expectInt = Integer.parseInt(expectString);
            val expectBinaryString = Integer.toBinaryString(expectInt);
            List<Double> expect = convertBinaryStringToDouble(String.format("%6s", expectBinaryString));
            expects.add(expect);
        }
        Util.normalizeByFeatureScaling(inputs);

        inputsTrain.addAll(inputs.subList(0,150));
        expectsTrain.addAll(expects.subList(0,150));
        inputsValidate.addAll(inputs.subList(150,178));
        expectsValidate.addAll(expects.subList(150,178));
    }

    @NonNull
    private List<Double> convertBinaryStringToDouble(@NonNull String value) {
        List<Double> result = new ArrayList<>();
        for (int i = 0; i < value.length(); i++) {
            val digit =  value.substring(i,i+1);
            if (digit.equals("1")) {
                result.add(0.9);
            } else {
                result.add(0.1);
            }
        }
        return result;
    }
}
