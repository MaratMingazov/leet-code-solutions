package maratmingazovr.leetcode.neural_network;

import lombok.val;
import maratmingazovr.leetcode.tasks.neural_network.iris_classificator.IrisClassificator;
import maratmingazovr.leetcode.tasks.neural_network.numbers_multiplicator.DecimalMultiplicator;
import maratmingazovr.leetcode.tasks.neural_network.wine_classificator.WineClassificator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class NetworkTest {

    @Test
    void testNetwork() {
        Network network = new Network(List.of(3, 4, 3), 0.2, List.of(ActivationFunction.SIGMOID, ActivationFunction.SIGMOID));
        val inputs = List.of(
                List.of(0.1, 0.2, 0.3),
                List.of(0.4, 0.5, 0.6),
                List.of(0.7, 0.8, 0.9));
        val expected = List.of(
                List.of(1.0, 0.0, 0.0),
                List.of(0.0, 1.0, 0.0),
                List.of(0.0, 0.0, 1.0));
        network.train(inputs, expected, 1000L);

        val output0 = network.calculateOutputs(List.of(0.1, 0.2, 0.3));
        val output1 = network.calculateOutputs(List.of(0.4, 0.5, 0.6));
        val output2 = network.calculateOutputs(List.of(0.7, 0.8, 0.9));
        assertEquals(0, Util.getMaxValueIndex(output0));
        assertEquals(1, Util.getMaxValueIndex(output1));
        assertEquals(2, Util.getMaxValueIndex(output2));
    }

    @Test
    void testIrisClassificator() {
        val classificator = new IrisClassificator();
        classificator.loadData();
        classificator.loadNetwork();
        val result = classificator.validate();
        assertThat("percentage", result.getPercentage(), greaterThanOrEqualTo(0.95D));
    }

    @Test
    void testWineClassificator() {
        val classificator = new WineClassificator();
        classificator.loadData();
        classificator.loadNetwork();
        val result = classificator.validate();
        assertThat("percentage", result.getPercentage(), greaterThanOrEqualTo(0.95D));
    }

    @Test
    void testDecimalMultiplicator() {
        val multiplicator = new DecimalMultiplicator();
        multiplicator.loadData();
        multiplicator.loadNetwork();
        val result = multiplicator.validate();
        assertThat("percentage", result.getPercentage(), greaterThanOrEqualTo(0.95D));
    }
}
