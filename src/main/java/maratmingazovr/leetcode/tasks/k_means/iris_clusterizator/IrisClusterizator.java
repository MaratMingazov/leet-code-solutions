package maratmingazovr.leetcode.tasks.k_means.iris_clusterizator;

import lombok.NonNull;
import lombok.val;
import maratmingazovr.leetcode.neural_network.Util;
import maratmingazovr.leetcode.tasks.k_means.AbstractClusterizator;

import java.util.List;
import java.util.stream.Collectors;

public class IrisClusterizator extends AbstractClusterizator {

    public IrisClusterizator() {
        super("src/main/java/maratmingazovr/leetcode/tasks/k_means/iris_clusterizator/data/iris.csv");
    }

    @Override
    protected void loadData(@NonNull String datasetFile,
                            @NonNull List<List<Double>> inputs,
                            @NonNull List<String> labels) {
        val dataset = Util.loadCSV(datasetFile);

        for (List<String> data : dataset) {
            List<Double> input = data.stream()
                                     .limit(4)
                                     .map(Double::parseDouble)
                                     .collect(Collectors.toList());
            inputs.add(input);
            labels.add(data.get(4));
        }
    }
}
