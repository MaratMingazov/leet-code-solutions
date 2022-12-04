package maratmingazovr.leetcode.tasks.linked_list;

import lombok.extern.log4j.Log4j2;
import maratmingazovr.leetcode.models.Node;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * <a href="https://leetcode.com/problems/linked-list-cycle/description/">description</a>
 * Given head, the head of a linked list, determine if the linked list has a cycle in it.
 * Return true if there is a cycle in the linked list. Otherwise, return false.
 */

@Log4j2
@Service
public class LinkedListCycleTask {

    /**
     * O(n) memory solution
     * @param head head of the linked list
     * @return true if there is a cycle in the linked list. Otherwise, return false.
     */
    public boolean hasCycleSolution1(Node head) {

        if (head == null) {
            return false;
        }

        Set<Node> values = new HashSet<>();
        values.add(head);
        log.info(values);

        while((head = head.getNext()) != null) {
            if (values.contains(head)) {
                return true;
            } else {
                values.add(head);
            }
            log.info(values);
        }
        return false;
    }

    /**
     * O(1) memory solution
     * @param head head of the linked list
     * @return true if there is a cycle in the linked list. Otherwise, return false.
     */
    public boolean hasCycleSolution2(Node head) {

        if (head == null) {
            return false;
        }

        Node fast = head;
        Node slow = head;
        do {
            if (fast.getNext() == null || fast.getNext().getNext() == null) {
                return false;
            }
            fast = fast.getNext().getNext();
            slow = slow.getNext();
        } while (fast != slow);

        return true;
    }

}
