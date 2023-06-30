package maratmingazovr.leetcode.neural_network_matrix;

import lombok.Data;
import lombok.NonNull;
import lombok.val;
import maratmingazovr.leetcode.neural_network.ActivationFunction;
import maratmingazovr.leetcode.neural_network.Network;
import maratmingazovr.leetcode.neural_network.Util;
import maratmingazovr.leetcode.neural_network.ValidationResult;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.BiFunction;

import static maratmingazovr.leetcode.neural_network.Util.randomize;

@Data
public class NeuralNetworkMatrix {

    int inputNodes, hiddenNodes, outputNodes;
    double learnRate;

    ActivationFunction activationFunctionHidden;
    ActivationFunction activationFunctionOutput;
    RealMatrix wih, who;
    RealMatrix bih, bho;

    double[][] totalErrors;

    RealMatrix hInputs, hOutputs;
    RealMatrix oInputs, oOutputs;
    RealMatrix hErrors, oErrors;

    Logger log = LoggerFactory.getLogger(NeuralNetworkMatrix.class);

    public NeuralNetworkMatrix(int inputNodes, int hiddenNodes, int outputNodes, double learnRate,
                               ActivationFunction activationFunctionHidden,
                               ActivationFunction activationFunctionOutput,
                               double[][] wih, double[] bih, double[][] who, double[] bho) {

        init(inputNodes, hiddenNodes, outputNodes, learnRate,
             activationFunctionHidden, activationFunctionOutput, wih, bih, who, bho);

    }

    private void init(int inputNodes, int hiddenNodes, int outputNodes, double learnRate,
                 ActivationFunction activationFunctionHidden,
                 ActivationFunction activationFunctionOutput,
                 double[][] wih, double[] bih, double[][] who, double[] bho) {

        this.inputNodes = inputNodes;
        this.hiddenNodes = hiddenNodes;
        this.outputNodes = outputNodes;
        this.learnRate = learnRate;
        this.activationFunctionHidden = activationFunctionHidden;
        this.activationFunctionOutput = activationFunctionOutput;

        this.wih = new Array2DRowRealMatrix(wih);
        this.who = new Array2DRowRealMatrix(who);
        this.bih = new Array2DRowRealMatrix(bih);
        this.bho = new Array2DRowRealMatrix(bho);

    }

    public NeuralNetworkMatrix(int inputNodes, int hiddenNodes, int outputNodes, double learnRate,
                               ActivationFunction activationFunctionHidden,
                               ActivationFunction activationFunctionOutput) {

        double[][] wih = new double[hiddenNodes][inputNodes];
        double[][] who = new double[outputNodes][hiddenNodes];
        double[] bih = new double[hiddenNodes];
        double[] bho = new double[outputNodes];

        randomize(wih); randomize(who);
        randomize(bih); randomize(bho);

        init(inputNodes, hiddenNodes, outputNodes, learnRate,
             activationFunctionHidden, activationFunctionOutput, wih, bih, who, bho);

    }



    double[] propogate(double[] inputs) {

        RealMatrix input = new Array2DRowRealMatrix(inputs);

        RealMatrix hInputs = wih.multiply(input).add(bih);
        hOutputs = Util.getActivationFunctionMatrix(activationFunctionHidden).apply(hInputs);
        RealMatrix oInputs = who.multiply(hOutputs).add(bho);
        oOutputs = Util.getActivationFunctionMatrix(activationFunctionHidden).apply(oInputs);

        return oOutputs.getColumn(0);
    }

    void calculateErrors(double[] targets) {
        RealMatrix target = new Array2DRowRealMatrix(targets);
        oErrors = target.subtract(oOutputs);
        hErrors = who.transpose().multiply(oErrors);
    }

    void updateWeights(double[] inputs) {
        RealMatrix input = new Array2DRowRealMatrix(inputs);
        double[] deltaho = oErrors.getColumnVector(0)
                                  .ebeMultiply(oOutputs.getColumnVector(0))
                                  .ebeMultiply(oOutputs.getColumnVector(0).mapMultiply(-1).mapAdd(1d))
                                  .mapMultiply(learnRate)
                                  .toArray();
        who = who.add(new Array2DRowRealMatrix(deltaho).multiply(hOutputs.transpose()));
        bho = bho.add(new Array2DRowRealMatrix(deltaho));

        double[] deltaih = hErrors.getColumnVector(0)
                                  .ebeMultiply(hOutputs.getColumnVector(0))
                                  .ebeMultiply(hOutputs.getColumnVector(0).mapMultiply(-1).mapAdd(1d))
                                  .mapMultiply(learnRate)
                                  .toArray();
        wih = wih.add(new Array2DRowRealMatrix(deltaih).multiply(input.transpose()));
        bih = bih.add(new Array2DRowRealMatrix(deltaih));
    }

    public void train(double[][] inputs, double[][] targets, int epoh) {
        totalErrors = new double[epoh][inputs.length];
        for (int ep = 0; ep < epoh; ep++) {
            for (int i = 0; i < inputs.length; i++) {
                propogate(inputs[i]);
                calculateErrors(targets[i]);
                totalErrors[ep][i] = totalError(oErrors.getColumn(0));
                updateWeights(inputs[i]);
            }
            System.out.println("epoh=" + ep + " / totalError=" + Util.getMedian(totalErrors[ep]));
        }
    }

    public ValidationResult validate(double[][] inputs,
                                     double[][] targets,
                                     BiFunction<double[], double[], Boolean> interpret) {
        int correct = 0;
        for (int i = 0; i < inputs.length; i++) {
            double[] input = inputs[i];
            double[] actual = propogate(input);
            double[] target = targets[i];
            boolean isEqual = interpret.apply(target, actual);
            if (isEqual) {
                correct++;
            }
        }
        double percentage = (double) correct / (double) inputs.length;
        log.info(correct + " correct of " + inputs.length + " = " + percentage * 100 + "%");

        return new ValidationResult(correct, inputs.length, percentage);
    }



    double totalError(double[] errors) {
        double result = 0;
        for (int i = 0; i < errors.length; i++) {
            result += Math.pow(errors[i], 2);
        }
        return result/2;
    }

}
