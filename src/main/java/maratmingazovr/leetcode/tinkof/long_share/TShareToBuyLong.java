package maratmingazovr.leetcode.tinkof.long_share;

import lombok.Data;
import lombok.NonNull;
import maratmingazovr.leetcode.tinkof.TCandle;

@Data
public class TShareToBuyLong {

    @NonNull
    Double priceToBuy;

    @NonNull TCandle candle;

    public TShareToBuyLong(@NonNull TCandle candle,
                           @NonNull Double priceToBuy) {
        this.candle = candle;
        this.priceToBuy = priceToBuy;
    }


}
