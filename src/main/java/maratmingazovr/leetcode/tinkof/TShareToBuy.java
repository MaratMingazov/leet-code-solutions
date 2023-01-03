package maratmingazovr.leetcode.tinkof;

import lombok.Data;
import lombok.NonNull;

@Data
public class TShareToBuy {

    @NonNull
    Double priceToBuy;

    @NonNull
    TCandle candle;

    public TShareToBuy(@NonNull TCandle candle,
                       @NonNull Double priceToBuy) {
        this.candle = candle;
        this.priceToBuy = priceToBuy;
    }


}
