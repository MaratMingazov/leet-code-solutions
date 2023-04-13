package maratmingazovr.leetcode.tasks.arrays;

// https://leetcode.com/problems/binary-search/description/

public class BinarySearchTask {

    public int search(int[] nums, int target) {
        return findIndex(nums, target, 0, nums.length-1);
    }

    public int findIndex(int[] nums, int target, int left, int right) {
        if (left >= right) {
            if (nums[left] == target) {
                return left;
            } else {
                return -1;
            }
        }

        int middle = left + (right-left)/2;
        if (nums[middle] == target) {
            return middle;
        } else if (nums[middle] > target) {
            right = middle-1;
        } else {
            left = middle+1;
        }
        return findIndex(nums, target, left, right);
    }
}
