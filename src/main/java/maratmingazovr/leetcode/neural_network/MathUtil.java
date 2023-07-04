package maratmingazovr.leetcode.neural_network;

public class MathUtil {

    public static double round2digits(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    /**
     * [5, 10, 4] -> [7.3433, 9.53453, 5.002, 9.645645]
     *
     */
    public static double[] generateUniformDistribution(int startValue, int finishValue, int count) {
        double[] result = new double[count];
        for (int i = 0; i < count; i++) {
            result[i] = (finishValue - startValue) * Math.random() + startValue;
        }
        return result;
    }

    public static double generateNormalDistribution(double mean, double deviation) {
        double s = 0;
        double x = 0;
        double y = 0;
        double[] values;
        while (s == 0 || s > 1) {
            values = generateUniformDistribution(-1, 1, 2);
            x = values[0];
            y = values[1];
            s = x*x+y*y;
        }
        return x*Math.sqrt((-2*Math.log(s))/(s));
    }

    public static double[] generateNormalDistribution(double mean, double deviation, int count) {
        double[] result = new double[count];
        for (int i = 0; i < count; i++) {
            result[i] = generateNormalDistribution(mean, deviation);
        }
        return convertToNormalDistribution(result, mean, deviation);
    }

    public static double[] round2Digits(double[] values) {
        double[] result = new double[values.length];
        for (int i = 0; i < values.length; i++) {
            result[i] = Math.round(values[i] * 100.0) / 100.0;
        }
        return result;
    }

    /**
     * Преобразует значения нормального распределения в Z показатели
     * [1,2,3,4,5] -> [-1.4142,-0.7071,0.0,0.7071.4142]
     * @return
     */
    public static double[] convertToZValues(double[] normalDistributionValues) {
        double[] zValues = new double[normalDistributionValues.length];
        double mean = mean(normalDistributionValues);
        double standardDeviation = standardDeviation(normalDistributionValues);
        for (int i = 0; i < normalDistributionValues.length; i++) {
            zValues[i] = (normalDistributionValues[i] - mean) / standardDeviation;
        }
        return zValues;
    }

    /**
     * Преобразует значения Z показателей в нормальное арспределение
     * с матиматическим ожиданием Mean
     * и стандартным отклонение standardDeviation
     */
    public static double[] convertToNormalDistribution(double[] zValues, double mean, double standardDeviation) {
        double[] values = new double[zValues.length];
        for (int i = 0; i < zValues.length; i++) {
            values[i] = zValues[i] * standardDeviation + mean;
        }
        return values;
    }

    /**
     * Вычисляет среднее арифметическое
     * [1,2,3,4,5] -> 3
     */
    public static double mean(double[] values) {
        double sum = 0.0;
        for (double value : values) {
            sum += value;
        }
        return sum / values.length;
    }

    /**
     * Вычисляет стандартное отклонение
     * [1,2,3,4,5], 3 -> 1.4142
     */
    public static double standardDeviation(double[] values) {
        double sum = 0.0;
        double mean = mean(values);
        for (double value : values) {
            sum += Math.pow((value - mean), 2);
        }
        return Math.sqrt(sum/values.length);
    }
}
