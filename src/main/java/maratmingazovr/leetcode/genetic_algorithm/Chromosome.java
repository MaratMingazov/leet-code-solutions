package maratmingazovr.leetcode.genetic_algorithm;

import org.springframework.lang.NonNull;

import java.util.List;

public interface Chromosome extends Comparable<Chromosome> {

    @NonNull
    Double fitness();

    @NonNull
    List<Chromosome> crossover(@NonNull Chromosome chromosome);

    void mutate();

    @NonNull
    Chromosome getCopy();


    @Override
    default int compareTo(Chromosome other) {
        Double mine = this.fitness();
        Double theirs = other.fitness();
        return mine.compareTo(theirs);
    }


}
