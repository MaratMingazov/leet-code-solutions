package maratmingazovr.leetcode.neural_network;

import lombok.NonNull;
import org.nd4j.linalg.api.buffer.DataType;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.util.List;

import static org.nd4j.linalg.factory.Nd4j.create;

public class NetworkM {

    @NonNull
    Integer inputNodesCount;
    @NonNull
    Integer hiddenNodesCount;
    @NonNull
    Integer outputNodesCount;
    @NonNull
    Double learningRate;

    @NonNull
    INDArray wih;

    @NonNull
    INDArray who;


    public NetworkM(@NonNull Integer inputNodesCount,
                    @NonNull Integer hiddenNodesCount,
                    @NonNull Integer outputNodesCount,
                    @NonNull Double learningRate) {

        this.inputNodesCount = inputNodesCount;
        this.hiddenNodesCount = hiddenNodesCount;
        this.outputNodesCount = outputNodesCount;
        this.learningRate = learningRate;

        wih = Nd4j.random().normal(0.0, Math.pow(hiddenNodesCount, -0.5), DataType.DOUBLE, hiddenNodesCount, inputNodesCount);
        who = Nd4j.random().normal(0.0, Math.pow(outputNodesCount, -0.5), DataType.DOUBLE, outputNodesCount, hiddenNodesCount);


    }

    public void train(@NonNull List<Double> input,
                      @NonNull List<Double> output) {

        double[] inputArray = input.stream().mapToDouble(Double::doubleValue).toArray();
        double[] outputArray = output.stream().mapToDouble(Double::doubleValue).toArray();
        try (INDArray inputDArray = create(inputArray, 1,input.size()).transpose();
             INDArray outputDArray = create(outputArray, 1, output.size()).transpose()) {

//            val hInputs = wih.mmul(inputDArray);
//            val hOutputs = Transforms.sigmoid(hInputs);
//            val hOutputs1 = Transforms.sigmoid(hInputs, false);
//            val hOutputs2 = Transforms.sigmoid(hInputs, true);


//            hOutputs = self.activation_function(hInputs)
//            oInputs = numpy.dot(self.who, hOutputs)
//            oOutputs = self.activation_function(oInputs)
//
//            oErrors = targets - oOutputs
//            hErrors = numpy.dot(self.who.T, oErrors)
//            self.who += self.learnRate * numpy.dot((oErrors * oOutputs * (1 - oOutputs)), numpy.transpose(hOutputs))
//            self.wih += self.learnRate * numpy.dot((hErrors * hOutputs * (1 - hOutputs)), numpy.transpose(inputs))

        }

    }
}
