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
@Import(IntegerToRomanTask.class)
public class IntegerToRomanTest {

    @Autowired
    IntegerToRomanTask integerToRomanTask;

    @ParameterizedTest
    @MethodSource("inputsForTestIntToRoman")
    void testIntToRoman(int num, String expectedRoman) {

        String actualRoman = integerToRomanTask.intToRoman(num);

        assertEquals(expectedRoman, actualRoman);
    }

    private static Stream<Arguments> inputsForTestIntToRoman() {
        return Stream.of(
                Arguments.of(3, "III"),
                Arguments.of(58, "LVIII"),
                Arguments.of(1994, "MCMXCIV")
                        );
    }
}
