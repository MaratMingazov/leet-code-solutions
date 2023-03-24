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
@Import(LongestSubstringTask.class)
public class LongestSubstringTest {

    @Autowired
    LongestSubstringTask longestSubstringTask;

    @ParameterizedTest
    @MethodSource("provideInputsForTestLengthOfLongestSubstring")
    void testLengthOfLongestSubstring(String str, int expected) {

        int actual = longestSubstringTask.lengthOfLongestSubstring(str);
        int actualLinear = longestSubstringTask.lengthOfLongestSubstringLinear(str);

        assertEquals(expected, actual);
        assertEquals(expected, actualLinear);
    }

    private static Stream<Arguments> provideInputsForTestLengthOfLongestSubstring() {


        return Stream.of(
                Arguments.of("abcabcbb", 3),
                Arguments.of("bbbbb", 1),
                Arguments.of("pwwkew", 3)
                        );
    }
}
