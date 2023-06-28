package maratmingazovr.leetcode.neural_network_matrix;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

public class NeuralNetwork {

    double learnRate;
    RealMatrix wih;
    RealMatrix who;
    RealMatrix bih;
    RealMatrix bho;

    double totalError = 0;

    public NeuralNetwork(int inputNodes, int hiddenNodes, int outputNodes, double learnRate) {

        this.learnRate = learnRate;
        //wih = new double[hiddenNodes][inputNodes];
        wih = new Array2DRowRealMatrix(
                new double[][]{
                        new double[]{0.37, 0.48, 0.51},
                        new double[]{0.11, 0.83, 0.44},
                        new double[]{0.55, 0.63, 0.27}
                });
//        who = new double[outputNodes][hiddenNodes];
        who = new Array2DRowRealMatrix(
                new double[][]{
                        new double[]{0.37, 0.51, 0.14},
                        new double[]{0.11, 0.91, 0.57},
                        new double[]{0.07, 0.81, 0.36}
                });

//        this.hBiases = new double[hiddenNodes];
//        this.oBiases = new double[outputNodes];
        bih = new Array2DRowRealMatrix(
                new double[][]{
                        new double[]{0.31},
                        new double[]{0.93},
                        new double[]{0.47}
                });
        bho = new Array2DRowRealMatrix(
                new double[][]{
                        new double[]{0.11},
                        new double[]{0.25},
                        new double[]{0.36}
                });
//        randomize(this.wih);
//        randomize(this.who);
//        randomize(this.hBiases);
//        randomize(this.oBiases);

    }



    void train(double[] inputs, double[] targets) {

        RealMatrix input = new Array2DRowRealMatrix(inputs);
        RealMatrix target = new Array2DRowRealMatrix(targets);

        RealMatrix hInputs = wih.multiply(input).add(bih);
        RealMatrix hOutputs = activateSigmoid(hInputs);
        RealMatrix oInputs = who.multiply(hOutputs).add(bho);
        RealMatrix oOutputs = activateSigmoid(oInputs);
        RealMatrix oErrors = target.subtract(oOutputs);
        RealMatrix hErrors = who.transpose().multiply(oErrors);
        totalError = totalError(oErrors.getColumn(0));

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

    void train(double[][] inputs, double[][] targets, int epoh) {
        for (int ep = 0; ep < epoh; ep++) {
            for (int i = 0; i < inputs.length; i++) {
                train(inputs[i], targets[i]);
            }
            System.out.println("TotalError = " + this.totalError);
        }
    }




    RealMatrix activateSigmoid(RealMatrix matrix) {
        RealMatrix result = matrix.copy();
        for (int row = 0; row < matrix.getRowDimension(); row++) {
            for (int column = 0; column < matrix.getColumnDimension(); column++) {
                double value = matrix.getEntry(row, column);
                result.setEntry(row, column, 1 / (1 + Math.exp(-1 * value)));
            }
        }
        return result;
    }



    double totalError(double[] errors) {
        double result = 0;
        for (int i = 0; i < errors.length; i++) {
            result += Math.pow(errors[i], 2);
        }
        return result/2;
    }

    double[] multi (double[] vector1, double[] vector2) {
        double[] result = new double[vector1.length];
        for (int i = 0; i < vector1.length; i++) {
            result[i] = vector1[i] * vector2[i];
        }
        return result;
    }

    double[][] plus(double[][] matrix1, double[][] matrix2, double multiplicator)  {
        double[][] result = new double[matrix1.length][matrix1[0].length];
        for (int i = 0; i < matrix1.length; i++) {
            for(int j = 0; j < matrix1[i].length; j++) {
                result[i][j] = matrix1[i][j] + matrix2[i][j] * multiplicator;
            }
        }
        return result;
    }

    void randomize(double[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for(int j = 0; j < matrix[i].length; j++) {
                matrix[i][j] = Math.random();
            }
        }
    }

    void randomize(double[] vector) {
        for (int i = 0; i < vector.length; i++) {
            vector[i] = Math.random();
        }
    }

}
