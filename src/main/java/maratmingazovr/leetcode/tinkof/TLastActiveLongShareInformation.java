package maratmingazovr.leetcode.tinkof;

import lombok.NonNull;


public class TLastActiveLongShareInformation {

    @NonNull
    Double price;

    @NonNull
    Double takeProfit;

    @NonNull
    Double stopLoss;

    public TLastActiveLongShareInformation(@NonNull Double price) {
        updatePrice(price);
    }


    public String toStringPriceTakeProfitAndStopLoss() {
        return TUtils.formatDouble(price) + " / "
                + TUtils.formatDouble(takeProfit) + " / "
                + TUtils.formatDouble(stopLoss);
    }

    public void updatePrice(@NonNull Double price) {
        this.price = price;
        this.takeProfit = price + price * TUtils.TAKE_PROFIT_PERCENT;
        this.stopLoss = price - price * TUtils.STOP_LOSS_PERCENT;
    }

    @NonNull
    public Double getPrice() {
        return this.price;
    }

}
