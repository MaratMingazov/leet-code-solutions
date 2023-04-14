package maratmingazovr.leetcode.tasks.arrays;

// https://leetcode.com/problems/search-in-rotated-sorted-array-ii/description/
public class SearchInRotatedSortedArray2Task {

    public boolean search(int[] nums, int target) {
        int left = 0;
        int right = nums.length-1;

        while (left <= right) {
            int middle = left + (right-left)/2;
            if (nums[middle] == target) {
                return true;
            }
            if (nums[middle] > nums[left]) {
                if (target >= nums[left] && target < nums[middle]) {
                    right = middle - 1;
                } else {
                    left = middle + 1;
                }
            } else if (nums[middle] < nums[left]) {
                if (target > nums[middle] && target <= nums[right]) {
                    left = middle + 1;
                } else {
                    right = middle - 1;
                }
            } else {
                left++;
            }
        }
        return false;

    }
}
