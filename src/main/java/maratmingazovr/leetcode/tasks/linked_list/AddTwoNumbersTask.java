package maratmingazovr.leetcode.tasks.linked_list;

import lombok.NonNull;
import maratmingazovr.leetcode.models.Node;

/**
 * <a href="https://leetcode.com/problems/add-two-numbers/">description</a>
 * You are given two non-empty linked lists representing two non-negative integers.
 * The digits are stored in reverse order, and each of their nodes contains a single digit.
 * Add the two numbers and return the sum as a linked list.
 */
public class AddTwoNumbersTask {

    @NonNull
    public Node addTwoNumbers(@NonNull Node head1, @NonNull Node head2) {
        Node zeroNode = new Node(0);
        Node currentNode = zeroNode;

        int carry = 0;
        while (head1 != null || head2 != null || carry == 1) {
            int resultVal = 0;
            if (head1 != null) {
                resultVal += head1.getValue();
                head1 = head1.getNext();
            }

            if (head2 != null) {
                resultVal += head2.getValue();
                head2 = head2.getNext();
            }

            resultVal += carry;
            if (resultVal > 9) {
                resultVal -= 10;
                carry = 1;
            } else {
                carry = 0;
            }

            currentNode.setNext(new Node(resultVal));
            currentNode = currentNode.getNext();
        }

        return zeroNode.getNext();
    }
}
