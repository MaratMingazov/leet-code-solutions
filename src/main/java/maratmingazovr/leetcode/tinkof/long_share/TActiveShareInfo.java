package maratmingazovr.leetcode.tinkof.long_share;

import lombok.Data;
import lombok.NonNull;
import maratmingazovr.leetcode.tinkof.TUtils;
import org.jetbrains.annotations.Nullable;
import ru.tinkoff.piapi.contract.v1.CandleInterval;

import java.time.Instant;


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

    @Nullable
    Double todayRSI;

    @Nullable
    Instant todayRSIInstant;

    @Nullable
    Double yesterdayRSI;

    @Nullable
    Instant yesterdayRSIInstant;

    @Nullable
    Double lastRSI;

    @Nullable
    Instant lastRSIInstant;

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
        this.todayRSI = 0.0;
        this.yesterdayRSI = 0.0;
        this.lastRSI = 0.0;
        this.todayRSIInstant = Instant.now();
        this.yesterdayRSIInstant = Instant.now();
        this.lastRSIInstant = Instant.now();
        this.interval = CandleInterval.CANDLE_INTERVAL_UNSPECIFIED;
    }

    public TActiveShareInfo(@NonNull Double buyPrice,
                            @NonNull Double sellPrice,
                            @NonNull Double simpleMovingAverage,
                            @NonNull Double bollingerUp,
                            @NonNull Double bollingerDown,
                            @Nullable Double todayRSI,
                            @Nullable Double yesterdayRSI,
                            @Nullable Double lastRSI,
                            @Nullable Instant todayRSIInstant,
                            @Nullable Instant yesterdayRSIInstant,
                            @Nullable Instant lastRSIInstant,
                            @NonNull CandleInterval interval) {
        updateBuyPrice(buyPrice);
        updateSellPrice(sellPrice);
        this.simpleMovingAverage = simpleMovingAverage;
        this.bollingerUp = bollingerUp;
        this.bollingerDown = bollingerDown;
        this.todayRSI = todayRSI == null ? 0.0 : todayRSI;
        this.yesterdayRSI = yesterdayRSI == null ? 0.0 : yesterdayRSI;
        this.lastRSI = lastRSI == null ? 0.0 : lastRSI;
        this.todayRSIInstant = todayRSIInstant == null ? Instant.now() : todayRSIInstant;
        this.yesterdayRSIInstant = yesterdayRSIInstant == null ? Instant.now() : yesterdayRSIInstant;
        this.lastRSIInstant = lastRSIInstant == null ? Instant.now() : lastRSIInstant;
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
        return buyPrice + "," + sellPrice;
    }

    public String toStringBB() {
        return TUtils.formatDouble(simpleMovingAverage) + " / "
                + TUtils.formatDouble(bollingerUp) + " / "
                + TUtils.formatDouble(bollingerDown);
    }

    public String toStringRSI() {
        return TUtils.formatDouble(todayRSI) + " / " + TUtils.formatInstant(todayRSIInstant) + "\n"
                + TUtils.formatDouble(yesterdayRSI) + " / " + TUtils.formatInstant(yesterdayRSIInstant) + "\n"
                + TUtils.formatDouble(lastRSI) + " / " + TUtils.formatInstant(lastRSIInstant);
    }


}
