package maratmingazovr.leetcode.tinkof;

import lombok.NonNull;

public enum TOperationType {
    BUY,
    SELL,
    OTHER;

    @NonNull
    public static TOperationType getFromString(@NonNull String type) {
        if (type.toLowerCase().contains("покупка")) {
            return TOperationType.BUY;
        }
        if (type.toLowerCase().contains("продажа")) {
            return TOperationType.SELL;
        }
        return TOperationType.OTHER;
    }
}
