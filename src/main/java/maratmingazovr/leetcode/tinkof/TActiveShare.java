package maratmingazovr.leetcode.tinkof;

import lombok.Data;
import lombok.NonNull;

@Data
public class TActiveShare {

    @NonNull
    String shareId;

    @NonNull
    String shareFigi;

    @NonNull
    Double price;

    @NonNull
    Double count;

    @NonNull
    TCurrency currency;


    public TActiveShare(@NonNull String shareId,
                        @NonNull String shareFigi,
                        @NonNull TCurrency currency,
                        @NonNull Double price,
                        @NonNull Double count) {
        this.shareId = shareId;
        this.price = price;
        this.count = count;
        this.currency = currency;
    }
}
