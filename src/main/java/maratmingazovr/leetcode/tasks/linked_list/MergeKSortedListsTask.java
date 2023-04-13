package maratmingazovr.leetcode.tasks.linked_list;

import maratmingazovr.leetcode.models.Node;

// https://leetcode.com/problems/merge-k-sorted-lists/description/
public class MergeKSortedListsTask {

    public Node mergeKLists(Node[] lists) {
        if (lists.length == 1) {
            return lists[0];
        }

        Node resultFirstNode = null;
        Node resultLastNode = null;


        boolean finish = false;

        while (!finish) {
            finish = true;
            Node minNode = null;
            Integer minIndex = null;
            for (int i = 0; i < lists.length; i++) {
                Node currentNode = lists[i];
                if (currentNode != null) {
                    finish = false;
                    if (minNode == null || currentNode.getValue() < minNode.getValue()) {
                        minNode = currentNode;
                        minIndex = i;
                    }
                }

            }
            if (minNode == null) {
                return resultFirstNode;
            }
            if (resultLastNode == null) {
                resultFirstNode = minNode;
                resultLastNode = minNode;
            } else {
                resultLastNode.setNext(minNode);
                resultLastNode = minNode;
            }
            lists[minIndex] = lists[minIndex].getNext();
        }

        return resultFirstNode;
    }
}
