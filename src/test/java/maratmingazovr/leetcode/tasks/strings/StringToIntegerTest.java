package maratmingazovr.leetcode.tasks.strings;

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
@Import(StringToIntegerTask.class)
public class StringToIntegerTest {

    @Autowired
    StringToIntegerTask stringToIntegerTask;

    @ParameterizedTest
    @MethodSource("provideInputsTestStringToInteger")
    public void testStringToInteger(String s, int expected) {

        int actual = stringToIntegerTask.myAtoi(s);

        assertEquals(expected, actual);

    }

    private static Stream<Arguments> provideInputsTestStringToInteger() {
        return Stream.of(
                Arguments.of("001", 1),
                Arguments.of("+001", 1),
                Arguments.of("-001", -1),
                Arguments.of("+-001", 0),
                Arguments.of("-001 av", -1),
                Arguments.of("-a001 av", 0)
                        );
    }
}
