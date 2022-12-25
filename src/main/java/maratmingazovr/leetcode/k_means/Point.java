package maratmingazovr.leetcode.k_means;

import lombok.Data;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

@Data
public class Point {

    @NonNull
    private List<Double> originalValue;

    @NonNull
    private List<Double> normalizedValue;

    @NonNull
    private Integer dimension;

    @NonNull
    private String label;

    public Point(@NonNull List<Double> value, @NonNull String label) {
        this.originalValue = new ArrayList<>(value);
        this.normalizedValue = new ArrayList<>(value);
        this.dimension = value.size();
        this.label = label;
    }
}
