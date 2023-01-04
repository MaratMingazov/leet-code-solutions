package maratmingazovr.leetcode.tinkof;

import lombok.Data;
import lombok.NonNull;
import lombok.val;
import ru.tinkoff.piapi.contract.v1.Operation;

import javax.annotation.Nullable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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

    @NonNull
    TOperationType type;

    @NonNull
    String typeString;

    @NonNull
    TCurrency currency;

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

    @Nullable TShare share;

    @NonNull
    TOperationResultType resultType;

    public TOperation(@NonNull Operation operation,
                      @NonNull TPortfolio portfolio) {
        this.figi = operation.getFigi();
        this.instrumentType = operation.getInstrumentType();
        this.type = TOperationType.getFromString(operation.getType());
        this.typeString = operation.getType();
        this.instant = Instant.ofEpochSecond(operation.getDate().getSeconds(), operation.getDate().getNanos());
        this.currency = TCurrency.getFromString(operation.getCurrency());
        this.price = TUtils.moneyValueToDouble(operation.getPrice());
        this.priceCurrency = operation.getPrice().getCurrency();
        this.payment = TUtils.moneyValueToDouble(operation.getPayment());
        this.paymentCurrency = operation.getPayment().getCurrency();
        this.quantity = operation.getQuantity();
        this.portfolio = portfolio;
        this.resultType = TOperationResultType.OTHER;

        portfolio.getShares().stream()
                 .filter(share -> share.getFigi().equals(operation.getFigi()))
                 .map(TShare::getId).findAny()
                 .ifPresent(shareId -> this.shareId = shareId);

        Optional<TShare> shareOpt = portfolio.getShares().stream().filter(share -> share.getFigi().equals(operation.getFigi())).findAny();
        if (shareOpt.isPresent()) {
            this.share = shareOpt.get();
            if (type.equals(TOperationType.BUY)) {
                share.setLastSharePrice(this.price);
                share.setLastShareTakeProfit(this.price + this.price * TUtils.TAKE_PROFIT_PERCENT);
                share.setLastShareStopLoss(this.price - this.price * TUtils.STOP_LOSS_PERCENT);
            }
            if (type.equals(TOperationType.SELL)) {
                if (this.price > share.getLastSharePrice()) {
                    this.resultType = TOperationResultType.TAKE_PROFIT;
                } else {
                    this.resultType = TOperationResultType.STOP_LOSS;
                }
            }
        }

    }

    @Override
    public String toString() {
        val type = checkStopLossOrTakeProfit();
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy:hh:mm").withZone(ZoneId.systemDefault());
        return "Operation: \n"
                + type
                + "date: " + formatter.format(instant) + "\n"
                + "share: " + shareId + "\n"
                + "type: " + this.typeString  + "\n"
                + "currency: " + currency + "\n"
                + "payment: " + quantity + " * " + price + " = " + payment + "\n";
    }

    private String checkStopLossOrTakeProfit() {
        for (TShare share : portfolio.getShares()) {
            if (share.getFigi().equals(this.figi) && this.type.equals(TOperationType.SELL)) {
                return "type: SELL \n"
                        + "type: " + resultType + "\n"
                        + "buyPrice: " + format(share.getLastSharePrice()) + " / " + format(share.getLastShareTakeProfit()) + " / " + format(share.getLastShareStopLoss()) +  "\n";

            }
            if (share.getFigi().equals(this.figi) && this.type.equals(TOperationType.BUY)) {
                return "type: BUY \n"
                        + "position: " + share.getLastSharePosition() + "\n"
                        + "interval: " + share.getLastShareInterval() + "\n"
                        + "buyPrice: " + format(share.getLastSharePrice()) + " / " + format(share.getLastShareTakeProfit()) + " / " + format(share.getLastShareStopLoss()) +  "\n"
                        + "comission: " + String.format("%.2f", share.getLastShareComission()) + " " + share.getLastShareComissionCurrency() + "\n"
                        + "BB: " + share.getLastShareSMA() + " " + share.getLastShareBollingerUp() + " " + share.getLastShareBollingerDown() + "\n";
            }
        }
        return "";
    }

    private String format(@NonNull Double value) {
        return String.format("%.2f", value);
    }
}
