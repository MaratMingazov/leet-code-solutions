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
@Import(LongestPalindromicSubstringTask.class)
public class LongestPalindromicSubstringTest {

    @Autowired
    LongestPalindromicSubstringTask longestPalindromicSubstringTask;

    @ParameterizedTest
    @MethodSource("provideInputsForTestLongestPalindromicSubstring")
    void testLongestPalindromicSubstringg(String str, String expected) {

        String actual = longestPalindromicSubstringTask.LongestPalindromicSubstring(str);

        assertEquals(expected, actual);
    }

    private static Stream<Arguments> provideInputsForTestLongestPalindromicSubstring() {


        return Stream.of(
                Arguments.of("a", "a"),
                Arguments.of("aac", "aa"),
                Arguments.of("aaba", "aba"),
                Arguments.of("aabc", "aa"),
                Arguments.of("aaaa", "aaaa"),
                Arguments.of("aaaaaa", "aaaaaa"),
                Arguments.of("bababd", "babab"),
                Arguments.of("cbbd", "bb")
                        );
    }
}
