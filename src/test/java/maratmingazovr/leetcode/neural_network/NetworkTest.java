package maratmingazovr.leetcode.neural_network;

import lombok.val;
import maratmingazovr.leetcode.neural_network.iris_classification.IrisClassificator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class NetworkTest {

    @Test
    void testNetwork() {
        Network<String> network = new Network<>(List.of(3, 4, 3), 0.2, ActivationFunction.SIGMOID);
        val inputs = List.of(
                List.of(0.1, 0.2, 0.3),
                List.of(0.4, 0.5, 0.6),
                List.of(0.7, 0.8, 0.9));
        val expected = List.of(
                List.of(1.0, 0.0, 0.0),
                List.of(0.0, 1.0, 0.0),
                List.of(0.0, 0.0, 1.0));
        network.train(inputs, expected, 2000L);

        val output0 = network.calculateOutputs(List.of(0.1, 0.2, 0.3));
        val output1 = network.calculateOutputs(List.of(0.4, 0.5, 0.6));
        val output2 = network.calculateOutputs(List.of(0.7, 0.8, 0.9));
        assertEquals(0, Util.getMaxValueIndex(output0));
        assertEquals(1, Util.getMaxValueIndex(output1));
        assertEquals(2, Util.getMaxValueIndex(output2));
    }

    @Test
    void testIrisClassificator() {
        val irisClassificator = new IrisClassificator();
        val results = irisClassificator.classify();
        assertThat("percentage", results.percentage, greaterThanOrEqualTo(0.8D));
    }
}
