package maratmingazovr.leetcode.tinkof.long_share;

import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.Data;
import lombok.NonNull;
import maratmingazovr.leetcode.tinkof.enums.TCurrency;

import java.time.Instant;

@Data
public class TActiveLongShare {

    @NonNull
    String shareId;

    @NonNull
    String shareFigi;

    @NonNull
    Double price;

    @NonNull
    Double count;

    @NonNull
    TCurrency currency;

    @NonNull Instant updateTime;


    public TActiveLongShare(@NonNull String shareId,
                            @NonNull String shareFigi,
                            @NonNull TCurrency currency,
                            @NonNull Double price,
                            @NonNull Double count,
                            @NonNull Instant updateTime) {
        this.shareId = shareId;
        this.shareFigi = shareFigi;
        this.price = price;
        this.count = count;
        this.currency = currency;
        this.updateTime = updateTime;
    }

    public TActiveLongShare() {
        this.shareId = "";
        this.shareFigi = "";
        this.price = 0.0;
        this.count = 0.0;
        this.currency = TCurrency.USD;
        this.updateTime = Instant.now();
    }
}
