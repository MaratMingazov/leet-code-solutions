package maratmingazovr.leetcode.neural_network.iris_classification;

import lombok.NonNull;
import lombok.val;
import maratmingazovr.leetcode.neural_network.ActivationFunction;
import maratmingazovr.leetcode.neural_network.Network;
import maratmingazovr.leetcode.neural_network.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class IrisClassificator {

    private static final String IRIS_SETOSA = "Iris-setosa";
    private static final String IRIS_VERSICOLOR = "Iris-versicolor";
    private static final String IRIS_VIRGINICA = "Iris-virginica";

    List<List<Double>> inputs = new ArrayList<>();
    List<List<Double>> expects = new ArrayList<>();
    private List<String> irisSpecies = new ArrayList<>();

    public IrisClassificator() {
        val irisDataset = Util.loadCSV("src/main/java/maratmingazovr/leetcode/neural_network/iris_classification/iris.csv");
        Collections.shuffle(irisDataset);
        for (List<String> data : irisDataset) {
            List<Double> input = data.stream().limit(4).map(Double::parseDouble).collect(Collectors.toList());
            inputs.add(input);

            val species = data.get(4);
            switch (species) {
                case IRIS_SETOSA:
                    expects.add(new ArrayList<Double>(Arrays.asList(1.0, 0.0, 0.0)));
                    break;
                case IRIS_VERSICOLOR:
                    expects.add(new ArrayList<Double>(Arrays.asList(0.0, 1.0, 0.0)));
                    break;
                default:
                    expects.add(new ArrayList<Double>(Arrays.asList(0.0, 0.0, 1.0)));
                    break;
            }
            irisSpecies.add(species);
        }
        Util.normalizeByFeatureScaling(inputs);
    }

    public Network<String>.Results classify() {
        // 4, 6, 3 layer structure; 0.3 learning rate; sigmoid activation function
        Network<String> irisNetwork = new Network<>(List.of(4,6,3), 0.3, ActivationFunction.SIGMOID);

        // train over the first 140 irises in the data set 50 times
        val inputsTrain = inputs.subList(0, 140);
        val expectsTrain = expects.subList(0, 140);
        irisNetwork.train(inputsTrain, expectsTrain, 50L);

        // test over the last 10 of the irises in the data set
        val inputsTest = inputs.subList(140, 150);
        val expectsTest = irisSpecies.subList(140, 150);
        return irisNetwork.validate(inputsTest, expectsTest, this::irisInterpretOutput);
    }

    @NonNull
    public String irisInterpretOutput(@NonNull List<Double> output) {
        double max = Util.getMaxValueIndex(output);
        if (max == 0) {
            return IRIS_SETOSA;
        } else if (max == 1) {
            return IRIS_VERSICOLOR;
        } else {
            return IRIS_VIRGINICA;
        }
    }
}
