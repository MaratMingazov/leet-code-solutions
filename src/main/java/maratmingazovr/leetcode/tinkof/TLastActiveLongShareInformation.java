package maratmingazovr.leetcode.tinkof;

import lombok.NonNull;
import lombok.Value;

@Value
public class TLastActiveLongShareInformation {

    @NonNull
    Double price;

    @NonNull
    Double takeProfit;

    public TLastActiveLongShareInformation(@NonNull Double price) {
        this.price = price;
        this.takeProfit = price + price * TUtils.TAKE_PROFIT_PERCENT;
    }
}
