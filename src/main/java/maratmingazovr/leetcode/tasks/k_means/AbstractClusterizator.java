package maratmingazovr.leetcode.tasks.k_means;

import lombok.NonNull;
import lombok.val;
import maratmingazovr.leetcode.k_means.Cluster;
import maratmingazovr.leetcode.k_means.KMeans;
import maratmingazovr.leetcode.k_means.Point;
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
    Logger log = LoggerFactory.getLogger(AbstractClusterizator.class);

    private KMeans kMeans;

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
    public List<Cluster> run(@NonNull Integer clustersCount,
                            @NonNull Integer epoh) {

        val points = calulatePoints();
        kMeans = new KMeans(points, clustersCount);
        val clusters = kMeans.run(epoh);
        log.info("Finish");
        return clusters;
    }

    public void describe(@NonNull List<Cluster> clusters) {
        val distance = kMeans.calculateSumOfDistances();
        log.info("distance = " + distance);
        for (Cluster cluster : clusters) {
            log.info("Cluster: ");
            for (Point point : cluster.getPoints()) {
                log.info("  " + point.getLabel() + " / " + point.getOriginalValue());
            }
        }
    }

    public void findClustersNumber(@NonNull Integer maxClustersNubmer,
                                   @NonNull Integer epoh) {
        val points = calulatePoints();
        for (int i = 1; i <= maxClustersNubmer; i++) {
            val kMeans = new KMeans(points, i);
            val clusters = kMeans.run(epoh);
            val distance = kMeans.calculateSumOfDistances();
            log.info("clusters = " + i + " / distance = " + distance);
            describe(clusters);
        }
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
