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
@Import(ZigzagConversionTask.class)
public class ZigzagConversionTest {

    @Autowired
    ZigzagConversionTask zigzagConversionTask;

    @ParameterizedTest
    @MethodSource("provideInputsForTestZigzagConversion")
    void testZigzagConversion(String str, int rows, String expected) {


        String actual = zigzagConversionTask.zigzagConversion(str, rows);
        String actual2 = zigzagConversionTask.zigzagConversionShort(str, rows);

        assertEquals(expected, actual);
        assertEquals(expected, actual2);
    }

    private static Stream<Arguments> provideInputsForTestZigzagConversion() {


        return Stream.of(
                Arguments.of("PAYPALISHIRING", 3, "PAHNAPLSIIGYIR"),
                Arguments.of("PAYPALISHIRING", 4, "PINALSIGYAHRPI"),
                Arguments.of("A", 1, "A")
                        );
    }
}
