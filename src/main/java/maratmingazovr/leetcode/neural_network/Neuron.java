package maratmingazovr.leetcode.neural_network;

import lombok.NonNull;

import java.util.function.DoubleUnaryOperator;

public class Neuron {

    // First layer neuron has no weights
    public double[] weights;
    public final double learningRate;
    public double outputCache;
    public double delta;

    @NonNull
    final DoubleUnaryOperator activationFunction;

    @NonNull
    final DoubleUnaryOperator derivativeActivationFunction;

    public Neuron(double[] weights,
                  double learningRate,
                  @NonNull DoubleUnaryOperator activationFunction,
                  @NonNull DoubleUnaryOperator derivativeActivationFunction) {
        this.weights = weights;
        this.learningRate = learningRate;
        outputCache = 0.0;
        delta = 0.0;
        this.activationFunction = activationFunction;
        this.derivativeActivationFunction = derivativeActivationFunction;
    }

    public double calculateOutput(double[] inputs) {
        outputCache = Util.dotProduct(inputs, weights);
        return activationFunction.applyAsDouble(outputCache);
    }
}
