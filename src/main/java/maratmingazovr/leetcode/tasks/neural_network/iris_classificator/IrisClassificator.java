package maratmingazovr.leetcode.neural_network.iris_classification;

import lombok.NonNull;
import lombok.val;
import maratmingazovr.leetcode.neural_network.ActivationFunction;
import maratmingazovr.leetcode.neural_network.Network;
import maratmingazovr.leetcode.neural_network.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private Network<String> irisNetwork;

    private final String datasetFilename = "src/main/java/maratmingazovr/leetcode/neural_network/iris_classification/iris.csv";
    private final String configurationFilename = "src/main/java/maratmingazovr/leetcode/neural_network/iris_classification/configuration.txt";

    @NonNull
    Logger log = LoggerFactory.getLogger(Network.class);

    public IrisClassificator() {
        val irisDataset = Util.loadCSV(datasetFilename);
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

    public void loadNetwork() {
        irisNetwork = new Network<>(Util.loadNetworkConfiguration(configurationFilename));
    }

    public void createNetwork() {
        irisNetwork = new Network<>(List.of(4,6,3), 0.3, ActivationFunction.SIGMOID);
    }

    public void train() {
        val inputsTrain = inputs.subList(0, 140);
        val expectsTrain = expects.subList(0, 140);
        irisNetwork.train(inputsTrain, expectsTrain, 50L);
    }

    public void saveNetworkConfiguration() {
        Util.saveNetworkConfiguration(configurationFilename, irisNetwork.getConfiguration());
    }

    public Network<String>.Results validate() {
        val inputsTest = inputs.subList(140, 150);
        val expectsTest = irisSpecies.subList(140, 150);
        val result =  irisNetwork.validate(inputsTest, expectsTest, this::irisInterpretOutput);
        log.info(result.correct + " correct of " + result.trials + " = " + result.percentage * 100 + "%");
        return result;
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
