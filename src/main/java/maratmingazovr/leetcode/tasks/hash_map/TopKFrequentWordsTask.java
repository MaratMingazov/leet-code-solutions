package maratmingazovr.leetcode.tasks.hash_map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// https://leetcode.com/problems/top-k-frequent-words/description/
public class TopKFrequentWordsTask {

    public List<String> topKFrequent(String[] words, int k) {

        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < words.length; i ++) {
            String word = words[i];
            if (map.containsKey(word)) {
                map.put(word, map.get(word) + 1);
            } else {
                map.put(word, 1);
            }
        }

        List<String> list = new ArrayList<>(map.keySet());
        Collections.sort(list, (a, b)-> {
            if (map.get(b).equals(map.get(a))) {
                return a.compareTo(b);
            }
            return Integer.compare(map.get(b), map.get(a));
        });
        return list.stream()
                   .limit(k)
                   .collect(Collectors.toList());
    }
}
