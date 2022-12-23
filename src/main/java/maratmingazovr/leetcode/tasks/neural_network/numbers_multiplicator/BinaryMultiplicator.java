package maratmingazovr.leetcode.tasks.neural_network.numbers_multiplicator;

import lombok.NonNull;
import lombok.val;
import maratmingazovr.leetcode.neural_network.Util;
import maratmingazovr.leetcode.tasks.neural_network.AbstractClassificator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BinaryMultiplicator extends AbstractClassificator {

    public BinaryMultiplicator() {
        super(
                "src/main/java/maratmingazovr/leetcode/tasks/neural_network/numbers_multiplicator/data/numbers_train.txt",
                "src/main/java/maratmingazovr/leetcode/tasks/neural_network/numbers_multiplicator/data/numbers_test.txt",
              "src/main/java/maratmingazovr/leetcode/tasks/neural_network/numbers_multiplicator/data/binary_configuration.txt");
    }

    @Override
    public void createDefaultNetworkAndTrain() {
//        network = new Network(List.of(14, 11, 11, 7), 0.7, List.of(ActivationFunction.SIGMOID, ActivationFunction.SIGMOID, ActivationFunction.SIGMOID, ActivationFunction.SIGMOID));
//        saveNetworkConfiguration();
        loadNetwork();
        loadData();
        validate();
//        train(10000L);
//        int a = 3;
//        validate();
//        int s = 3;
    }

    @Override
    public @NonNull Boolean isExpectedEqualToOutput(@NonNull List<Double> expected,
                                                    @NonNull List<Double> output) {

        val expectedBinaryString = convertDoubletoBinaryString(expected);
        int expectedInt = Integer.parseInt(expectedBinaryString, 2);
        val outputBinaryString = convertDoubletoBinaryString(output);
        int outputInt = Integer.parseInt(outputBinaryString, 2);

        return Math.abs(expectedInt - outputInt) < 3;

//        for (int i = 0; i < expected.size(); i++) {
//            if (expected.get(i) == 0.9 && output.get(i) < 0.5) {
//                return false;
//            }
//        }
//
//        return true;
    }

    @NonNull
    private String convertDoubletoBinaryString(@NonNull List<Double> values) {
        val builder = new StringBuilder();
        for (Double value : values) {
            if (value > 0.7D) {
                builder.append("1");
            } else {
                builder.append("0");
            }
        }
        return  builder.toString();
    }

    @Override
    public void loadData(@NonNull String datasetFile,
                         @NonNull List<List<Double>> inputs,
                         @NonNull List<List<Double>> expects) {
        val dataset = Util.loadCSV(datasetFile);
        //Collections.shuffle(dataset);

        for (List<String> data : dataset) {
            val inputList = data.stream()
                            .limit(2)
                            .map(Integer::valueOf)
                            .map(Integer::toBinaryString)
                            .map(v -> String.format("%8s", v))
                            .map(this::convertBinaryStringToDouble)
                            .collect(Collectors.toList());
            List<Double> input = new ArrayList<>();
            input.addAll(inputList.get(0));
            input.addAll(inputList.get(1));
            inputs.add(input);

            val expectString = data.get(2);
            val expectInt = Integer.parseInt(expectString);
            val expectBinaryString = Integer.toBinaryString(expectInt);
            List<Double> expect = convertBinaryStringToDouble(String.format("%8s", expectBinaryString));
            expects.add(expect);
        }
        //Util.normalizeByFeatureScaling(inputs);
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
