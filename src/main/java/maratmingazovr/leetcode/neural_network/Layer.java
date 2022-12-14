package maratmingazovr.leetcode.neural_network;

import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.DoubleUnaryOperator;

public class Layer {

    // first layer has no previous layer
    @Nullable
    public Layer previousLayer;

    @NonNull
    public List<Neuron> neurons = new ArrayList<>();


    public double[] outputCache;

    public Layer(@Nullable Layer previousLayer,
                 int numNeurons, double learningRate,
                 @NonNull DoubleUnaryOperator activationFunction,
                 @NonNull DoubleUnaryOperator derivativeActivationFunction) {
        this.previousLayer = previousLayer;
        Random random = new Random();
        for (int i = 0; i < numNeurons; i++) {
            double[] randomWeights = null;
            if (previousLayer != null) {
                randomWeights = random.doubles(previousLayer.neurons.size()).toArray();
            }
            Neuron neuron = new Neuron(randomWeights, learningRate, activationFunction, derivativeActivationFunction);
            neurons.add(neuron);
        }
        outputCache = new double[numNeurons];
    }

    public double[] outputs(double[] inputs) {
        if (previousLayer != null) {
            outputCache = neurons.stream().mapToDouble(n -> n.output(inputs)).toArray();
        } else {
            outputCache = inputs;
        }
        return outputCache;
    }

    // should only be called on output layer
    public void calculateDeltasForOutputLayer(double[] expected) {
        for (int n = 0; n < neurons.size(); n++) {
            neurons.get(n).delta = neurons.get(n).derivativeActivationFunction.applyAsDouble(neurons.get(n).outputCache)
                    * (expected[n] - outputCache[n]);
        }
    }

    // should not be called on output layer
    public void calculateDeltasForHiddenLayer(Layer nextLayer) {
        for (int i = 0; i < neurons.size(); i++) {
            int index = i;
            double[] nextWeights = nextLayer.neurons.stream().mapToDouble(n -> n.weights[index]).toArray();
            double[] nextDeltas = nextLayer.neurons.stream().mapToDouble(n -> n.delta).toArray();
            double sumWeightsAndDeltas = Util.dotProduct(nextWeights, nextDeltas);
            neurons.get(i).delta = neurons.get(i).derivativeActivationFunction
                    .applyAsDouble(neurons.get(i).outputCache) * sumWeightsAndDeltas;
        }
    }
}
