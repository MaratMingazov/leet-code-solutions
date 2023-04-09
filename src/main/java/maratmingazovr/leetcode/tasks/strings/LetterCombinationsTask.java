package maratmingazovr.leetcode.tasks.strings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//https://leetcode.com/problems/letter-combinations-of-a-phone-number/

public class LetterCombinationsTask {

    public List<String> letterCombinations(String digits) {

        Map<Character, String> map = new HashMap<>();
        map.put('2', "abc");
        map.put('3', "def");
        map.put('4', "ghi");
        map.put('5', "jkl");
        map.put('6', "mno");
        map.put('7', "pqrs");
        map.put('8', "tuv");
        map.put('9', "wxyz");

        List<String> words = new ArrayList<>();
        for (int i = 0; i < digits.length(); i++) {
            char ch =  digits.charAt(i);
            if (map.containsKey(ch)) {
                words.add(map.get(ch));
            }
        }

        return getCombinations("", 0, words);

    }



    public List<String> getCombinations(String str, int index, List<String> words) {

        List<String> result = new ArrayList<>();
        if (index >= words.size()) {
            if (str.length() > 0) {
                result.add(str);
            }
            return result;
        }

        String word = words.get(index);
        for (int i = 0; i < word.length(); i++) {
            String newStr = str + String.valueOf(word.charAt(i));
            result.addAll(getCombinations(newStr, index+1, words));
        }
        return result;
    }

}
