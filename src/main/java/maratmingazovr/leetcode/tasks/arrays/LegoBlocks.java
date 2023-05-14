package maratmingazovr.leetcode.tasks.arrays;

public class LegoBlocks {

    public int legoBlocks(int height, int width) {

        int divisor = (int)Math.pow(10,9) + 7;
        long[] widthCombinations = new long[width];
        for (int i = 0; i < width; i++) {
            if (i == 0) {
                widthCombinations[0] = 1;
            } else if (i == 1) {
                widthCombinations[1] = 2;
            }else if (i == 2) {
                widthCombinations[2] = 4;
            }else if (i == 3) {
                widthCombinations[3] = 8;
            } else {
                widthCombinations[i] = (widthCombinations[i-1]
                        + widthCombinations[i-2]
                        + widthCombinations[i-3]
                        + widthCombinations[i-4])
                        % divisor;
            }
        }

        long[] allCombinations = new long[width];
        for (int i = 0; i < width; i++) {
            long times = height;
            long result = 1;
            while (times > 0) {
                result = (result * widthCombinations[i]) % divisor;
                times--;
            }
            allCombinations[i] = result;
        }

        long[] goodCombinations = new long[width];
        goodCombinations[0] = 1;
        for (int i = 1; i < width; i++) {
            long goodCombination = allCombinations[i];
            for (int j = 0; j < i; j++) {
                goodCombination -= goodCombinations[j] * allCombinations[i-j-1] % divisor;
            }
            while(goodCombination < 0) {
                goodCombination += divisor;
            }
            goodCombinations[i] = goodCombination;
        }
        return (int)goodCombinations[width-1];
    }
}
