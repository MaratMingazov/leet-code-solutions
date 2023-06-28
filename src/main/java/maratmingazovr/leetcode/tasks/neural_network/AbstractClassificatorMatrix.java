package maratmingazovr.leetcode.tasks.neural_network;

import lombok.Data;
import lombok.NonNull;
import maratmingazovr.leetcode.neural_network.Network;
import maratmingazovr.leetcode.neural_network.NetworkConfiguration;
import maratmingazovr.leetcode.neural_network.Util;
import maratmingazovr.leetcode.neural_network.ValidationResult;
import maratmingazovr.leetcode.neural_network_matrix.NeuralNetworkMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Data
public abstract class AbstractClassificatorMatrix {

    @NonNull
    protected final String datasetFileTrain;
    @NonNull
    protected final String datasetFileValidate;
    @NonNull
    protected final String configurationFile;
//    @NonNull
//    protected List<List<Double>> inputsTrain = new ArrayList<>();
//    @NonNull
//    protected List<List<Double>> expectsTrain = new ArrayList<>();
//    @NonNull
//    protected List<List<Double>> inputsValidate = new ArrayList<>();
//    @NonNull
//    protected List<List<Double>> expectsValidate = new ArrayList<>();

    @NonNull Logger log = LoggerFactory.getLogger(Network.class);

    protected NeuralNetworkMatrix network;

    protected AbstractClassificatorMatrix(@NonNull String datasetFileTrain,
                                          @NonNull String datasetFileValidate,
                                          @NonNull String configurationFile) {
        this.datasetFileTrain = datasetFileTrain;
        this.datasetFileValidate = datasetFileValidate;
        this.configurationFile = configurationFile;
    }

    public void loadNetwork() {
        NetworkConfiguration configuration = Util.loadNetworkConfiguration(configurationFile);
        List<Integer> layerStructure = configuration.getLayersStructure();
        int inputNodes = layerStructure.get(0);
        int hiddenNodes = layerStructure.get(1);
        int outputNodes = layerStructure.get(2);
        List<List<Double>> allWeights = configuration.getLayersWeights();

        double [][]wih = new double[hiddenNodes][inputNodes];
        double[] bih = new double[hiddenNodes];
        for (int row = 0; row < hiddenNodes; row++) {
            List<Double> weights = allWeights.get(row);
            for (int col = 0; col < inputNodes; col++) {
                wih[row][col] = weights.get(col);
            }
            bih[row] = weights.get(hiddenNodes);
        }
        double [][] who = new double[outputNodes][hiddenNodes];
        double[] bho = new double[outputNodes];
        for (int row = hiddenNodes; row < hiddenNodes+outputNodes; row++) {
            List<Double> weights = allWeights.get(row);
            for (int col = 0; col < hiddenNodes; col++) {
                who[row-hiddenNodes][col] = weights.get(col);
            }
            bho[row-hiddenNodes] = weights.get(outputNodes);
        }

        network = new NeuralNetworkMatrix(inputNodes, hiddenNodes, outputNodes,
                                          configuration.getLearningRate(),
                                          configuration.getActivationFunctions().get(0),
                                          configuration.getActivationFunctions().get(1),
                                          wih, bih, who, bho);

    }

    public abstract void createDefaultNetworkAndTrain();

    public void saveNetworkConfiguration() {
        Util.saveNetworkConfiguration(configurationFile, new NetworkConfiguration(network));
    }

    public ValidationResult validate() {
//        network.validate(inputsTrain, expectsTrain, this::isExpectedEqualToOutput);
//        return  network.validate(inputsValidate, expectsValidate, this::isExpectedEqualToOutput);
        return null;
    }

    public void train(@NonNull Long epoh) {
//        network.train(inputsTrain, expectsTrain, epoh);
    }

    @NonNull
    public abstract Boolean isExpectedEqualToOutput(@NonNull List<Double> expected,
                                                    @NonNull List<Double> output);

    public void loadData() {
//        loadData(datasetFileTrain, inputsTrain, expectsTrain);
//        loadData(datasetFileValidate, inputsValidate, expectsValidate);
    };

    public abstract void loadData(@NonNull String datasetFile,
                                  @NonNull List<List<Double>> inputs,
                                  @NonNull List<List<Double>> expects);
}
