package maratmingazovr.leetcode.tinkof;

import lombok.NonNull;

public enum TOperationType {
    BUY,
    SELL;

    @NonNull
    public static TOperationType getFromString(@NonNull String type) {
        if (type.toLowerCase().contains("покупка")) {
            return TOperationType.BUY;
        }
        if (type.toLowerCase().contains("продажа")) {
            return TOperationType.SELL;
        }
        throw new IllegalArgumentException("Invalid type: " + type);
    }
}
