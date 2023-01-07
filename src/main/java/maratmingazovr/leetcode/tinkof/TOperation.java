package maratmingazovr.leetcode.tinkof;

import lombok.Data;
import lombok.NonNull;
import lombok.val;
import maratmingazovr.leetcode.tinkof.enums.TCurrency;
import maratmingazovr.leetcode.tinkof.enums.TOperationSellResult;
import maratmingazovr.leetcode.tinkof.enums.TOperationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.tinkoff.piapi.contract.v1.Operation;

import javax.annotation.Nullable;
import java.time.Instant;
import java.util.Optional;

@Data
public class TOperation {

    @NonNull
    String figi;

    @Nullable
    String shareId;

    @NonNull
    String instrumentType;

    @NonNull
    Instant instant;

    @NonNull TOperationType type;

    @NonNull
    String typeString;

    @NonNull TCurrency currency;

    @NonNull
    Double price;

    @NonNull
    String priceCurrency;

    @NonNull
    Double payment;

    @NonNull
    String paymentCurrency;

    @NonNull
    Long quantity;

    @NonNull TPortfolio portfolio;

    @Nullable
    Optional<TShare> share;

    @NonNull TOperationSellResult sellResult;

    private static Logger log = LoggerFactory.getLogger(TOperation.class);

    public TOperation(@NonNull Operation operation,
                      @NonNull TPortfolio portfolio) {
        this.figi = operation.getFigi();
        this.instrumentType = operation.getInstrumentType();
        this.type = TOperationType.getFromString(operation.getType());
        this.typeString = operation.getType();
        this.instant = TUtils.timeStampToInstant(operation.getDate());
        this.currency = TCurrency.getFromString(operation.getCurrency());
        this.price = TUtils.moneyValueToDouble(operation.getPrice());
        this.priceCurrency = operation.getPrice().getCurrency();
        this.payment = TUtils.moneyValueToDouble(operation.getPayment());
        this.paymentCurrency = operation.getPayment().getCurrency();
        this.quantity = operation.getQuantity();
        this.portfolio = portfolio;
        this.sellResult = TOperationSellResult.OTHER;

        portfolio.getShares().stream()
                 .filter(share -> share.getFigi().equals(operation.getFigi()))
                 .map(TShare::getId).findAny()
                 .ifPresent(shareId -> this.shareId = shareId);

        Optional<TShare> shareOpt = portfolio.getShares().stream().filter(share -> share.getFigi().equals(operation.getFigi())).findAny();
        if (shareOpt.isPresent()) {
            val share = shareOpt.get();
            this.share = Optional.of(share);
            if (type.equals(TOperationType.BUY)) {
                val activeLongShareInfo =  share.getActiveLongShareInfo();
                activeLongShareInfo.updatePrice(this.price);
            }
            if (type.equals(TOperationType.SELL)) {
                val activeLongShareInfoPrice = share.getActiveLongShareInfo().getPrice();
                if (activeLongShareInfoPrice.equals(0.0)) {
                    this.sellResult = TOperationSellResult.OTHER;
                } else if (this.price > activeLongShareInfoPrice) {
                    this.sellResult = TOperationSellResult.TAKE_PROFIT;
                } else {
                    this.sellResult = TOperationSellResult.STOP_LOSS;
                }
            }
        }

    }

    @Override
    public String toString() {
        val type = checkStopLossOrTakeProfit();
        return "Operation: \n"
                + type
                + "date: " + TUtils.TIME_FORMATTER.format(instant) + "\n"
                + "share: " + shareId + "\n"
                + "type: " + this.typeString  + "\n"
                + "currency: " + currency + "\n"
                + "payment: " + quantity + " * " + price + " = " + payment + "\n";
    }

    private String checkStopLossOrTakeProfit() {

        val shareOptional = portfolio.findShareByFigi(this.figi);
        if (shareOptional.isPresent()) {
            val activeLongShareInfo = shareOptional.get().getActiveLongShareInfo();
            if (this.type.equals(TOperationType.SELL)) {
                return "type: SELL \n"
                        + "sellResult: " + sellResult + "\n"
                        + "buyInfo: " + activeLongShareInfo.toStringPriceTakeProfitAndStopLoss() +  "\n";

            }
            if (this.type.equals(TOperationType.BUY)) {
                return "type: BUY \n"
                        + "position: " + "LONG" + "\n"
                        + "interval: " + activeLongShareInfo.getInterval() + "\n"
                        + "buyInfo: " + activeLongShareInfo.toStringPriceTakeProfitAndStopLoss() +  "\n"
                        + "BB: " + activeLongShareInfo.toStringBB() + "\n";
            }
        }
        return "";
    }
}
