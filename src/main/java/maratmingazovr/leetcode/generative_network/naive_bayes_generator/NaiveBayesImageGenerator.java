package maratmingazovr.leetcode.generative_network.naive_bayes_generator;

import javafx.scene.paint.Color;
import lombok.Data;
import lombok.NonNull;
import lombok.val;
import maratmingazovr.leetcode.java_fx.ImageFx;
import maratmingazovr.leetcode.neural_network.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Data
public class NaiveBayesImageGenerator {

    // list - список пикселей в картинке
    // map - отдельный пиксел, содержит вероятность Double получить значение насущенности Integer
    //
    @NonNull
    private List<SortedMap<Integer, Double>> pixelsValuesProbabilities = new ArrayList<>();
    @NonNull
    private List<SortedMap<Integer, Integer>> pixelsValuesCount = new ArrayList<>();

    public NaiveBayesImageGenerator(@NonNull Integer pixelsCount) {
        for (int pixenNumber = 0; pixenNumber < pixelsCount; pixenNumber++) {
            pixelsValuesProbabilities.add(new TreeMap<>());
            pixelsValuesCount.add(new TreeMap<>());
        }
    }


    public void addInput(@NonNull List<Color> imageColors) {
        for (int pixenNumber = 0; pixenNumber < imageColors.size(); pixenNumber++) {
            val pixelColor = imageColors.get(pixenNumber);
            val pixelValue = Util.colorToInt(pixelColor);
            val pixelValuesCount = pixelsValuesCount.get(pixenNumber);
            pixelValuesCount.putIfAbsent(pixelValue, 0);
            pixelValuesCount.put(pixelValue, pixelValuesCount.get(pixelValue) + 1);
        }
    }

    public void calculateProbabilities() {
        for (int pixelNumber = 0; pixelNumber < pixelsValuesCount.size(); pixelNumber++) {
            val pixelValuesCount = pixelsValuesCount.get(pixelNumber);
            val pixelValues = new ArrayList<>(pixelValuesCount.keySet());
            val pixelCounts = new ArrayList<>(pixelValuesCount.values());
            val pixelProbabilities =  Util.getProbabilityDistribution(pixelCounts);

            val pixelValuesProbabilities = pixelsValuesProbabilities.get(pixelNumber);
            pixelValuesProbabilities.clear();
            for (int i = 0; i < pixelValues.size(); i++) {
                val pixelValue = pixelValues.get(i);
                val pixelProbability = pixelProbabilities.get(i);
                pixelValuesProbabilities.put(pixelValue, pixelProbability);
            }
        }
        int a = 3;
    }

    @NonNull
    public ImageFx generateImage() {
        val map = generateImages();
        return map.get(map.lastKey());
    }

    private SortedMap<Double, ImageFx> generateImages() {
        SortedMap<Double, ImageFx> result = new TreeMap<>();
        for (int i = 0; i < 10000; i++) {
            List<Double> pixelProbabilities = new ArrayList<>();
            val colors = pixelsValuesProbabilities.stream().map(pixelValuesProbabilities -> {
                val pixelValues = new ArrayList<>(pixelValuesProbabilities.keySet());
                val pixelCounts = new ArrayList<>(pixelValuesProbabilities.values());
                val colorValuePair = Util.getProbabilityValue(pixelValues, pixelCounts);
                val colorValue = colorValuePair.getKey();
                val colorProbability = colorValuePair.getValue();
                pixelProbabilities.add(colorProbability);
                return Util.IntToColor(colorValue);
            }).collect(Collectors.toList());
            val totalProbability = pixelProbabilities.stream().reduce((x,y) -> x * y).orElse(0.0);
            result.put(totalProbability, new ImageFx(colors));
        }
        return result;
    }
}
