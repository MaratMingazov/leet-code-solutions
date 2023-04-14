package maratmingazovr.leetcode.tasks.arrays;

public class FindMinimumInRotatedSortedArrayTask {

    public int findMin(int[] nums) {

        if (nums.length == 0) {
            return 0;
        }
        int min = nums[0];
        int left = 0;
        int right = nums.length - 1;


        while(left <= right) {
            int middle = left + (right - left)/2;
            if (nums[middle] < min) {
                min = nums[middle];
                right = middle - 1;
            } else {
                left = middle + 1;
            }
        }

        return min;

    }
}
