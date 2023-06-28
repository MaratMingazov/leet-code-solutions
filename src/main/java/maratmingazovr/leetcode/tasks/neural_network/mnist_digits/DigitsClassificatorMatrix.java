package maratmingazovr.leetcode.tasks.neural_network.mnist_digits;

import lombok.NonNull;
import maratmingazovr.leetcode.neural_network.ActivationFunction;
import maratmingazovr.leetcode.neural_network_matrix.NeuralNetworkMatrix;
import maratmingazovr.leetcode.tasks.neural_network.AbstractClassificatorMatrix;

import java.util.List;

public class DigitsClassificatorMatrix extends AbstractClassificatorMatrix {

    public static void main(String[] args) {
        DigitsClassificatorMatrix classificator = new DigitsClassificatorMatrix();
        classificator.loadNetwork();
    }

    public DigitsClassificatorMatrix() {
        super(
                "src/main/java/maratmingazovr/leetcode/tasks/neural_network/mnist_digits/data/mnist_train_100.csv",
                "src/main/java/maratmingazovr/leetcode/tasks/neural_network/mnist_digits/data/mnist_test_10.csv",
                "src/main/java/maratmingazovr/leetcode/tasks/neural_network/mnist_digits/data/configuration_matrix.txt");
    }
    @Override
    public void createDefaultNetworkAndTrain() {

        double[][] wih = new double[][]{
                new double[]{0.37, 0.48, 0.51},
                new double[]{0.11, 0.83, 0.44},
                new double[]{0.55, 0.63, 0.27}
        };
        double[][] who = new double[][]{
                new double[]{0.37, 0.51, 0.14},
                new double[]{0.11, 0.91, 0.57},
                new double[]{0.07, 0.81, 0.36}
        };
        double[] bih = new double[]{0.31, 0.93, 0.47};
        double[] bho = new double[]{0.11, 0.25, 0.36};

        network = new NeuralNetworkMatrix(3, 3, 3, 0.3,
                                          ActivationFunction.SIGMOID, ActivationFunction.SIGMOID,
                                          wih, bih, who, bho);

        double[][] inputs = new double[][]{new double[]{0.25, 0.48, 0.86}};
        double[][] targets = new double[][]{new double[]{0.86, 0.45, 0.74}};

        network.train(inputs, targets, 10);
        saveNetworkConfiguration();
    }

    public void loadNetworkAndTrain() {
        loadNetwork();
        int a = 3;
    }

    @Override
    public @NonNull Boolean isExpectedEqualToOutput(@NonNull List<Double> expected,
                                                    @NonNull List<Double> output) {
        return null;
    }

    @Override
    public void loadData(String datasetFile,
                         List<List<Double>> inputs,
                         List<List<Double>> targets) {

    }
}
