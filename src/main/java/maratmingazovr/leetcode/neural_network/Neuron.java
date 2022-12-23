package maratmingazovr.leetcode.neural_network;

import lombok.Data;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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

    @Nullable
    final ActivationFunction activationFunction;

    public Neuron(@NonNull Integer id,
                  @NonNull List<Double> weights,
                  @Nullable Double biasWeight,
                  @Nullable ActivationFunction activationFunction) {

        this.id = id;
        this.weights = weights;
        this.biasWeight = biasWeight;
        this.outputCache = 0.0;
        this.delta = 0.0;
        this.activationFunction = activationFunction;
    }


    @NonNull
    public Double calculateOutput(@NonNull List<Double> inputs) {
        if (biasWeight == null) {
            throw new IllegalArgumentException("Given neuron has no weights");
        }

        outputCache = Util.dotProduct(inputs, weights) + biasWeight;
        return Util.getActivationFunction(activationFunction).applyAsDouble(outputCache);
    }
}
