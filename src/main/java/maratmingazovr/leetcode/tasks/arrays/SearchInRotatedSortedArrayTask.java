package maratmingazovr.leetcode.tasks.arrays;

// https://leetcode.com/problems/search-in-rotated-sorted-array/description/
public class SearchInRotatedSortedArrayTask {

    public int search(int[] nums, int target) {
        if (nums.length == 0) {
            return -1;
        }
        int left = 0;
        int right = nums.length-1;

        while (left <= right) {
            int middle = left + (right-left)/2;
            if (nums[middle] == target) {
                return middle;
            }
            if (nums[middle] >= nums[left]) {
                if(nums[middle] > target && nums[left] <= target) {
                    right = middle - 1;
                } else {
                    left = middle + 1;
                }
            } else {
                if (nums[middle] < target && nums[right] >= target ) {
                    left = middle + 1;
                } else {
                    right = middle - 1;
                }
            }
        }
        return -1;
    }
}
