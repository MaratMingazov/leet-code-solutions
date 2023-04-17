package maratmingazovr.leetcode.tasks.hash_map;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// https://leetcode.com/problems/group-anagrams/description/
public class GroupAnagramsTask {

    public List<List<String>> groupAnagrams(String[] strs) {

        Map<String, List<String>> map = new HashMap<>();
        for (int i = 0; i < strs.length; i++) {
            String str = strs[i];
            char[] chrs = str.toCharArray();
            Arrays.sort(chrs);
            String newStr = new String(chrs);
            if (!map.containsKey(newStr)) {
                map.put(newStr, new ArrayList<>());
            }
            map.get(newStr).add(str);
        }
        return map.values().stream().collect(Collectors.toList());
    }
}
