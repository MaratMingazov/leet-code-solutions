package maratmingazovr.leetcode.tasks.neural_network;

import lombok.Data;
import lombok.NonNull;
import maratmingazovr.leetcode.neural_network.Network;
import maratmingazovr.leetcode.neural_network.Util;
import maratmingazovr.leetcode.neural_network.ValidationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@Data
public abstract class AbstractClassificator {

    @NonNull
    protected final String datasetFileTrain;

    @NonNull
    protected final String datasetFileValidate;
    @NonNull
    protected final String configurationFile;
    @NonNull
    protected List<List<Double>> inputsTrain = new ArrayList<>();
    @NonNull
    protected List<List<Double>> expectsTrain = new ArrayList<>();
    @NonNull
    protected List<List<Double>> inputsValidate = new ArrayList<>();
    @NonNull
    protected List<List<Double>> expectsValidate = new ArrayList<>();

    @NonNull Logger log = LoggerFactory.getLogger(Network.class);

    protected Network network;

    protected AbstractClassificator(@NonNull String datasetFileTrain,
                                    @NonNull String datasetFileValidate,
                                    @NonNull String configurationFile) {
        this.datasetFileTrain = datasetFileTrain;
        this.datasetFileValidate = datasetFileValidate;
        this.configurationFile = configurationFile;
    }

    public void loadNetwork() {
        network = new Network(Util.loadNetworkConfiguration(configurationFile));
    }

    public abstract void createDefaultNetworkAndTrain();

    public void saveNetworkConfiguration() {
        Util.saveNetworkConfiguration(configurationFile, network.getConfiguration());
    }

    public ValidationResult validate() {
        network.validate(inputsTrain, expectsTrain, this::isExpectedEqualToOutput);
        return  network.validate(inputsValidate, expectsValidate, this::isExpectedEqualToOutput);
    }

    public void train(@NonNull Long epoh) {
        network.train(inputsTrain, expectsTrain, epoh);
    }

    @NonNull
    public abstract Boolean isExpectedEqualToOutput(@NonNull List<Double> expected,
                                                    @NonNull List<Double> output);

    public void loadData() {
        loadData(datasetFileTrain, inputsTrain, expectsTrain);
        loadData(datasetFileValidate, inputsValidate, expectsValidate);
    };

    public abstract void loadData(@NonNull String datasetFile,
                                  @NonNull List<List<Double>> inputs,
                                  @NonNull List<List<Double>> expects);
}
