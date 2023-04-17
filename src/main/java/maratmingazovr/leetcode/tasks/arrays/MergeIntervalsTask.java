package maratmingazovr.leetcode.tasks.arrays;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

// https://leetcode.com/problems/merge-intervals/description/

public class MergeIntervalsTask {

    public int[][] merge(int[][] intervals) {

        Comparator<int[]> comparator = (a, b) -> {
            if (a[0] == b[0]) {
                return Integer.compare(a[1], b[1]);
            }
            return Integer.compare(a[0], b[0]);
        };

        Arrays.sort(intervals, comparator);

        List<int[]> list = new ArrayList<>();
        if (intervals.length < 1) {
            return new int[0][0];
        }
        list.add(intervals[0]);
        int index = 0;

        for (int i = 1; i < intervals.length; i++) {
            int[] interval = intervals[i];
            if (interval[0] <= list.get(index)[1]) {
                list.get(index)[1] = Integer.max(list.get(index)[1], interval[1]);
            } else {
                list.add(interval);
                index++;
            }
        }

        int[][] result = new int[list.size()][2];
        for (int i = 0; i < list.size(); i++) {
            result[i] = list.get(i);
        }
        return result;
    }
}
