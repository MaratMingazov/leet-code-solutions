package maratmingazovr.leetcode.tinkof;

import lombok.Data;
import lombok.NonNull;
import lombok.val;
import ru.tinkoff.piapi.contract.v1.CandleInterval;
import ru.tinkoff.piapi.contract.v1.HistoricCandle;

import javax.annotation.Nullable;
import java.time.Instant;

import static ru.tinkoff.piapi.core.utils.MapperUtils.quotationToBigDecimal;

@Data
public class TCandle {

    @NonNull
    Double open;
    @NonNull
    Double close;
    @NonNull
    Double high;
    @NonNull
    Double low;
    @NonNull
    Long volume;
    @NonNull
    Instant instant;



    //////// BB ////////
    @Nullable
    Double simpleMovingAverage;
    @Nullable
    Double bollingerUp;
    @Nullable
    Double bollingerDown;
    //////// BB ////////


    @NonNull
    TShare share;
    @NonNull
    CandleInterval interval;


    //////// RSI ////////

    @Nullable
    Double upWardMove;

    @Nullable
    Double downWardMove;

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

    @Nullable
    Double upWardMoveAverage;

    @Nullable
    Double downWardMoveAverage;
    //////// RSI ////////

    public TCandle(@NonNull HistoricCandle candle,
                   @NonNull TShare share,
                   @NonNull CandleInterval interval) {
        this.open = quotationToBigDecimal(candle.getOpen()).doubleValue();
        this.close = quotationToBigDecimal(candle.getClose()).doubleValue();
        this.high = quotationToBigDecimal(candle.getHigh()).doubleValue();
        this.low = quotationToBigDecimal(candle.getLow()).doubleValue();
        this.volume = candle.getVolume();
        this.instant = Instant.ofEpochSecond(candle.getTime().getSeconds(), candle.getTime().getNanos());
        this.interval = interval;
        this.share = share;
    }

    @Override
    public String toString() {

        val m1Candles = share.getCandlesMap().get(CandleInterval.CANDLE_INTERVAL_1_MIN).size();
        val m5Candles = share.getCandlesMap().get(CandleInterval.CANDLE_INTERVAL_5_MIN).size();
        val m15Candles = share.getCandlesMap().get(CandleInterval.CANDLE_INTERVAL_15_MIN).size();
        val m60Candles = share.getCandlesMap().get(CandleInterval.CANDLE_INTERVAL_HOUR).size();
        val m24Candles = share.getCandlesMap().get(CandleInterval.CANDLE_INTERVAL_DAY).size();

        return "open: " + open + "\n"
                + "close: " + close + "\n"
                + "high: " + high + "\n"
                + "low: " + low + "\n"
                + "vol: " + volume + "\n"
                + "time: " + instant + "\n"
                + "bb: " + TUtils.formatDouble(simpleMovingAverage) + " / " + TUtils.formatDouble(bollingerUp) + " / " + TUtils.formatDouble(bollingerDown) + "\n"
                + "rsi: \n" + toStringRSI() + "\n"
                + "interval: " + interval + "\n"
                + "candles: " + m1Candles + " / " + m5Candles + " / " + m15Candles + " / " + m60Candles + " / " + m24Candles + "\n";
    }

    public String toStringRSI() {
        return TUtils.formatDouble(todayRSI) + " / " + TUtils.formatInstant(todayRSIInstant) + "\n"
                + TUtils.formatDouble(yesterdayRSI) + " / " + TUtils.formatInstant(yesterdayRSIInstant) + "\n"
                + TUtils.formatDouble(lastRSI) + " / " + TUtils.formatInstant(lastRSIInstant);
    }


}
