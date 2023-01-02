package maratmingazovr.leetcode.tinkof;

import lombok.Data;
import lombok.NonNull;
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

    @Nullable
    Double simpleMovingAverage;
    @Nullable
    Double bollingerUp;
    @Nullable
    Double bollingerDown;


    @NonNull
    TShare share;
    @NonNull
    CandleInterval interval;

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


}
