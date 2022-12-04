package maratmingazovr.leetcode.tasks.linked_list;

import lombok.NonNull;
import lombok.val;
import maratmingazovr.leetcode.models.Node;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@Import(LinkedListCycleTask.class)
public class LinkedListCycleTaskTest {

    @Autowired
    LinkedListCycleTask linkedListCycleTask;

    @ParameterizedTest
    @MethodSource("provideInputsForTestHasCycle")
    void testHasCycle(@Nullable Node head, @NonNull boolean expectedOutput) {
        val actualOutputSolution1 = linkedListCycleTask.hasCycleSolution1(head);
        val actualOutputSolution2 = linkedListCycleTask.hasCycleSolution2(head);

        assertEquals(expectedOutput, actualOutputSolution1);
        assertEquals(expectedOutput, actualOutputSolution2);
    }

    private static Stream<Arguments> provideInputsForTestHasCycle() {

        Node node1_1 = new Node(1, null);
        Node node1_2 = new Node(2, node1_1);
        Node node1_3 = new Node(3, node1_2);

        Node node2_1 = new Node(1, null);
        Node node2_2 = new Node(2, node2_1);
        Node node2_3 = new Node(3, node2_2);
        node2_1.setNext(node2_3);

        return Stream.of(
                Arguments.of(null, false),
                Arguments.of(node1_1, false),
                Arguments.of(node1_3, false),
                Arguments.of(node2_3, true)
                        );
    }
}
