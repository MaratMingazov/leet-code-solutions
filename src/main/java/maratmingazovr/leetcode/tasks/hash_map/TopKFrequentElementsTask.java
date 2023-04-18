package maratmingazovr.leetcode.tasks.hash_map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

// https://leetcode.com/problems/top-k-frequent-elements/description/
public class TopKFrequentElementsTask {

    public int[] topKFrequent(int[] nums, int k) {
        Map<Integer, Integer> map = new HashMap<>();

        for (int i = 0; i < nums.length; i++) {
            map.put(nums[i], map.getOrDefault(nums[i], 0) + 1);
        }

        Map<Integer, List<Integer>> tree = new TreeMap<>((a, b) -> b - a);

        for (int key: map.keySet()) {
            int value = map.get(key);
            List<Integer> keys = tree.getOrDefault(value, new ArrayList<>());
            keys.add(key);
            tree.put(value, keys);
        }

        List<Integer> result = new ArrayList();
        for (List<Integer> values : tree.values()) {
            for (Integer value: values) {
                if (result.size() < k) {
                    result.add(value);
                }
            }
        }

        return result.stream().mapToInt(a -> a).toArray();
    }
}
