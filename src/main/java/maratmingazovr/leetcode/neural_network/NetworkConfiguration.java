package maratmingazovr.leetcode.neural_network;

import lombok.NonNull;
import lombok.Value;
import lombok.val;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Value
public class NetworkConfiguration {

    @NonNull
    List<Integer> layersStructure;

    @NonNull
    List<ActivationFunction> activationFunctions;

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

    public NetworkConfiguration(@NonNull List<Integer> layersStructure,
                                @NonNull List<ActivationFunction> activationFunctions,
                                @NonNull List<List<Double>> layersWeights) {
        this.layersStructure = layersStructure;
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
