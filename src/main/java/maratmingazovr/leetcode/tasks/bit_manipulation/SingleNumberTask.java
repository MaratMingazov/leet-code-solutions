package maratmingazovr.leetcode.tasks.bit_manipulation;


// https://leetcode.com/problems/single-number/description/
public class SingleNumberTask {

    public int singleNumber(int[] nums) {
        int result = 0;
        for (int i = 0; i < nums.length; i++) {
            result ^= nums[i];
        }
        return result;
    }
}
