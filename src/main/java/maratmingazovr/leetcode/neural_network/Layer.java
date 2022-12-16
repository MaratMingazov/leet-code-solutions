package maratmingazovr.leetcode.neural_network;

import lombok.Data;
import lombok.NonNull;
import lombok.val;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.DoubleUnaryOperator;
import java.util.stream.Collectors;

@Data
public class Layer {

    @NonNull
    Integer id;

    // first layer has no previous layer
    @Nullable
    Layer previousLayer;

    @NonNull
    List<Neuron> neurons;

    @NonNull
    List<Double> outputCache;

    @NonNull
    Double lastLayerTotalError;

    @NonNull
    Random random = new Random();

    public Layer(@NonNull Integer id,
                 @Nullable Layer previousLayer,
                 @NonNull Integer neuronsCount,
                 @NonNull List<List<Double>> layerWeights,
                 @NonNull List<Double> layerBiasWeights,
                 @NonNull DoubleUnaryOperator activationFunction,
                 @NonNull DoubleUnaryOperator derivativeActivationFunction) {

        this.id = id;
        this.previousLayer = previousLayer;
        this.outputCache = new ArrayList<>();

        for (int neuronId = 0; neuronId < neuronsCount; neuronId++) {
            val neuronWeights = calculateNeuronWeights(layerWeights, neuronId, previousLayer);
            val biasWeight = calculateNeuronBiasWeight(layerBiasWeights, neuronId, previousLayer);
            Neuron neuron = new Neuron(neuronId,
                                       neuronWeights,
                                       biasWeight,
                                       activationFunction,
                                       derivativeActivationFunction);
            neurons.add(neuron);
        }
    }

    @NonNull
    private List<Double> calculateNeuronWeights(@NonNull List<List<Double>> layerWeights,
                                                @NonNull Integer neuronId,
                                                @Nullable Layer previousLayer) {
        if (!layerWeights.isEmpty()) {
            return layerWeights.get(neuronId);
        }
        if (previousLayer == null) {
            return List.of();
        }
        return random.doubles(previousLayer.getNeurons().size())
                     .boxed()
                     .collect(Collectors.toList());
    }

    @Nullable
    private Double calculateNeuronBiasWeight(@NonNull List<Double> layerBiasWeights,
                                             @NonNull Integer neuronId,
                                             @Nullable Layer previousLayer) {
        if (!layerBiasWeights.isEmpty()) {
            return layerBiasWeights.get(neuronId);
        }
        if (previousLayer == null) {
            return null;
        }
        return random.nextDouble();
    }



    @NonNull
    public List<Double> calculateOutputs(@NonNull List<Double> inputs) {
        if (previousLayer == null) {
            outputCache = inputs;
        } else {
            outputCache = neurons.stream()
                                 .mapToDouble(neuron -> neuron.calculateOutput(inputs))
                                 .boxed()
                                 .collect(Collectors.toList());
        }
        return outputCache;
    }

    // should only be called on output layer
    public void calculateDeltasForOutputLayer(@NonNull List<Double> expected) {
        double totalError = 0d;
        for (int i = 0; i < neurons.size(); i++) {
            val neuron = neurons.get(i);
            val derivative = neuron.derivativeActivationFunction.applyAsDouble(neuron.outputCache);
            neuron.delta = derivative  * (expected.get(i) - outputCache.get(i));
            totalError += Math.pow((expected.get(i) - outputCache.get(i)),2);
        }
        this.lastLayerTotalError = totalError;
    }

    // should not be called on output layer
    public void calculateDeltasForHiddenLayer(Layer nextLayer) {
        for (int i = 0; i < neurons.size(); i++) {
            int index = i;
            val nextWeights = nextLayer.getNeurons().stream()
                                               .map(n -> n.getWeights().get(index))
                                               .collect(Collectors.toList());
            val nextDeltas = nextLayer.neurons.stream()
                                              .map(Neuron::getDelta)
                                              .collect(Collectors.toList());
            double sumWeightsAndDeltas = Util.dotProduct(nextWeights, nextDeltas);
            val neuron = neurons.get(i);
            val derivative = neuron.derivativeActivationFunction.applyAsDouble(neuron.outputCache);
            neuron.delta = derivative * sumWeightsAndDeltas;
        }
    }
}
