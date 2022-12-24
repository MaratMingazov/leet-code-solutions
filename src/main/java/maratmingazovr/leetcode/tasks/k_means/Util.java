package maratmingazovr.leetcode.tasks.k_means;

import lombok.NonNull;
import lombok.val;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Util {



    public static void normalizeByFeatureScaling(@NonNull List<List<Double>> points) {
        for (int colNum = 0; colNum < points.get(0).size(); colNum++) {
            List<Double> column = new ArrayList<>();
            for (List<Double> row : points) {
                column.add(row.get(colNum));
            }
            double maximum = Collections.max(column);
            double minimum = Collections.min(column);
            double difference = maximum - minimum;
            for (List<Double> row : points) {
                row.set(colNum, (row.get(colNum) - minimum) / difference);
            }
        }
    }

    @NonNull
    public static Double calculateDistance(@NonNull List<Double> point1,
                                           @NonNull List<Double> point2) {

        if (point1.size() != point2.size()) {
            throw new IllegalArgumentException("Points should have same dimension");
        }

        double differences = 0.0;
        for (int i = 0; i < point1.size(); i++) {
            val difference = point1.get(i) - point2.get(i);
            differences += Math.pow(difference, 2);
        }
        return Math.sqrt(differences);
    }
}
