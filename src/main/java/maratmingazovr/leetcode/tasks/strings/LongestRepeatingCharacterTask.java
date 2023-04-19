package maratmingazovr.leetcode.tasks.strings;

public class LongestRepeatingCharacterTask {

    public int characterReplacement(String s, int k) {

        int[] array = new int[26];
        int start = 0;
        int maxCount = 0;
        int ans = 0;
        for (int end = 0;end < s.length();end++) {
            char ch = s.charAt(end);
            array[ch - 'A']++;
            maxCount = Math.max(maxCount, array[ch - 'A']);
            int currentLength = end - start + 1;

            if (currentLength - maxCount > k) {
                array[s.charAt(start) - 'A']--;
                start++;
            }

            ans = Math.max(ans, (end - start + 1));
        }
        return ans;
    }

}
