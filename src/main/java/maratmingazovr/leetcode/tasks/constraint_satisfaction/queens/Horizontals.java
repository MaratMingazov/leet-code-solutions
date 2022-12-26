package maratmingazovr.leetcode.tasks.constraint_satisfaction.queens;

import lombok.NonNull;

public enum Horizontals {
    _1(1),
    _2(2),
    _3(3),
    _4(4),
    _5(5),
    _6(6),
    _7(7),
    _8(8);

    @NonNull
    final Integer value;

    Horizontals(@NonNull Integer value) {
        this.value = value;
    }
}
