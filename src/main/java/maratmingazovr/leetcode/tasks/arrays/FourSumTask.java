package maratmingazovr.leetcode.tasks.arrays;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//https://leetcode.com/problems/4sum/description/
public class FourSumTask {

    public List<List<Integer>> fourSum(int[] nums, int target) {

        List<List<Integer>> result = new ArrayList<>();

        Arrays.sort(nums);
        if (nums.length < 4) {
            return result;
        }


        for (int i = 0; i < nums.length-3; i++) {
            for (int j = nums.length-1; j >= 3; j--) {
                int left = i + 1;
                int right = j - 1;
                while (left < right) {
                    long sum = (long)nums[i] + (long)nums[left] + (long)nums[right] + (long)nums[j];
                    if (sum == target) {
                        List<Integer> current = new ArrayList<>(Arrays.asList(nums[i], nums[left], nums[right], nums[j]));
                        if (!isContains(current, result)) {
                            result.add(current);
                        }
                        left++;
                    } else if (sum > target) {
                        right--;
                    } else {
                        left++;
                    }
                }
            }
        }
        return result;
    }

    private boolean isContains(List<Integer> current, List<List<Integer>> result) {
        for (List<Integer> list : result) {
            if (current.equals(list)) {
                return true;
            }
        }
        return false;
    }
}
