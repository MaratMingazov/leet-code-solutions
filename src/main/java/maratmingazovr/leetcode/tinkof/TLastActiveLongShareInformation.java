package maratmingazovr.leetcode.tinkof;

import lombok.Data;
import lombok.NonNull;
import ru.tinkoff.piapi.contract.v1.CandleInterval;


@Data
public class TLastActiveLongShareInformation {

    @NonNull
    Double price;

    @NonNull
    Double takeProfit;

    @NonNull
    Double stopLoss;

    @NonNull
    Double simpleMovingAverage;

    @NonNull
    Double bollingerUp;

    @NonNull
    Double bollingerDown;

    @NonNull
    CandleInterval interval;

    public TLastActiveLongShareInformation() {
        this.price = 0.0;
        this.takeProfit = 0.0;
        this.stopLoss = 0.0;
        this.simpleMovingAverage = 0.0;
        this.bollingerUp = 0.0;
        this.bollingerDown = 0.0;
        this.interval = CandleInterval.CANDLE_INTERVAL_UNSPECIFIED;
    }

    public TLastActiveLongShareInformation(@NonNull Double price,
                                           @NonNull Double simpleMovingAverage,
                                           @NonNull Double bollingerUp,
                                           @NonNull Double bollingerDown,
                                           @NonNull CandleInterval interval) {
        updatePrice(price);
        this.simpleMovingAverage = simpleMovingAverage;
        this.bollingerUp = bollingerUp;
        this.bollingerDown = bollingerDown;
        this.interval = interval;
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
    public String toStringForSave() {
        return price + "," + simpleMovingAverage + "," + bollingerUp + "," + bollingerDown;
    }

    public String toStringBB() {
        return TUtils.formatDouble(simpleMovingAverage) + " / "
                + TUtils.formatDouble(bollingerUp) + " / "
                + TUtils.formatDouble(bollingerDown);
    }


}
