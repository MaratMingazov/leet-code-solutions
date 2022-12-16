package maratmingazovr.leetcode.neural_network;

import lombok.Data;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.DoubleUnaryOperator;

@Data
public class Neuron {

    @NonNull
    Integer id;

    // First layer neuron has no weights
    @NonNull
    List<Double> weights;
    @Nullable
    Double biasWeight;

    @NonNull
    Double outputCache;

    @NonNull
    Double delta;

    @NonNull
    final DoubleUnaryOperator activationFunction;

    @NonNull
    final DoubleUnaryOperator derivativeActivationFunction;

    public Neuron(@NonNull Integer id,
                  @NonNull List<Double> weights,
                  @Nullable Double biasWeight,
                  @NonNull DoubleUnaryOperator activationFunction,
                  @NonNull DoubleUnaryOperator derivativeActivationFunction) {

        this.id = id;
        this.weights = weights;
        this.biasWeight = biasWeight;
        this.outputCache = 0.0;
        this.delta = 0.0;
        this.activationFunction = activationFunction;
        this.derivativeActivationFunction = derivativeActivationFunction;
    }


    @NonNull
    public Double calculateOutput(@NonNull List<Double> inputs) {
        if (biasWeight == null) {
            throw new IllegalArgumentException("Given neuron has no weights");
        }

        outputCache = Util.dotProduct(inputs, weights) + biasWeight;
        return activationFunction.applyAsDouble(outputCache);
    }
}
