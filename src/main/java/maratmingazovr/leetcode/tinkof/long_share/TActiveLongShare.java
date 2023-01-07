package maratmingazovr.leetcode.tinkof.long_share;

import lombok.Data;
import lombok.NonNull;
import maratmingazovr.leetcode.tinkof.enums.TCurrency;

@Data
public class TActiveLongShare {

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


    public TActiveLongShare(@NonNull String shareId,
                            @NonNull String shareFigi,
                            @NonNull TCurrency currency,
                            @NonNull Double price,
                            @NonNull Double count) {
        this.shareId = shareId;
        this.shareFigi = shareFigi;
        this.price = price;
        this.count = count;
        this.currency = currency;
    }
}
