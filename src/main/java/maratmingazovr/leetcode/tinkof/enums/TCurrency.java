package maratmingazovr.leetcode.tinkof.enums;

import lombok.NonNull;

public enum TCurrency {
    USD,
    RUB;

    @NonNull
    public static TCurrency getFromString(@NonNull String currency) {
        switch (currency.toLowerCase()) {
            case "rub": return TCurrency.RUB;
            case "usd": return TCurrency.USD;
            default: throw new IllegalArgumentException("invalid currency: " + currency);
        }
    }
}
