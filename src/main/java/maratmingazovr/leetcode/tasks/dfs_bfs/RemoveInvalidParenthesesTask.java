package maratmingazovr.leetcode.tasks.dfs_bfs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

// https://leetcode.com/problems/remove-invalid-parentheses/description/
public class RemoveInvalidParenthesesTask {

    public List<String> removeInvalidParentheses(String s) {

        List<String> result = new ArrayList<>();
        Queue<String> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();

        if (s == null) {
            return result;
        }
        queue.add(s);

        boolean found = false;

        while(!queue.isEmpty()) {
            String str = queue.poll();

            if (isValid(str)) {
                result.add(str);
                found = true;
            }

            if (found) {
                continue;
            }

            for (int i = 0; i < str.length(); i++) {
                char ch = str.charAt(i);
                if (ch != '(' && ch != ')') {
                    continue;
                }
                String newStr = str.substring(0, i) + str.substring(i+1);
                if (!visited.contains(newStr)) {
                    queue.add(newStr);
                    visited.add(newStr);
                }
            }


        }

        return result;

    }

    private boolean isValid(String s) {
        int count = 0;
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (ch == '(') {
                count++;
            } else if (ch == ')' && count-- == 0) {
                return false;
            }
        }
        return count == 0;
    }
}
