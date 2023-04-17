package maratmingazovr.leetcode.tasks.hash_map;

import java.util.ArrayList;
import java.util.List;

// https://leetcode.com/problems/find-all-anagrams-in-a-string/description/
public class FindAllAnagramsTask {

    public List<Integer> findAnagrams(String s, String p) {

        List<String> first = new ArrayList<>();
        List<String> second = new ArrayList<>();
        for (int i = 0; i < p.length(); i++) {
            second.add(String.valueOf(p.charAt(i)));
        }

        int index = 0;
        List<Integer> result = new ArrayList<>();

        for (int i = 0; i < s.length(); i++) {

            if (second.isEmpty()) {
                result.add(index);
                index++;
                second.add(first.get(0));
                first.remove(0);
            }

            String str = String.valueOf(s.charAt(i));
            if (second.contains(str)) {
                first.add(str);
                second.remove(str);
            } else {
                index++;
                if (!first.isEmpty()) {
                    second.add(first.get(0));
                    first.remove(0);
                    i--;
                }
            }
        }
        if (second.isEmpty()) {
            result.add(index);
        }
        return result;
    }
}
