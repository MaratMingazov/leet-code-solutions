package maratmingazovr.leetcode.tasks.strings;

import java.util.ArrayList;
import java.util.List;

// https://leetcode.com/problems/generate-parentheses/
public class GenerateParenthesesTask {

    public List<String> generateParenthesis(int n) {
        List<String> result = new ArrayList<>();
        calculate(result, 0, 0, n, "");
        return result;

    }

    public void calculate(List<String> result, int open, int close, int n, String current) {
        if (open == n && close == n) {
            result.add(current);
            return;
        }
        if (open < n) {
            calculate(result, open+1, close, n, current + "(");
        }
        if (close < open) {
            calculate(result, open, close+1, n, current + ")");
        }
    }
}
