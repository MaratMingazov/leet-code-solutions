package maratmingazovr.leetcode.k_means;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import java.util.List;

@Data
@AllArgsConstructor
public class Cluster {

    @NonNull
    List<Point> points;

    @NonNull
    Point centroid;
}
