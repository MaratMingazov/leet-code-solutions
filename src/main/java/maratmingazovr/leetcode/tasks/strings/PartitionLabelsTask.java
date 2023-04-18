package maratmingazovr.leetcode.tasks.strings;

import java.util.ArrayList;
import java.util.List;

// https://leetcode.com/problems/partition-labels/description/
public class PartitionLabelsTask {

    public List<Integer> partitionLabels(String s) {

        List<Integer> partitionIndex = new ArrayList<>();
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (ch == '_') {
                continue;
            }
            if (!s.substring(i,s.length()).contains("_")) {
                partitionIndex.add(i);
            }
            s = s.replaceAll(Character.toString(ch), "_");
        }
        partitionIndex.add(s.length());
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < partitionIndex.size() - 1; i++) {
            result.add(partitionIndex.get(i+1) - partitionIndex.get(i));
        }
        return result;


    }
}
