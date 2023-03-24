package maratmingazovr.leetcode.tasks.strings;

import java.util.HashSet;
import java.util.Set;

/**
 * <a href="https://leetcode.com/problems/longest-substring-without-repeating-characters/">description</a>
 * Given a string s, find the length of the longest
 * substring
 *  without repeating characters.
 */
public class LongestSubstringTask {

    /**
     * Time complexity: O(n^2)
     * @param s
     * @return
     */
    public int lengthOfLongestSubstring(String s) {
        int max = 0;

        for (int i = 0; i < s.length(); i ++) {
            int len = getLength(i, s);
            if (len > max) {
                max = len;
            }
        }
        return max;
    }

    private int getLength(int startIndex, String s) {
        Set<Integer> chars = new HashSet<>();
        for (int i = startIndex; i < s.length(); i ++) {
            int ch = s.charAt(i);
            if (chars.contains(ch)) {
                return chars.size();
            } else {
                chars.add(ch);
            }
        }
        return chars.size();
    }

    /**
     * Time complexity: O(n)
     * @param s
     * @return
     */
    public int lengthOfLongestSubstringLinear(String s) {
        Set<Character> set = new HashSet<>();
        int maxLength = 0;
        int left = 0;
        for (int right = 0; right < s.length(); right++) {
            char ch = s.charAt(right);
            if (!set.contains(ch)) {
                set.add(ch);
                maxLength = Math.max(maxLength, right - left + 1);
            } else {
                while(s.charAt(left) != ch) {
                    set.remove(s.charAt(left));
                    left++;
                }
                set.remove(s.charAt(left));
                left++;
                set.add(ch);
            }
        }
        return maxLength;
    }

    public int lengthOfLongestSubstring3(String s) {
        Set<Character>set=new HashSet<>();
        int maxLength=0;
        int left=0;
        for(int right=0;right<s.length();right++){

            if(!set.contains(s.charAt(right))){
                set.add(s.charAt(right));
                maxLength=Math.max(maxLength,right-left+1);

            }else{
                while(s.charAt(left)!=s.charAt(right)){
                    set.remove(s.charAt(left));
                    left++;
                }
                set.remove(s.charAt(left));left++;
                set.add(s.charAt(right));
            }

        }
        return maxLength;
    }
}
