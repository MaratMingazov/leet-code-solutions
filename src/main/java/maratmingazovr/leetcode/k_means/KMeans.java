package maratmingazovr.leetcode.k_means;

import lombok.Data;
import lombok.NonNull;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    @NonNull
    Logger log = LoggerFactory.getLogger(KMeans.class);


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
        clusters = new ArrayList<>();
        int dimension = points.get(0).getDimension();
        for (int i = 0; i < clustersCount; i++) {
            val randomValue = random.doubles().boxed().limit(dimension).collect(Collectors.toList());
            Cluster cluster = new Cluster(new ArrayList<>(), new Point(randomValue, "centroid"));
            clusters.add(cluster);
        }
    }

    private List<Double> generateRandomPoint(@NonNull Integer dimension) {
        return random.doubles().boxed().limit(dimension).collect(Collectors.toList());
    }

    @NonNull
    public List<Cluster> run(@NonNull Integer epoh) {
        for (int iteration = 0; iteration < epoh; iteration++) {
            for (Cluster cluster : clusters) { // clear all clusters
                cluster.getPoints().clear();
            }
            assignClusters();
            List<Point> oldCentroids = new ArrayList<>(getCentroids());
            generateCentroids(); // find new centroids
            relocateEmptyClusters();
            if (listsEqual(oldCentroids, getCentroids())) {
                log.info("Converged after " + iteration + " iterations.");
                return clusters;
            }

        }
        log.info("Finished after " + epoh + " epoh.");
        return clusters;
    }


    // We find clusters with no points and move them near cluster with max point
    private void relocateEmptyClusters() {
        for (Cluster cluster : clusters) {
            if (!cluster.getPoints().isEmpty()) {
                continue;
            }
            val maxPoints = clusters.stream().map(c -> c.getPoints().size()).max(Integer::compare).orElse(0);
            val maxPointCluster = clusters.stream().filter(c -> c.getPoints().size() == maxPoints).findFirst().orElseThrow();
            val centroid = maxPointCluster.getCentroid();
            val newCentroid = centroid.getNormalizedValue().stream().map(v -> v + v * 0.1D).collect(Collectors.toList());
            cluster.centroid = new Point(newCentroid, "");
        }
    }

    // Find the center of each cluster and move the centroid to there
    private void generateCentroids() {
        for (Cluster cluster : clusters) {
            // Ignore if the cluster is empty
            if (cluster.points.isEmpty()) {
                continue;
            }
            List<Double> means = new ArrayList<>();
            for (int i = 0; i < cluster.getPoints().get(0).getDimension(); i++) {
                int dimension = i; // needed to use in scope of closure
                val dimensionMean = cluster.points.stream()
                                                     .mapToDouble(point -> point.getNormalizedValue().get(dimension))
                                                     .average()
                                                     .orElse(0);
                means.add(dimensionMean);
            }
            cluster.centroid = new Point(means, "");
        }
    }

    @NonNull
    private List<Point> getCentroids() {
        return clusters.stream().map(Cluster::getCentroid).collect(Collectors.toList());
    }

    // Find the closest cluster centroid to each point and assign the point
    // to that cluster
    private void assignClusters() {
        for (Point point : points) {
            double lowestDistance = Double.MAX_VALUE;
            var closestCluster = clusters.get(0);
            for (Cluster cluster : clusters) {
                double centroidDistance = Util.calculateDistance(point.getNormalizedValue(), cluster.getCentroid().getNormalizedValue());
                if (centroidDistance < lowestDistance) {
                    lowestDistance = centroidDistance;
                    closestCluster = cluster;
                }
            }
            closestCluster.points.add(point);
        }
    }

    // Check if two Lists of DataPoints are of equivalent DataPoints
    private boolean listsEqual(List<Point> first, List<Point> second) {
        if (first.size() != second.size()) {
            return false;
        }
        for (int i = 0; i < first.size(); i++) {
            for (int j = 0; j < first.get(0).getDimension(); j++) {
                if (first.get(i).getNormalizedValue().get(j).doubleValue() != second.get(i).getNormalizedValue().get(j).doubleValue()) {
                    return false;
                }
            }
        }
        return true;
    }

    public Double calculateSumOfDistances() {
        Double result = 0D;
        for (Cluster cluster : clusters) {
            val centroid = cluster.getCentroid();
            result += cluster.getPoints().stream()
                    .map(point -> Util.calculateDistance(point.getNormalizedValue(), centroid.getNormalizedValue()))
                    .reduce(Double::sum)
                    .orElse(0D);
        }
        return result;
    }
}
