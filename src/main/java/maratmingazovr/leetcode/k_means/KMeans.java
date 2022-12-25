package maratmingazovr.leetcode.k_means;

import lombok.Data;
import lombok.NonNull;
import lombok.val;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Data
public class KMeans {

    @NonNull
    private List<Point> points;
    @NonNull
    private List<Cluster> clusters;

    @NonNull
    private Random random = new Random();


    public KMeans(@NonNull List<Point> points,
                  @NonNull Integer clustersCount) {
        if (clustersCount < 1) {
            throw new IllegalArgumentException("k must be >= 1");
        }
        this.points = points;

        List<List<Double>> valuesToNormalize = new ArrayList<>();
        for (Point point : points) {
            valuesToNormalize.add(point.getNormalizedValue());
        }
        Util.normalizeByFeatureScaling(valuesToNormalize);

        // initialize empty clusters with random centroids
        val clusters = new ArrayList<>();
        int dimension = points.get(0).getDimension();
        for (int i = 0; i < clustersCount; i++) {
            val randomValue = random.doubles().boxed().limit(dimension).collect(Collectors.toList());
            Cluster cluster = new Cluster(new ArrayList<>(), randomValue);
            clusters.add(cluster);
        }
    }

    private List<Double> generateRandomPoint(@NonNull Integer dimension) {
        return random.doubles().boxed().limit(dimension).collect(Collectors.toList());
    }

    @NonNull
    public List<Cluster> run(@NonNull Integer epoh) {
        return new ArrayList<>();
    }
}
