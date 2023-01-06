package maratmingazovr.leetcode.tinkof;

import lombok.NonNull;
import lombok.Value;

@Value
public class TLastActiveLongShareInformation {

    @NonNull
    Double price;

    @NonNull
    Double takeProfit;

    @NonNull
    Double stopLoss;

    public TLastActiveLongShareInformation(@NonNull Double price) {
        this.price = price;
        this.takeProfit = price + price * TUtils.TAKE_PROFIT_PERCENT;
        this.stopLoss = price - price * TUtils.STOP_LOSS_PERCENT;
    }


    public String toStringPriceTakeProfitAndStopLoss() {
        return TUtils.formatDouble(price) + " / "
                + TUtils.formatDouble(takeProfit) + " / "
                + TUtils.formatDouble(stopLoss);
    }
}
