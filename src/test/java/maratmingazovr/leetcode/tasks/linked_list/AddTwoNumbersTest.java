package maratmingazovr.leetcode.tasks.linked_list;

import lombok.NonNull;
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
@Import(AddTwoNumbersTask.class)
public class AddTwoNumbersTest {

    @Autowired
    AddTwoNumbersTask addTwoNumbersTask;

    @ParameterizedTest
    @MethodSource("provideInputsForTestAddTwoNumbers")
    void testAddTwoNumbers(@NonNull Node head1,
                           @NonNull Node head2,
                           @NonNull Node expected) {

        val actual = addTwoNumbersTask.addTwoNumbers(head1, head2);

        assertTrue(Node.isEquals(expected, actual));
    }

    private static Stream<Arguments> provideInputsForTestAddTwoNumbers() {

        Node head1a_1 = new Node(3, null);
        Node head1a_2 = new Node(4, head1a_1);
        Node head1a_3 = new Node(2, head1a_2);

        Node head1b_1 = new Node(4, null);
        Node head1b_2 = new Node(6, head1b_1);
        Node head1b_3 = new Node(5, head1b_2);

        Node head1c_1 = new Node(8, null);
        Node head1c_2 = new Node(0, head1c_1);
        Node head1c_3 = new Node(7, head1c_2);


        Node head2a_1 = new Node(0, null);
        Node head2b_1 = new Node(0, null);
        Node head2c_1 = new Node(0, null);


        Node head3a_1 = new Node(9, null);
        Node head3a_2 = new Node(9, head3a_1);
        Node head3a_3 = new Node(9, head3a_2);
        Node head3a_4 = new Node(9, head3a_3);
        Node head3a_5 = new Node(9, head3a_4);
        Node head3a_6 = new Node(9, head3a_5);
        Node head3a_7 = new Node(9, head3a_6);

        Node head3b_1 = new Node(9, null);
        Node head3b_2 = new Node(9, head3b_1);
        Node head3b_3 = new Node(9, head3b_2);
        Node head3b_4 = new Node(9, head3b_3);

        Node head3c_1 = new Node(1, null);
        Node head3c_2 = new Node(0, head3c_1);
        Node head3c_3 = new Node(0, head3c_2);
        Node head3c_4 = new Node(0, head3c_3);
        Node head3c_5 = new Node(9, head3c_4);
        Node head3c_6 = new Node(9, head3c_5);
        Node head3c_7 = new Node(9, head3c_6);
        Node head3c_8 = new Node(8, head3c_7);


        return Stream.of(
                Arguments.of(head1a_3, head1b_3, head1c_3),
                Arguments.of(head2a_1, head2b_1, head2c_1),
                Arguments.of(head3a_7, head3b_4, head3c_8)
                        );
    }
}
