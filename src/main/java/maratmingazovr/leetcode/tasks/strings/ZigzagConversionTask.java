package maratmingazovr.leetcode.tasks.strings;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <a href="https://leetcode.com/problems/zigzag-conversion/">description</a>
 */
public class ZigzagConversionTask {

    public String zigzagConversion(String s, int numRows) {

        if (numRows < 2) {
            return s;
        }

        int index = 0;
        int row = 0;
        int col = 0;
        boolean goDown = true;
        List<List<String>> matrix = new ArrayList<>();

        for (int i = 0; i < numRows; i++) {
            List<String> mRow = new ArrayList<>();
            for (int j = 0; j < s.length(); j++) {
                mRow.add(null);
            }
            matrix.add(mRow);
        }

        numRows--;
        while (index < s.length()) {
            String ch = s.substring(index, index+1);
            if (goDown) {
                matrix.get(row).set(col, ch);
                if (row == numRows) {
                    goDown = false;
                    col++;row--;
                } else {
                    row++;
                }
            } else {
                matrix.get(row).set(col, ch);
                if (row == 0) {
                    goDown = true;
                    col++;row++;
                } else {
                    col++; row--;
                }
            }
            index++;
        }

        return matrix.stream()
                           .map(list -> list.stream()
                                            .filter(Objects::nonNull)
                                            .reduce("", String::concat))
                           .reduce("", String::concat);
    }

    public String zigzagConversionShort(String s, int numRows) {
        if (numRows == 1) return s;

        StringBuilder ret = new StringBuilder();
        int n = s.length();
        int cycleLen = 2 * numRows - 2;

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j + i < n; j += cycleLen) {
                ret.append(s.charAt(j + i));
                if (i != 0 && i != numRows - 1 && j + cycleLen - i < n)
                    ret.append(s.charAt(j + cycleLen - i));
            }
        }
        return ret.toString();
    }
}
