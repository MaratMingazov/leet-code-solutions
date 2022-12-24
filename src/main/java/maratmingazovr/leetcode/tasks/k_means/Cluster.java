package maratmingazovr.leetcode.tasks.k_means;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import java.util.List;

@Data
@AllArgsConstructor
public class Cluster {

    @NonNull
    List<List<Double>> points;

    @NonNull
    List<Double> centroid;
}
