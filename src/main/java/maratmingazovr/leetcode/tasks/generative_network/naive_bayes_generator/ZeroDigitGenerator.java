package maratmingazovr.leetcode.tasks.generative_network.naive_bayes_generator;

import javafx.scene.paint.Color;
import lombok.Data;
import lombok.NonNull;
import lombok.val;
import maratmingazovr.leetcode.neural_network.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class ZeroDigitGenerator {
    @NonNull
    private List<List<Integer>> inputs = new ArrayList<>();

    public ZeroDigitGenerator() {
        val datasetFile = "src/main/java/maratmingazovr/leetcode/tasks/generative_network/naive_bayes_generator/data/mnist_train_100.csv";
        val dataset = Util.loadCSV(datasetFile);

        for (List<String> data : dataset) {
            val digit = data.get(0);
            if (!digit.equals("0")) {
                continue;
            }
            List<Integer> input = data.stream()
                                     .skip(1)
                                     .map(Integer::parseInt)
                                     .map(v -> 255 - v)
                                     .collect(Collectors.toList());
            inputs.add(input);
        }
    }



    public List<List<Color>> getColors() {
        List<List<Color>> result = new ArrayList<>();
        for (List<Integer> image : inputs) {
            List<Color> imageColors = image.stream().map(v -> ((double) v) / 255).map(Color::gray).collect(Collectors.toList());
            result.add(imageColors);
        }
        return result;
    }
}
