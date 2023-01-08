package maratmingazovr.leetcode.tinkof.long_share;

import lombok.Data;
import lombok.NonNull;
import maratmingazovr.leetcode.tinkof.TUtils;
import ru.tinkoff.piapi.contract.v1.CandleInterval;


@Data
public class TActiveShareInfo {

    @NonNull
    String shareId;

    @NonNull
    Double buyPrice;



    @NonNull
    Double buyTakeProfit;

    @NonNull
    Double buyStopLoss;

    @NonNull
    Double sellPrice;

    @NonNull
    Double sellTakeProfit;

    @NonNull
    Double sellStopLoss;

    @NonNull
    Double simpleMovingAverage;

    @NonNull
    Double bollingerUp;

    @NonNull
    Double bollingerDown;

    @NonNull
    CandleInterval interval;

    public TActiveShareInfo() {
        this.shareId = "";
        this.buyPrice = 0.0;
        this.buyTakeProfit = 0.0;
        this.buyStopLoss = 0.0;
        this.sellPrice = 0.0;
        this.sellTakeProfit = 0.0;
        this.sellStopLoss = 0.0;
        this.simpleMovingAverage = 0.0;
        this.bollingerUp = 0.0;
        this.bollingerDown = 0.0;
        this.interval = CandleInterval.CANDLE_INTERVAL_UNSPECIFIED;
    }

    public TActiveShareInfo(@NonNull String shareId,
                            @NonNull Double buyPrice,
                            @NonNull Double sellPrice,
                            @NonNull Double simpleMovingAverage,
                            @NonNull Double bollingerUp,
                            @NonNull Double bollingerDown,
                            @NonNull CandleInterval interval) {
        this.shareId = shareId;
        updateBuyPrice(buyPrice);
        updateSellPrice(sellPrice);
        this.simpleMovingAverage = simpleMovingAverage;
        this.bollingerUp = bollingerUp;
        this.bollingerDown = bollingerDown;
        this.interval = interval;
    }


    public String toStringBuyPriceTakeProfitAndStopLoss() {
        return TUtils.formatDouble(buyPrice) + " / "
                + TUtils.formatDouble(buyTakeProfit) + " / "
                + TUtils.formatDouble(buyStopLoss);
    }

    public String toStringSellPriceTakeProfitAndStopLoss() {
        return TUtils.formatDouble(sellPrice) + " / "
                + TUtils.formatDouble(sellTakeProfit) + " / "
                + TUtils.formatDouble(sellStopLoss);
    }

    public void updateBuyPrice(@NonNull Double buyPrice) {
        this.buyPrice = buyPrice;
        this.buyTakeProfit = buyPrice + buyPrice * TUtils.TAKE_PROFIT_PERCENT;
        this.buyStopLoss = buyPrice - buyPrice * TUtils.STOP_LOSS_PERCENT;
    }

    public void updateSellPrice(@NonNull Double sellPrice) {
        this.sellPrice = sellPrice;
        this.sellTakeProfit = sellPrice - sellPrice * TUtils.TAKE_PROFIT_PERCENT;
        this.sellStopLoss = sellPrice + sellPrice * TUtils.STOP_LOSS_PERCENT;
    }

    @NonNull
    public String toStringForSave() {
        return shareId + "," + buyPrice + "," + sellPrice + "," + simpleMovingAverage + "," + bollingerUp + "," + bollingerDown;
    }

    public String toStringBB() {
        return TUtils.formatDouble(simpleMovingAverage) + " / "
                + TUtils.formatDouble(bollingerUp) + " / "
                + TUtils.formatDouble(bollingerDown);
    }


}
