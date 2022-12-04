package maratmingazovr.leetcode.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;


@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class Node {

    @NonNull
    private Integer value;

    @Nullable
    private Node next;

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public boolean equals(@Nullable Object other) {
        if (!(other instanceof Node)) {
            return false;
        }
        return this.value.equals(((Node) other).getValue());
    }

    @Override
    public int hashCode() {
        return value;
    }
}
