package maratmingazovr.leetcode.neural_network;

import lombok.NonNull;
import lombok.Value;

@Value
public class ValidationResult {

    @NonNull
    Integer correct;

    @NonNull
    Integer trials;

    @NonNull
    Double percentage;
}
