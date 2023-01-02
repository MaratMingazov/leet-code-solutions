package maratmingazovr.leetcode.tinkof;

import lombok.Data;
import lombok.NonNull;

@Data
public class TActiveShare {
    @NonNull
    private final String currency;

    @NonNull
    private final Double price;

    @NonNull
    private final Double count;

    @NonNull
    private final TShare share;

    public TActiveShare(@NonNull String currency,
                        @NonNull Double price,
                        @NonNull Double count,
                        @NonNull TShare share) {
        this.currency = currency;
        this.price = price;
        this.count = count;
        this.share = share;
    }
}
