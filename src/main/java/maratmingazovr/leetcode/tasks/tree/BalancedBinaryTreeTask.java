package maratmingazovr.leetcode.tasks.tree;

import maratmingazovr.leetcode.models.Node;

// https://leetcode.com/problems/balanced-binary-tree/description/
public class BalancedBinaryTreeTask {

    public boolean isBalanced(Node root) {
        return search(root) != -1;
    }

    public int search(Node node) {

        if (node == null) {
            return 0;
        }

        int left = search(node.getNext());
        if (left == -1) {
            return -1;
        }
        int right = search(node.getNext());
        if (right == -1) {
            return -1;
        }

        if (Math.abs(left-right) > 1) {
            return -1;
        }

        return Math.max(left,right) + 1;
    }
}
