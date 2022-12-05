package maratmingazovr.leetcode.tasks.linked_list;

import lombok.extern.log4j.Log4j2;
import maratmingazovr.leetcode.models.Node;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

/**
 * <a href="https://leetcode.com/problems/reverse-linked-list/description/">description</a>
 * Given the head of a singly linked list, reverse the list, and return the reversed list.
 */

@Log4j2
@Service
public class ReverseLinkedListTask {

    public Node reverseLinkedListIteratively(@Nullable Node head) {
        if (head == null || head.getNext() == null) {
            return head;
        }
        Node previousNode = null;
        Node nextNode = null;
        while(head != null) {
            nextNode = head.getNext();
            head.setNext(previousNode);
            previousNode = head;
            head = nextNode;
        }
        return previousNode;
    }

    public Node reverseLinkedListRecursively(@Nullable Node head) {
        return findHeadNode(head, null);
    }

    private Node findHeadNode(Node currentNode, Node previousNode) {
        if (currentNode == null) {
            return previousNode;
        }
        Node next = currentNode.getNext();
        currentNode.setNext(previousNode);
        return findHeadNode(next, currentNode);
    }
}
