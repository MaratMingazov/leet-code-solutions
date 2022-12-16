package maratmingazovr.leetcode.neural_network;

import lombok.Data;
import lombok.NonNull;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Data
public class Network<T> {

    @NonNull
     List<Layer> layers = new ArrayList<>();

    @NonNull
    Double learningRate;

    @NonNull
    Logger log = LoggerFactory.getLogger(Network.class);

    public Network(@NonNull List<Integer> layerStructure,
                   @NonNull Double learningRate,
                   @NonNull ActivationFunction activationFunction) {

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
                                     activationFunction);
        layers.add(inputLayer);

        // hidden layers and output layer
        for (int layerId = 1; layerId < layerStructure.size(); layerId++) {
            Layer nextLayer = new Layer(layerId,
                                        layers.get(layerId - 1),
                                        layerStructure.get(layerId),
                                        List.of(),
                                        List.of(),
                                        activationFunction);
            layers.add(nextLayer);
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
            for (int i = 0; i < inputs.size(); i++) {
                val xs = inputs.get(i);
                val  ys = expecteds.get(i);
                calculateOutputs(xs);
                backpropagate(ys);
                updateWeights();
            }
//            log.info("totalError = " + layers.get(layers.size()-1).getLastLayerTotalError());
        }

    }

    public class Results {
        public final int correct;
        public final int trials;
        public final double percentage;

        public Results(int correct, int trials, double percentage) {
            this.correct = correct;
            this.trials = trials;
            this.percentage = percentage;
        }
    }

    // for generalized results that require classification
    // this function will return the correct number of trials
    // and the percentage correct out of the total
    public Results validate(List<List<Double>> inputs, List<T> expecteds, Function<List<Double>, T> interpret) {
        int correct = 0;
        for (int i = 0; i < inputs.size(); i++) {
            val input = inputs.get(i);
            T expected = expecteds.get(i);
            T result = interpret.apply(calculateOutputs(input));
            if (result.equals(expected)) {
                correct++;
            }
        }
        double percentage = (double) correct / (double) inputs.size();
        return new Results(correct, inputs.size(), percentage);
    }
}
