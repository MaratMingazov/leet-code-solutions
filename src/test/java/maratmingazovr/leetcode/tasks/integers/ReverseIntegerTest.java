package maratmingazovr.leetcode.tasks.integers;

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
@Import(ReverseIntegerTask.class)
public class ReverseIntegerTest {

    @Autowired
    ReverseIntegerTask reverseIntegerTask;

    @ParameterizedTest
    @MethodSource("provideInputsForTestReverseInteger")
    void testReverseInteger(int x, int expected) {


        int actual = reverseIntegerTask.reverseInteger(x);
        int actual2 = reverseIntegerTask.reverseInteger2(x);

        assertEquals(expected, actual);
        assertEquals(expected, actual2);
    }

    private static Stream<Arguments> provideInputsForTestReverseInteger() {

        return Stream.of(
                Arguments.of(123, 321),
                Arguments.of(0, 0),
                Arguments.of(210, 12),
                Arguments.of(-2105, -5012)
                        );
    }
}
