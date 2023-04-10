package maratmingazovr.leetcode.tasks.linked_list;

import maratmingazovr.leetcode.models.Node;

import java.util.ArrayList;
import java.util.List;

// https://leetcode.com/problems/remove-nth-node-from-end-of-list/description/
public class RemoveNodeTask {

    public Node removeNthFromEnd(Node head, int n) {

        if (n < 1 || head == null) {
            return head;
        }

        List<Node> nodes = new ArrayList<>();
        while(head != null) {
            nodes.add(head);
            head = head.getNext();
        }

        int indexToRemove = nodes.size()-n;
        if (indexToRemove < 1) {
            return nodes.get(0).getNext();
        }

        Node previousNode = nodes.get(indexToRemove-1);
        if (previousNode.getNext() != null) {
            previousNode.setNext(previousNode.getNext().getNext());
        }

        return nodes.get(0);
    }
}
