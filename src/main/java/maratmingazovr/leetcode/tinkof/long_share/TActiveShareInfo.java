package maratmingazovr.leetcode.tinkof.long_share;

import lombok.Data;
import lombok.NonNull;
import maratmingazovr.leetcode.tinkof.TUtils;
import org.jetbrains.annotations.Nullable;
import ru.tinkoff.piapi.contract.v1.CandleInterval;


@Data
public class TActiveShareInfo {

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
    Double rsi;

    @NonNull
    Double rsiPrev;

    @NonNull
    CandleInterval interval;

    public TActiveShareInfo() {
        this.buyPrice = 0.0;
        this.buyTakeProfit = 0.0;
        this.buyStopLoss = 0.0;
        this.sellPrice = 0.0;
        this.sellTakeProfit = 0.0;
        this.sellStopLoss = 0.0;
        this.simpleMovingAverage = 0.0;
        this.bollingerUp = 0.0;
        this.bollingerDown = 0.0;
        this.rsi = 0.0;
        this.rsiPrev = 0.0;
        this.interval = CandleInterval.CANDLE_INTERVAL_UNSPECIFIED;
    }

    public TActiveShareInfo(@NonNull Double buyPrice,
                            @NonNull Double sellPrice,
                            @NonNull Double simpleMovingAverage,
                            @NonNull Double bollingerUp,
                            @NonNull Double bollingerDown,
                            @Nullable Double rsi,
                            @Nullable Double rsiPrev,
                            @NonNull CandleInterval interval) {
        updateBuyPrice(buyPrice);
        updateSellPrice(sellPrice);
        this.simpleMovingAverage = simpleMovingAverage;
        this.bollingerUp = bollingerUp;
        this.bollingerDown = bollingerDown;
        this.rsi = rsi == null ? 0.0 : rsi;
        this.rsiPrev = rsiPrev == null ? 0.0 : rsiPrev;
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
        return buyPrice + ","
                + sellPrice + ","
                + simpleMovingAverage + ","
                + bollingerUp + ","
                + bollingerDown + ","
                + rsi + ","
                + rsiPrev;
    }

    public String toStringBB() {
        return TUtils.formatDouble(simpleMovingAverage) + " / "
                + TUtils.formatDouble(bollingerUp) + " / "
                + TUtils.formatDouble(bollingerDown);
    }

    public String toStringRSI() {
        return TUtils.formatDouble(rsi) + " / "
                + TUtils.formatDouble(rsiPrev);
    }


}
