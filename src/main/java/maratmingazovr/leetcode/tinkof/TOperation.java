package maratmingazovr.leetcode.tinkof;

import lombok.Data;
import lombok.NonNull;
import lombok.val;
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
        this.instant = Instant.ofEpochSecond(operation.getDate().getSeconds(), operation.getDate().getNanos());
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
                val lastActiveLongShareInformationOptional =  share.getLastLongShareInformation();
                if (lastActiveLongShareInformationOptional.isPresent()) {
                    val information = lastActiveLongShareInformationOptional.get();
                    information.updatePrice(this.price);
                } else {
                    log.info("Exception. TOperation. Buy share. but lastActiveLongShare does not exists. share = " + share.getId() + " / price = " + this.price);
                }
            }
            if (type.equals(TOperationType.SELL)) {
                val lastShareInformationOptional = share.getLastLongShareInformation();
                if (lastShareInformationOptional.isPresent()) {
                    val lastShareInformation = lastShareInformationOptional.get();
                    val lastSharePrice = lastShareInformation.getPrice();
                    if (this.price > lastSharePrice) {
                        this.sellResult = TOperationSellResult.TAKE_PROFIT;
                    } else {
                        this.sellResult = TOperationSellResult.STOP_LOSS;
                    }
                } else {
                    this.sellResult = TOperationSellResult.OTHER;
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
            val share = shareOptional.get();
            String pricaeTakeProfitStopLoss = "-";
            String comission = "-";
            val lastLongShareInformationOptional = share.getLastLongShareInformation();
            if (lastLongShareInformationOptional.isPresent()) {
                val information = lastLongShareInformationOptional.get();
                pricaeTakeProfitStopLoss = information.toStringPriceTakeProfitAndStopLoss();
                comission = information.toStringComission();
            }

            if (this.type.equals(TOperationType.SELL)) {
                return "type: SELL \n"
                        + "sellResult: " + sellResult + "\n"
                        + "buyInfo: " + pricaeTakeProfitStopLoss +  "\n";

            }
            if (this.type.equals(TOperationType.BUY)) {
                return "type: BUY \n"
                        + "position: " + share.getLastSharePosition() + "\n"
                        + "interval: " + share.getLastShareInterval() + "\n"
                        + "buyInfo: " + pricaeTakeProfitStopLoss +  "\n"
                        + "comission: " + comission + "\n"
                        + "BB: " + share.getLastShareSMA() + " " + share.getLastShareBollingerUp() + " " + share.getLastShareBollingerDown() + "\n";
            }
        }
        return "";
    }
}
