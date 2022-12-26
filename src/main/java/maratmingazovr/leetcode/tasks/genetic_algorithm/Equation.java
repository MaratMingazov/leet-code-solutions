package maratmingazovr.leetcode.tasks.genetic_algorithm;

import lombok.Data;
import lombok.NonNull;
import lombok.val;
import maratmingazovr.leetcode.genetic_algorithm.Chromosome;
import maratmingazovr.leetcode.genetic_algorithm.GeneticAlgorithm;
import maratmingazovr.leetcode.genetic_algorithm.SelectionType;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Data
public class Equation implements Chromosome {

    @NonNull
    private Double x;

    @NonNull
    private Double y;

    public Equation(@NonNull Double x,
                    @NonNull Double y) {
        this.x = x;
        this.y = y;
    }

    public static Chromosome getRandomInstance() {
        val random = new Random();
        return new Equation(random.nextDouble(), random.nextDouble());
    }

    @NonNull
    @Override
    public Double fitness() {
        return 6*x - x*x + 4*y - y*y;
    }

    @NonNull
    @Override
    public List<Chromosome> crossover(@NonNull Chromosome other) {
        if (other instanceof Equation) {
            val otherEquation = (Equation) other;
            val child1 = new Equation(x, otherEquation.getY());
            val child2 = new Equation(otherEquation.getX(), y);
            return List.of(child1, child2);
        }
        return List.of(this, other);
    }

    @NonNull
    @Override
    public void mutate() {
        val random = new Random();
        if (random.nextDouble() > 0.5) { // mutate x
            if (random.nextDouble() > 0.5) {
                x += 0.1;
            } else {
                x -= 0.1;
            }
        } else { // otherwise mutate y
            if (random.nextDouble() > 0.5) {
                y += 0.1;
            } else {
                y -= 0.1;
            }
        }
    }

    @NonNull
    @Override
    public Chromosome getCopy() {
        return new Equation(x, y);
    }

    @Override
    public String toString() {
        return "X: " + x + " Y: " + y + " Fitness: " + fitness();
    }

    public static void main(String[] args) {

        val log = LoggerFactory.getLogger(Equation.class);
        List<Chromosome> initialPopulation = new ArrayList<>();
        final int POPULATION_SIZE = 20;
        final int GENERATIONS = 100;
        final double THRESHOLD = 13.0;
        for (int i = 0; i < POPULATION_SIZE; i++) {
            initialPopulation.add(Equation.getRandomInstance());
        }
        val ga = new GeneticAlgorithm(initialPopulation, 0.1, 0.7, SelectionType.TOURNAMENT);
        val result = ga.run(GENERATIONS, THRESHOLD);
        log.info(result.toString());
    }
}
