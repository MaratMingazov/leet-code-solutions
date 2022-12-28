package maratmingazovr.leetcode.neural_network;

import lombok.Data;
import lombok.NonNull;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Data
public class Network {

    @NonNull
     List<Layer> layers = new ArrayList<>();

    @NonNull
    Double learningRate;

    @NonNull
    Logger log = LoggerFactory.getLogger(Network.class);

    public Network(@NonNull List<Integer> layerStructure,
                   @NonNull Double learningRate,
                   @NonNull List<ActivationFunction> activationFunctions) {

        this(layerStructure, List.of(), learningRate, activationFunctions);
    }

    public Network(@NonNull NetworkConfiguration configuration) {
        this(
                configuration.getLayersStructure(),
                configuration.getLayersWeights(),
                configuration.getLearningRate(),
                configuration.getActivationFunctions()
            );
    }

    public Network(@NonNull List<Integer> layerStructure,
                   @NonNull List<List<Double>> layersWeights,
                   @NonNull Double learningRate,
                   @NonNull List<ActivationFunction> activationFunctions) {

        if (layerStructure.size() < 3) {
            throw new IllegalArgumentException("Error: Should be at least 3 layers (1 input, 1 hidden, 1 output).");
        }
        this.learningRate = learningRate;

        // input layer
        Layer inputLayer = new Layer(0,
                                     null,
                                     layerStructure.get(0),
                                     List.of(),
                                     List.of(),
                                     null);
        layers.add(inputLayer);
        int skip = 0;
        // hidden layers and output layer
        for (int layerId = 1; layerId < layerStructure.size(); layerId++) {
            val neuronsCount = layerStructure.get(layerId);
            List<List<Double>> neuronsWeights = new ArrayList<>();
            List<Double> neuronsBiases = new ArrayList<>();
            if (!layersWeights.isEmpty()) {
                calculateNeuronsWeightsAndBiases(layersWeights, neuronsWeights, neuronsBiases, skip, neuronsCount);
            }
            Layer layer = new Layer(layerId,
                                        layers.get(layerId - 1),
                                        layerStructure.get(layerId),
                                        neuronsWeights,
                                        neuronsBiases,
                                        activationFunctions.get(layerId-1));
            layers.add(layer);
            skip += neuronsCount;
        }
    }

    private void calculateNeuronsWeightsAndBiases(@NonNull List<List<Double>> layersWeights,
                                                  @NonNull List<List<Double>> neuronsWeights,
                                                  @NonNull List<Double> neuronsBiases,
                                                  @NonNull Integer skipNeuronsCount,
                                                  @NonNull Integer currentLayerNeuronsCount) {
        val sublist = layersWeights.subList(skipNeuronsCount, layersWeights.size());
        int index = 0;
        while(index < currentLayerNeuronsCount) {
            val neuronWeights = sublist.get(index);
            neuronsBiases.add(neuronWeights.get(neuronWeights.size()-1));
            neuronsWeights.add(neuronWeights.stream().limit(neuronWeights.size()-1).collect(Collectors.toList()));
            index++;
        }
    }

    // Pushes input data to the first layer, then output from the first
    // as input to the second, second to the third, etc.
    @NonNull
    public  List<Double> calculateOutputs(@NonNull List<Double> input) {
        var result = input;
        for (Layer layer : layers) {
            result = layer.calculateOutputs(result);
        }
        return result;
    }

    // Figure out each neuron's changes based on the errors of the output
    // versus the expected outcome
    @NonNull
    private void backpropagate(@NonNull List<Double> expected) {
        // calculate delta for output layer neurons
        int lastLayer = layers.size() - 1;
        layers.get(lastLayer).calculateDeltasForOutputLayer(expected);

        // calculate delta for hidden layers in reverse order
        for (int i = lastLayer - 1; i >= 0; i--) {
            layers.get(i).calculateDeltasForHiddenLayer(layers.get(i + 1));
        }
    }

    // backpropagate() doesn't actually change any weights
    // this function uses the deltas calculated in backpropagate() to
    // actually make changes to the weights
    private void updateWeights() {
        for (Layer layer : layers.subList(1, layers.size())) {
            for (Neuron neuron : layer.getNeurons()) {
                for (int w = 0; w < neuron.getWeights().size(); w++) {
                    val previousLayerOutput = layer.getPreviousLayer().getOutputCache().get(w);
                    val neuronWeights = neuron.getWeights();
                    val neuronNewWeight = neuronWeights.get(w)+ (learningRate * previousLayerOutput * neuron.delta);
                    neuronWeights.set(w, neuronNewWeight);
                    neuron.setBiasWeight(neuron.getBiasWeight() + (learningRate * neuron.delta));
                }
            }
        }
    }

    // train() uses the results of outputs() run over many inputs and compared
    // against expecteds to feed backpropagate() and updateWeights()
    public void train(@NonNull List<List<Double>> inputs,
                      @NonNull List<List<Double>> expecteds,
                      @NonNull Long epoh) {
        for (int e = 0; e < epoh; e++) {
            double allInputsError = 0;
            for (int i = 0; i < inputs.size(); i++) {
                val xs = inputs.get(i);
                val  ys = expecteds.get(i);
                calculateOutputs(xs);
                backpropagate(ys);
                updateWeights();
                allInputsError += layers.get(layers.size()-1).getLastLayerTotalError();
            }
            log.info("epoh =" + e + " / totalError = " + allInputsError);
        }

    }

    // for generalized results that require classification
    // this function will return the correct number of trials
    // and the percentage correct out of the total
    public ValidationResult validate(@NonNull List<List<Double>> inputs,
                                     @NonNull List<List<Double>> expects,
                                     @NonNull BiFunction<List<Double>, List<Double>, Boolean> interpret) {
        int correct = 0;
        for (int i = 0; i < inputs.size(); i++) {
            val input = inputs.get(i);
            val output = calculateOutputs(input);
            val expected = expects.get(i);
            val isEqual = interpret.apply(expected, output);
            if (isEqual) {
                correct++;
            }
        }
        double percentage = (double) correct / (double) inputs.size();
        log.info(correct + " correct of " + inputs.size() + " = " + percentage * 100 + "%");

        return new ValidationResult(correct, inputs.size(), percentage);
    }

    @NonNull
    public NetworkConfiguration getConfiguration() {
        return new NetworkConfiguration(this);
    }
}
