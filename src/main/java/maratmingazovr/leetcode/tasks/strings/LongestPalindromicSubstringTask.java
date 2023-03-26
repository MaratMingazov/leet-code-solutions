package maratmingazovr.leetcode.tasks.strings;

public class LongestPalindromicSubstringTask {

    public String LongestPalindromicSubstring(String s) {

        int leftIndex = 0;
        int rightIndex = 0;

        for (int i = 0; i < s.length(); i++) {
            int oddLength = palindromeLength(i,i,s);
            int evenLength = palindromeLength(i,i+1,s);
            int length = Math.max(oddLength, evenLength);

            if (length > rightIndex-leftIndex) {
                leftIndex = i - (length-1)/2;
                rightIndex = i + length/2;
            }
        }
        return s.substring(leftIndex, rightIndex+1);
    }

    private int palindromeLength(int leftIndex, int rightIndex, String s) {
        while(leftIndex >= 0 && rightIndex < s.length() && s.charAt(leftIndex) == s.charAt(rightIndex) ) {
            leftIndex--;
            rightIndex++;
        }
        return rightIndex-leftIndex-1;
    }
}
