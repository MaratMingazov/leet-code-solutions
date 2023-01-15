package maratmingazovr.leetcode.tinkof;

import lombok.Data;
import lombok.NonNull;
import maratmingazovr.leetcode.tinkof.long_share.TActiveShare;
import maratmingazovr.leetcode.tinkof.long_share.TActiveShareInfo;
import org.jetbrains.annotations.Nullable;
import ru.tinkoff.piapi.contract.v1.CandleInterval;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class TShare {

    @NonNull
    Map<CandleInterval, List<TCandle>> candlesMap = new HashMap<>();

    @NonNull
    String id;

    @NonNull
    String figi;

    @NonNull
    TActiveShare activeShare = new TActiveShare();

    @NonNull
    TActiveShareInfo activeShareInfo = new TActiveShareInfo();

    @NonNull
    Double actualPrice;

    @NonNull
    Double bbMultiplicatorUp;

    @NonNull
    Double bbMultiplicatorDown;

    public TShare(@NonNull String id,
                  @NonNull String figi) {
        this.id = id;
        this.figi = figi;

        this.bbMultiplicatorUp = 2.0;
        this.bbMultiplicatorDown = 2.0;
        this.actualPrice = 0.0;

        candlesMap.put(CandleInterval.CANDLE_INTERVAL_1_MIN, new ArrayList<>());
        candlesMap.put(CandleInterval.CANDLE_INTERVAL_5_MIN, new ArrayList<>());
        candlesMap.put(CandleInterval.CANDLE_INTERVAL_15_MIN, new ArrayList<>());
        candlesMap.put(CandleInterval.CANDLE_INTERVAL_HOUR, new ArrayList<>());
        candlesMap.put(CandleInterval.CANDLE_INTERVAL_DAY, new ArrayList<>());
    }
}
