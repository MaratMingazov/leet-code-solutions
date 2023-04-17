package maratmingazovr.leetcode.tasks.hash_map;

import java.util.ArrayList;
import java.util.List;

// https://leetcode.com/problems/valid-anagram/description/
public class ValidAnagramTask {

    public boolean isAnagram(String s, String t) {

        List<Character> first = new ArrayList<>();
        for (int i = 0; i < s.length(); i++) {
            first.add(s.charAt(i));
        }
        for (int i = 0; i < t.length(); i++) {
            char ch = t.charAt(i);
            if (first.contains(ch)) {
                first.remove(Character.valueOf(ch));
            } else {
                return false;
            }
        }
        return first.isEmpty();
    }
}
