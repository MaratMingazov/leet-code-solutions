package maratmingazovr.leetcode.tasks.arrays;

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
@Import(ContainerWithMostWater.class)
public class ContainerWithMostWaterTest {

    @Autowired
    ContainerWithMostWater containerWithMostWater;

    @ParameterizedTest
    @MethodSource("inputsTestContainerWithMostWater")
    public void testContainerWithMostWater(int[] array, int expectedArea) {

        int actualArea = containerWithMostWater.maxArea(array);

        assertEquals(expectedArea, actualArea);

    }

    private static Stream<Arguments> inputsTestContainerWithMostWater() {
        return Stream.of(
                Arguments.of(new int[]{1,8,6,2,5,4,8,3,7}, 49)
                        );
    }
}
