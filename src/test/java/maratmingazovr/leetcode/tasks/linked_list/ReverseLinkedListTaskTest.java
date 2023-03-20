package maratmingazovr.leetcode.tasks.linked_list;

import lombok.val;
import maratmingazovr.leetcode.models.Node;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@Import(ReverseLinkedListTask.class)
public class ReverseLinkedListTaskTest {

    @Autowired
    ReverseLinkedListTask reverseLinkedListTask;

    @ParameterizedTest
    @MethodSource("provideInputsForTestReverseLinkedList")
    void testReverseLinkedListIteratively(Node head, Node expectedReversedHead) {
        val actualReversedHead = reverseLinkedListTask.reverseLinkedListIteratively(head);

        assertTrue(Node.isEquals(expectedReversedHead, actualReversedHead));
    }

    @ParameterizedTest
    @MethodSource("provideInputsForTestReverseLinkedList")
    void testReverseLinkedListRecursively(Node head, Node expectedReversedHead) {
        val actualReversedHead = reverseLinkedListTask.reverseLinkedListRecursively(head);

        assertTrue(Node.isEquals(expectedReversedHead, actualReversedHead));
    }

    private static Stream<Arguments> provideInputsForTestReverseLinkedList() {

        Node node1_1 = new Node(1, null);
        Node node1_1_reversed = new Node(1, null);

        Node node2_1 = new Node(1, null);
        Node node2_2 = new Node(2, node2_1);
        Node node2_3 = new Node(3, node2_2);

        Node node2_1_reversed = new Node(3, null);
        Node node2_2_reversed = new Node(2, node2_1_reversed);
        Node node2_3_reversed = new Node(1, node2_2_reversed);

        return Stream.of(
                Arguments.of(null, null),
                Arguments.of(node1_1, node1_1_reversed),
                Arguments.of(node2_3, node2_3_reversed)
                        );
    }


}
