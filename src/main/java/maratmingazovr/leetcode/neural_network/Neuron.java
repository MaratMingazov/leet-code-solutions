package maratmingazovr.leetcode.neural_network;

import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.DoubleUnaryOperator;

public class Neuron {

    // First layer neuron has no weights
    @Nullable
    List<Double> weights;
    @Nullable
    Double biasWeight;

    @NonNull
    Double learningRate;

    @NonNull
    Double outputCache;

    @NonNull
    Double delta;

    @NonNull
    final DoubleUnaryOperator activationFunction;

    @NonNull
    final DoubleUnaryOperator derivativeActivationFunction;

    public Neuron(@Nullable List<Double> weights,
                  @Nullable Double biasWeight,
                  @NonNull Double learningRate,
                  @NonNull DoubleUnaryOperator activationFunction,
                  @NonNull DoubleUnaryOperator derivativeActivationFunction) {
        this.weights = weights;
        this.biasWeight = biasWeight;
        this.learningRate = learningRate;
        this.outputCache = 0.0;
        this.delta = 0.0;
        this.activationFunction = activationFunction;
        this.derivativeActivationFunction = derivativeActivationFunction;
    }


    @NonNull
    public Double calculateOutput(@NonNull List<Double> inputs) {
        if (weights == null || biasWeight == null) {
            throw new IllegalArgumentException("Given neuron has no weights");
        }

        outputCache = Util.dotProduct(inputs, weights) + biasWeight;
        return activationFunction.applyAsDouble(outputCache);
    }
}
