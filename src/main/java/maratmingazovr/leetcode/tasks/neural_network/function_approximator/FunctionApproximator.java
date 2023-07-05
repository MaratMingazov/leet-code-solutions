package maratmingazovr.leetcode.tasks.neural_network.function_approximator;

import maratmingazovr.leetcode.neural_network.MathUtil;

public class FunctionApproximator {

    public FunctionApproximator() {

    }

    public void calculateValues() {
        double x = MathUtil.round2digits(-1.0);
        double y;
        double z;

        while (x <= 1) {
            y = MathUtil.round2digits(-1.0);
            while (y <=1) {
                if (x*x + y*y <= 1
                        && x != 0.0 && x != 1.0 && x != -1.0
                        && y != 0.0 && y != 1.0 && y != -1.0) {
                    z = Math.sqrt(1 - x*x - y*y);
                    z = MathUtil.round2digits(z);
                    System.out.println(x + "," + y + "," + z);
                }
                y = MathUtil.round2digits(y + 0.1);
            }
            x = MathUtil.round2digits(x + 0.1);
        }
    }
}
