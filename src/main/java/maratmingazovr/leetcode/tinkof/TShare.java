package maratmingazovr.leetcode.tinkof;

import lombok.Data;
import lombok.NonNull;
import lombok.val;
import ru.tinkoff.piapi.contract.v1.CandleInterval;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Data
public class TShare {

    @NonNull
    Map<CandleInterval, List<TCandle>> candlesMap = new HashMap<>();

    @NonNull
    List<TActiveShare> activeShares = new ArrayList<>();

    @NonNull
    String id;

    @NonNull
    String figi;

//    @NonNull
//    Double lastSharePrice = 0.0;

//    @NonNull
//    Double lastShareTakeProfit = 0.0;

//    @NonNull
//    Double lastShareStopLoss = 0.0;

//    @NonNull
//    Double lastShareComission = 0.0;

//    @NonNull
//    String lastShareComissionCurrency = "";

    @NonNull
    String lastSharePosition;

    @NonNull
    String lastShareSMA;

    @NonNull
    String lastShareBollingerUp;

    @NonNull
    String lastShareInterval = "";

    @NonNull
    String lastShareBollingerDown;

    @NonNull
    TLastActiveLongShareInformation lastLongShareInformation = new TLastActiveLongShareInformation();

    public TShare(@NonNull String id,
                  @NonNull String figi) {
        this.id = id;
        this.figi = figi;

        candlesMap.put(CandleInterval.CANDLE_INTERVAL_1_MIN, new ArrayList<>());
        candlesMap.put(CandleInterval.CANDLE_INTERVAL_5_MIN, new ArrayList<>());
        candlesMap.put(CandleInterval.CANDLE_INTERVAL_15_MIN, new ArrayList<>());
        candlesMap.put(CandleInterval.CANDLE_INTERVAL_HOUR, new ArrayList<>());
        candlesMap.put(CandleInterval.CANDLE_INTERVAL_DAY, new ArrayList<>());
    }
}
