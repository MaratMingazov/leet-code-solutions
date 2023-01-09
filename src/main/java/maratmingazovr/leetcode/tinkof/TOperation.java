package maratmingazovr.leetcode.tinkof;

import lombok.Data;
import lombok.NonNull;
import lombok.val;
import maratmingazovr.leetcode.tinkof.enums.TCurrency;
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
    Optional<TShare> shareOpt;

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
        this.shareOpt = portfolio.findShareByFigi(this.figi);

        portfolio.getShares().stream()
                 .filter(share -> share.getFigi().equals(operation.getFigi()))
                 .map(TShare::getId).findAny()
                 .ifPresent(shareId -> this.shareId = shareId);

        if (shareOpt.isPresent()) {
            val activeShareInfo = shareOpt.get().getActiveShareInfo();
            if (type.equals(TOperationType.BUY)) {
                activeShareInfo.updateBuyPrice(this.price);
            }
            if (type.equals(TOperationType.SELL)) {
                activeShareInfo.updateSellPrice(this.price);
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
        if (this.shareOpt.isPresent()) {
            val share = shareOpt.get();
            val activeShare = share.getActiveShare();
            val activeShareInfo = share.getActiveShareInfo();

            if (type.equals(TOperationType.SELL)) {
                if (activeShare.getCount() < 0.0) {
                    // мы купили short
                    return "ENTER SHORT \n"
                            + "interval: " + activeShareInfo.getInterval() + "\n"
                            + "buyInfo: " + activeShareInfo.toStringSellPriceTakeProfitAndStopLoss() +  "\n"
                            + "BB: " + activeShareInfo.toStringBB()
                            + "RSI" + activeShareInfo.toStringRSI() + "\n";
                } else {
                    //мы закрыли long
                    String takeProfitStopLoss = "";
                    if (this.price >= activeShareInfo.getBuyPrice()) {
                        takeProfitStopLoss = "TAKE_PROFIT";
                    } else {
                        takeProfitStopLoss = "STOP_LOSS";
                    }
                    return "EXIT LONG " + takeProfitStopLoss + "\n"
                            + "buyInfo: " + activeShareInfo.toStringBuyPriceTakeProfitAndStopLoss() +  "\n";
                }
            }
            if (type.equals(TOperationType.BUY)) {
                if (activeShare.getCount() > 0.0) {
                    // мы купили long
                    return "ENTER LONG \n"
                            + "interval: " + activeShareInfo.getInterval() + "\n"
                            + "buyInfo: " + activeShareInfo.toStringBuyPriceTakeProfitAndStopLoss() +  "\n"
                            + "BB: " + activeShareInfo.toStringBB() + "\n"
                            + "RSI" + activeShareInfo.toStringRSI() + "\n";
                } else {
                    //мы закрыли short
                    String takeProfitStopLoss = "";
                    if (this.price <= activeShareInfo.getSellPrice()) {
                        takeProfitStopLoss = "TAKE_PROFIT";
                    } else {
                        takeProfitStopLoss = "STOP_LOSS";
                    }
                    return "EXIT SHORT " + takeProfitStopLoss + "\n"
                            + "buyInfo: " + activeShareInfo.toStringSellPriceTakeProfitAndStopLoss() +  "\n";
                }
            }
        }
        return "";
    }
}
