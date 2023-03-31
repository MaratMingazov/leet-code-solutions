package maratmingazovr.leetcode.tasks.strings;

/**
 * <a href="https://leetcode.com/problems/string-to-integer-atoi">description</a>
 */
public class StringToIntegerTask {

    public int myAtoi(String s) {
        long result = 0;
        int sign = 1;
        boolean digitsFound = false;
        boolean signFound = false;
        for (int i = 0; i < s.length(); i++) {
            String strValue = String.valueOf(s.charAt(i));
            if ((int)s.charAt(i) >= 48 && (int)s.charAt(i) <= 57) {
                result  += Integer.parseInt(strValue);
                result = result * 10;
                digitsFound = true;

                if (result/10 > Integer.MAX_VALUE) {
                    if (sign == 1) {
                        return Integer.MAX_VALUE;
                    } else {
                        return Integer.MIN_VALUE;
                    }
                }
            } else {
                if (digitsFound) {
                    break;
                }
                if (strValue.equals("-")) {
                    if (signFound) {
                        return 0;
                    }
                    signFound = true;
                    sign = -1;
                } else if (strValue.equals("+")) {
                    if (signFound) {
                        return 0;
                    }
                    signFound = true;
                } else if(!strValue.equals(" ")) {
                    break;
                } else if (signFound) {
                    return 0;
                }
            }
        }
        result =  sign * (result/10);
        return (int) result;
    }
}
