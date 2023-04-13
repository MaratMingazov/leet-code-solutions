package maratmingazovr.leetcode.tasks.arrays;

// https://leetcode.com/problems/guess-number-higher-or-lower/description/

public class GuessNumberTask {

    public int guessNumber(int n) {
        int left = 1;
        int right = n;
        int middle = n/2;
        int result = guess(middle);

        while (result != 0) {
            if (result == 1) {
                left = middle + 1;
            } else {
                right = middle - 1;
            }
            middle = left + (right - left) / 2;
            result = guess(middle);
        }
        return middle;
    }

    public int guess(int num) {
        if (num == 6) {
            return 0;
        }
        if (num < 6) {
            return 1;
        }
        return -1;
    }
}
