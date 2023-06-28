package maratmingazovr.leetcode.neural_network;

import lombok.NonNull;
import lombok.Value;
import lombok.val;
import maratmingazovr.leetcode.neural_network_matrix.NeuralNetworkMatrix;
import org.apache.commons.math3.linear.RealMatrix;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Value
public class NetworkConfiguration {

    @NonNull
    List<Integer> layersStructure;

    @NonNull
    List<ActivationFunction> activationFunctions;

    @NonNull
    Double learningRate;

    // last weight is bias weight
    @NonNull
    List<List<Double>> layersWeights;

    public NetworkConfiguration(@NonNull Network network) {
        layersStructure = network.getLayers().stream()
                                 .map(layer -> layer.getNeurons().size())
                                 .collect(Collectors.toList());
        activationFunctions = network.getLayers().stream()
                                     .skip(1)
                                     .map(Layer::getActivationFunction)
                                     .collect(Collectors.toList());
        this.learningRate = network.getLearningRate();
        layersWeights = new ArrayList<>();
        val layers = network.getLayers();
        for (Layer layer : layers.subList(1,layers.size())) {
            val neurons = layer.getNeurons();
            for (Neuron neuron : neurons) {
                List<Double> neuronWeights = new ArrayList<>(neuron.getWeights());
                neuronWeights.add(neuron.getBiasWeight());
                layersWeights.add(neuronWeights);
            }
        }
    }

    public NetworkConfiguration(@NonNull NeuralNetworkMatrix network) {
        layersStructure = Stream.of(network.getInputNodes(), network.getHiddenNodes(), network.getOutputNodes())
                                .collect(Collectors.toList());
        activationFunctions = Stream.of(network.getActivationFunctionHidden(), network.getActivationFunctionOutput())
                                    .collect(Collectors.toList());
        learningRate = network.getLearnRate();
        layersWeights = new ArrayList<>();
        RealMatrix wih = network.getWih();
        RealMatrix bih = network.getBih();
        for (int row = 0; row < wih.getRowDimension(); row++) {
            List<Double> rowWeights = new ArrayList<>();
            for (int col = 0; col < wih.getColumnDimension(); col++) {
                rowWeights.add(wih.getEntry(row, col));
            }
            rowWeights.add(bih.getEntry(row, 0));
            layersWeights.add(rowWeights);
        }
        RealMatrix who = network.getWho();
        RealMatrix bho = network.getBho();
        for (int row = 0; row < who.getRowDimension(); row++) {
            List<Double> rowWeights = new ArrayList<>();
            for (int col = 0; col < who.getColumnDimension(); col++) {
                rowWeights.add(who.getEntry(row, col));
            }
            rowWeights.add(bho.getEntry(row, 0));
            layersWeights.add(rowWeights);
        }
    }

    public NetworkConfiguration(@NonNull List<Integer> layersStructure,
                                @NonNull Double learningRate,
                                @NonNull List<ActivationFunction> activationFunctions,
                                @NonNull List<List<Double>> layersWeights) {
        this.layersStructure = layersStructure;
        this.learningRate = learningRate;
        this.activationFunctions = activationFunctions;
        this.layersWeights = layersWeights;
    }

    @NonNull
    public String getLayersStructureAsString() {
        val result = layersStructure.stream()
                                 .map(String::valueOf)
                                 .collect(Collectors.toList());
        return String.join(",", result);
    }

    @NonNull
    public String getLearningRateAsString() {
        return learningRate.toString();
    }

    public String getActivationFunctionsAsString() {
        val result = activationFunctions.stream()
                                    .map(Enum::name)
                                    .collect(Collectors.toList());
        return String.join(",", result);
    }

    @NonNull
    public List<String> getLayersWeightsAsString() {
        List<String> result = new ArrayList<>();
        for (List<Double> layersWeight : layersWeights) {
            val weightsString = layersWeight.stream()
                                             .map(String::valueOf)
                                             .collect(Collectors.toList());
            result.add(String.join(",", weightsString));
        }
        return result;
    }

}
