package maratmingazovr.leetcode.tasks.k_means;

import lombok.NonNull;
import lombok.Value;
import lombok.val;

import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.stream.Collectors;

@Value
public class Statistics {

    @NonNull
    List<Double> list;
    @NonNull
    DoubleSummaryStatistics dss;

    public Statistics(@NonNull List<Double> list) {
        if (list.isEmpty()) {
            throw new IllegalArgumentException("list should not be empty");
        }
        this.list = list;
        this.dss = list.stream().collect(Collectors.summarizingDouble(d -> d));
    }

    @NonNull
    public Double getSum() {
        return dss.getSum();
    }

    @NonNull
    public Double getAverage() {
        return dss.getAverage();
    }

    // Find the variance sum((Xi - mean)^2) / N
    // Дисперсия
    @NonNull
    public Double getVariance() {
        val average = getAverage();
        return list.stream()
                   .mapToDouble(x -> Math.pow((x - average), 2))
                   .average()
                   .orElse(0D);
    }

    // Find the standard deviation sqrt(variance)
    @NonNull
    public Double getStd() {
        return Math.sqrt(getVariance());
    }

    // Convert elements to respective z-scores (formula z-score =
    // (x - mean) / std)
    @NonNull
    public List<Double> getZScored() {
        val average = getAverage();
        val std = getStd();
        return list.stream()
                   .map(x -> std != 0 ? ((x - average) / std) : 0.0)
                   .collect(Collectors.toList());
    }

    @NonNull
    public Double getMax() {
        return dss.getMax();
    }

    @NonNull
    public Double getMin() {
        return dss.getMin();
    }


}
