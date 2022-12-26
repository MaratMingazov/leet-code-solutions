package maratmingazovr.leetcode.genetic_algorithm;

import lombok.Data;
import lombok.NonNull;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Data
public class GeneticAlgorithm {


    @NonNull
    private List<Chromosome> population;
    @NonNull
    private Double mutationChance;
    @NonNull
    private Double crossoverChance;
    @NonNull
    private Random random;

    @NonNull
    private SelectionType selectionType;

    @NonNull
    Logger log = LoggerFactory.getLogger(GeneticAlgorithm.class);

    public GeneticAlgorithm(@NonNull List<Chromosome> initialPopulation,
                            @NonNull Double mutationChance,
                            @NonNull Double crossoverChance,
                            @NonNull SelectionType selectionType) {

        this.population = initialPopulation;
        this.mutationChance = mutationChance;
        this.crossoverChance = crossoverChance;
        this.random = new Random();

    }

    // Use the probability distribution wheel to pick numPicks individuals
    @NonNull
    private List<Chromosome> pickRoulette(@NonNull List<Double> wheel,
                                          @NonNull Integer numPicks) {
        List<Chromosome> picks = new ArrayList<>();
        for (int i = 0; i < numPicks; i++) {
            double pick = random.nextDouble();
            for (int j = 0; j < wheel.size(); j++) {
                pick -= wheel.get(j);
                if (pick <= 0) { // we had one that took us over, leads to a pick
                    picks.add(population.get(j));
                    break;
                }
            }
        }
        return picks;
    }

    // Pick a certain number of individuals via a tournament
    @NonNull
    private List<Chromosome> pickTournament(@NonNull Integer numParticipants,
                                            @NonNull Integer numPicks) {
        // Find numParticipants random participants to be in the tournament
        Collections.shuffle(population);
        List<Chromosome> tournament = population.subList(0, numParticipants);
        // Find the numPicks highest fitnesses in the tournament
        tournament.sort(Collections.reverseOrder());
        return tournament.subList(0, numPicks);
    }

    // With mutationChance probability, mutate each individual
    private void mutate() {
        for (Chromosome individual : population) {
            if (mutationChance > random.nextDouble()) {
                individual.mutate();
            }
        }
    }

    // Replace the population with a new generation of individuals
    private void reproduceAndReplace() {
        ArrayList<Chromosome> nextPopulation = new ArrayList<>();
        // keep going until we've filled the new generation
        while (nextPopulation.size() < population.size()) {
            // pick the two parents
            List<Chromosome> parents;
            if (selectionType == SelectionType.ROULETTE) {
                // create the probability distribution wheel
                val totalFitness = population.stream().mapToDouble(Chromosome::fitness).sum();
                val wheel = population.stream().map(C -> C.fitness() / totalFitness).collect(Collectors.toList());
                parents = pickRoulette(wheel, 2);
            } else { // tournament
                parents = pickTournament(population.size() / 2, 2);
            }
            // potentially crossover the 2 parents
            if (random.nextDouble() < crossoverChance) {
                val parent1 = parents.get(0);
                val parent2 = parents.get(1);
                nextPopulation.addAll(parent1.crossover(parent2));
            } else { // just add the two parents
                nextPopulation.addAll(parents);
            }
        }
        // if we have an odd number, we'll have 1 exra, so we remove it
        if (nextPopulation.size() > population.size()) {
            nextPopulation.remove(0);
        }
        // replace the reference/generation
        population = nextPopulation;
    }

    // Run the genetic algorithm for maxGenerations iterations
    // and return the best individual found
    @NonNull
    public Chromosome run(@NonNull Integer epoh,
                          @NonNull Double threshold) {
        var best = Collections.max(population).getCopy();
        for (int generation = 0; generation < epoh; generation++) {
            // early exit if we beat threshold
            if (best.fitness() >= threshold) {
                return best;
            }
            // Debug printout
            log.info("Generation " + generation + " Best " + best.fitness() + " Avg " + population.stream().mapToDouble(Chromosome::fitness).average().orElse(0.0));
            reproduceAndReplace();
            mutate();
            val highest = Collections.max(population);
            if (highest.fitness() > best.fitness()) {
                best = highest.getCopy();
            }
        }
        return best;
    }

}
