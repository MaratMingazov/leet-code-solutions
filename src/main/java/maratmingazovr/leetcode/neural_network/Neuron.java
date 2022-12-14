package maratmingazovr.leetcode.neural_network;

import lombok.Data;
import lombok.NonNull;

import java.util.function.DoubleUnaryOperator;

@Data
public class Neuron {

    double[] weights;
    final double learningRate;
    double outputCache;
    double delta;
    final DoubleUnaryOperator activationFunction;
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

    public double output(double[] inputs) {
        outputCache = Util.dotProduct(inputs, weights);
        return activationFunction.applyAsDouble(outputCache);
    }
}
