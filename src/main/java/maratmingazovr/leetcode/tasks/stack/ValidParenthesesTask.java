package maratmingazovr.leetcode.tasks.stack;

import java.util.Stack;

// https://leetcode.com/problems/valid-parentheses/description/
public class ValidParenthesesTask {

    public boolean isValid(String s) {
        Stack<Character> stack = new Stack<>();
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (ch == '(' || ch == '{' || ch == '[') {
                stack.push(ch);
            } else {
                if (stack.isEmpty()) {
                    return false;
                }
                char element = stack.pop();
                if (ch == ')' && element != '(') {
                    return false;
                }
                if (ch == '}' && element != '{') {
                    return false;
                }
                if (ch == ']' && element != '[') {
                    return false;
                }
            }
        }
        return stack.isEmpty();
    }
}
