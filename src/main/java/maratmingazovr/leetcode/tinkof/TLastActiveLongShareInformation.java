package maratmingazovr.leetcode.tinkof;

import lombok.Data;
import lombok.NonNull;



@Data
public class TLastActiveLongShareInformation {

    @NonNull
    Double price;

    @NonNull
    Double takeProfit;

    @NonNull
    Double stopLoss;

    @NonNull
    Double comission;

    @NonNull
    String comissionCurrency;

    public TLastActiveLongShareInformation(@NonNull Double price,
                                           @NonNull Double comission,
                                           @NonNull String comissionCurrency) {
        updatePrice(price);
        this.comission = comission;
        this.comissionCurrency = comissionCurrency;
    }


    public String toStringPriceTakeProfitAndStopLoss() {
        return TUtils.formatDouble(price) + " / "
                + TUtils.formatDouble(takeProfit) + " / "
                + TUtils.formatDouble(stopLoss);
    }

    public String toStringComission() {
        return TUtils.formatDouble(comission) + " / " + comissionCurrency;
    }

    public void updatePrice(@NonNull Double price) {
        this.price = price;
        this.takeProfit = price + price * TUtils.TAKE_PROFIT_PERCENT;
        this.stopLoss = price - price * TUtils.STOP_LOSS_PERCENT;
    }

    @NonNull
    public String toStringForSave() {
        return price + "," + comission + "," + comissionCurrency;
    }


}
