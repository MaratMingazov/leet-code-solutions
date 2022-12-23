package maratmingazovr.leetcode.neural_network.wine_classification;

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

public class WineClassificator {

    List<List<Double>> inputs = new ArrayList<>();
    List<List<Double>> expects = new ArrayList<>();
    private List<Integer> wineSpecies = new ArrayList<>();

    public WineClassificator() {
        val wineDataset = Util.loadCSV("src/main/java/maratmingazovr/leetcode/neural_network/wine_classification/wine.csv");
        Collections.shuffle(wineDataset);

        for (List<String> data : wineDataset) {
            // last thirteen items are parameters (doubles)
            List<Double> input = data.stream().skip(1).map(Double::parseDouble).collect(Collectors.toList());
            inputs.add(input);

            // first item is species
            val species = Integer.parseInt(data.get(0));
            switch (species) {
                case 1:
                    expects.add(new ArrayList<Double>(Arrays.asList(1.0, 0.0, 0.0)));
                    break;
                case 2:
                    expects.add(new ArrayList<Double>(Arrays.asList(0.0, 1.0, 0.0)));
                    break;
                default:
                    expects.add(new ArrayList<Double>(Arrays.asList(0.0, 0.0, 1.0)));
                    break;
            }
            wineSpecies.add(species);
        }
        Util.normalizeByFeatureScaling(inputs);
    }

    public Network<Integer>.Results classify() {
        // 4, 6, 3 layer structure; 0.3 learning rate; sigmoid activation function
        Network<Integer> wineNetwork = new Network<>(List.of(13,7,3), 0.9, ActivationFunction.SIGMOID);

        // train over the first 140 irises in the data set 50 times
        val inputsTrain = inputs.subList(0, 150);
        val expectsTrain = expects.subList(0, 150);
        wineNetwork.train(inputsTrain, expectsTrain, 10L);

        // test over the last 10 of the irises in the data set
        val inputsTest = inputs.subList(150, 178);
        val expectsTest = wineSpecies.subList(150, 178);
        return wineNetwork.validate(inputsTest, expectsTest, this::wineInterpretOutput);
    }

    @NonNull
    public Integer wineInterpretOutput(@NonNull List<Double> output) {
        double max = Util.getMaxValueIndex(output);
        if (max == 0) {
            return 1;
        } else if (max == 1) {
            return 2;
        } else {
            return 3;
        }
    }

}
