package maratmingazovr.leetcode.tinkof.long_share;

import lombok.Data;
import lombok.NonNull;
import maratmingazovr.leetcode.tinkof.TCandle;

@Data
public class TShareToBuy {

    @NonNull
    Double priceToBuy;

    @NonNull TCandle candle;

    public TShareToBuy(@NonNull TCandle candle,
                       @NonNull Double priceToBuy) {
        this.candle = candle;
        this.priceToBuy = priceToBuy;
    }


}
