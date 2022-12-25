package maratmingazovr.leetcode.tasks.k_means;

import lombok.NonNull;
import lombok.val;
import maratmingazovr.leetcode.k_means.Cluster;
import maratmingazovr.leetcode.k_means.KMeans;
import maratmingazovr.leetcode.k_means.Point;
import maratmingazovr.leetcode.neural_network.Network;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractClusterizator {

    @NonNull
    protected String datasetFile;

    @NonNull
    protected List<List<Double>> inputs = new ArrayList<>();
    @NonNull
    protected List<String> labels = new ArrayList<>();

    @NonNull
    Logger log = LoggerFactory.getLogger(Network.class);

    protected AbstractClusterizator(@NonNull String datasetFile) {
        this.datasetFile = datasetFile;
    }

    public void loadData() {
        loadData(datasetFile, inputs, labels);
    }

    protected abstract void loadData(@NonNull String datasetFile,
                                     @NonNull List<List<Double>> inputs,
                                     @NonNull List<String> labels);

    @NonNull
    public List<Cluster> run(@NonNull Integer epoh,
                             @NonNull Integer clustersCount) {

        val points = calulatePoints();
        val kMeans = new KMeans(points, clustersCount);
        val clusters = kMeans.run(epoh);
        log.info("Finish");
        return clusters;
    }

    @NonNull
    private List<Point> calulatePoints() {
        List<Point> result = new ArrayList<>();
        for (int i = 0; i < inputs.size(); i++) {
            result.add(new Point(inputs.get(i), labels.get(i)));
        }
        return result;
    }
}
