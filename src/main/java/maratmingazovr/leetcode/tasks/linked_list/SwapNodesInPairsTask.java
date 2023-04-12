package maratmingazovr.leetcode.tasks.linked_list;

import maratmingazovr.leetcode.models.Node;

// https://leetcode.com/problems/swap-nodes-in-pairs/description/
public class SwapNodesInPairsTask {
    public Node swapPairs(Node head) {
        if (head == null || head.getNext() == null) {
            return head;
        }
        Node result = head.getNext();
        Node previous = null;
        Node first = head;
        Node second = head.getNext() ;

        while (second != null) {
            first.setNext(second.getNext());
            second.setNext(first);
            if (previous != null) {
                previous.setNext(second);
            }

            previous = first;
            first = first.getNext();
            if (first != null) {
                second = first.getNext();
            } else {
                second = null;
            }
        }
        return result;

    }
}
