package maratmingazovr.leetcode.tinkof;

import lombok.Data;
import lombok.NonNull;
import lombok.val;
import ru.tinkoff.piapi.contract.v1.Operation;

import javax.annotation.Nullable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

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
    String type;

    @NonNull
    String currency;

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

    public TOperation(@NonNull Operation operation,
                      @NonNull TPortfolio portfolio) {
        this.figi = operation.getFigi();
        this.instrumentType = operation.getInstrumentType();
        this.type = operation.getType();
        this.instant = Instant.ofEpochSecond(operation.getDate().getSeconds(), operation.getDate().getNanos());
        this.currency = operation.getCurrency();
        this.price = TUtils.moneyValueToDouble(operation.getPrice());
        this.priceCurrency = operation.getPrice().getCurrency();
        this.payment = TUtils.moneyValueToDouble(operation.getPayment());
        this.paymentCurrency = operation.getPayment().getCurrency();
        this.quantity = operation.getQuantity();
        this.portfolio = portfolio;

        portfolio.getShares().stream()
                 .filter(share -> share.getFigi().equals(operation.getFigi()))
                 .map(TShare::getId).findAny()
                 .ifPresent(shareId -> this.shareId = shareId);

    }

    @Override
    public String toString() {
        val type = checkStopLossOrTakeProfit();
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy:hh:mm").withZone(ZoneId.systemDefault());
        return "Operation: \n"
                + type
                + "date: " + formatter.format(instant) + "\n"
                + "share: " + shareId + "\n"
                + "type: " + instrumentType + "\n"
                + "type: " + this.type  + "\n"
                + "currency: " + currency + "\n"
                + "quantity: " + quantity + "\n"
                + "price: " + price + " " + priceCurrency + "\n"
                + "payment: " + payment + " " + paymentCurrency + "\n";
    }

    private String checkStopLossOrTakeProfit() {
        for (TShare share : portfolio.getShares()) {
            if (share.getFigi().equals(this.figi) && this.type.toLowerCase().contains("продажа")) {
                if (this.price > share.getLastSharePrice()) {
                    return "type: TAKE_PROFIT \n"
                            + "buyPrice: " + String.format("%.2f", share.getLastSharePrice()) + "\n"
                            + "takeProfit: " + String.format("%.2f", share.getLastShareTakeProfit()) + "\n"
                            + "stopLoss: " + String.format("%.2f", share.getLastShareStopLoss()) + "\n";
                }
                if (this.price < share.getLastSharePrice()) {
                    return "type: STOP_LOSS + \n "
                            + "buyPrice: " + String.format("%.2f", share.getLastSharePrice()) + "\n"
                            + "takeProfit: " + String.format("%.2f", share.getLastShareTakeProfit()) + "\n"
                            + "stopLoss: " + String.format("%.2f", share.getLastShareStopLoss()) + "\n";
                }
            }
            if (share.getFigi().equals(this.figi) && this.type.toLowerCase().contains("покупка")) {
                return "type: BUY \n"
                        + "position: " + share.getLastSharePosition() + "\n"
                        + "interval: " + share.getLastShareInterval() + "\n"
                        + "buyPrice: " + String.format("%.2f", share.getLastSharePrice()) + "\n"
                        + "comission: " + String.format("%.2f", share.getLastShareComission()) + " " + share.getLastShareComissionCurrency() + "\n"
                        + "takeProfit: " + String.format("%.2f", share.getLastShareTakeProfit()) + "\n"
                        + "stopLoss: " + String.format("%.2f", share.getLastShareStopLoss()) + "\n"
                        + "BB: " + share.getLastShareSMA() + " " + share.getLastShareBollingerUp() + " " + share.getLastShareBollingerDown() + "\n";
            }
        }
        return "";
    }
}
