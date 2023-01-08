package maratmingazovr.leetcode.tinkof;

import lombok.Data;
import lombok.NonNull;
import lombok.val;
import maratmingazovr.leetcode.tinkof.enums.TCurrency;
import maratmingazovr.leetcode.tinkof.enums.TOperationType;
import maratmingazovr.leetcode.tinkof.long_share.TActiveShare;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.tinkoff.piapi.contract.v1.CandleInterval;
import ru.tinkoff.piapi.contract.v1.LastPrice;
import ru.tinkoff.piapi.contract.v1.Operation;
import ru.tinkoff.piapi.core.models.Portfolio;
import ru.tinkoff.piapi.core.models.Position;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.tinkoff.piapi.contract.v1.CandleInterval.CANDLE_INTERVAL_15_MIN;
import static ru.tinkoff.piapi.contract.v1.CandleInterval.CANDLE_INTERVAL_1_MIN;
import static ru.tinkoff.piapi.contract.v1.CandleInterval.CANDLE_INTERVAL_5_MIN;
import static ru.tinkoff.piapi.contract.v1.CandleInterval.CANDLE_INTERVAL_DAY;
import static ru.tinkoff.piapi.contract.v1.CandleInterval.CANDLE_INTERVAL_HOUR;

@Data
public class TPortfolio {

    @NonNull
    private final List<TShare> shares = new ArrayList<>();

    @NonNull
    private final List<TOperation> operations = new ArrayList<>();

    @NonNull
    private Long buyOperationsCount = 0L;

    @NonNull
    private Long sellOperationsCount = 0L;

    @NonNull
    private Double dollarBalance = 0.0;

    @NonNull
    private Double rubBalance = 0.0;

    @NonNull
    private String dollarBalanceFigi = "BBG0013HGFT4";
    @NonNull
    private String rubBalanceFigi = "RUB000UTSTOM";

    private String euroBalanceFigi = "BBG0013HJJ31";

    private static Logger log = LoggerFactory.getLogger(TUtils.class);

    public TPortfolio() {
        shares.add(new TShare("AAPL","BBG000B9XRY4"));
        shares.add(new TShare("TSLA","BBG000N9MNX3"));
        shares.add(new TShare("SBER","BBG004730N88"));
        shares.add(new TShare("VTBR","BBG004730ZJ9"));
        shares.add(new TShare("GAZP","TCSS07661625"));
        shares.add(new TShare("ROSN","BBG004731354"));
        shares.add(new TShare("LKOH","TCS009024277"));
        shares.add(new TShare("BABA","BBG006G2JVL2"));
        shares.add(new TShare("YNDX","BBG006L8G4H1"));
        shares.add(new TShare("INTC","BBG000C0G1D1"));
        shares.add(new TShare("COIN","BBG00ZGF7771"));
        shares.add(new TShare("GTLB","BBG00DHTYPH8"));
        shares.add(new TShare("DOCU","BBG000N7KJX8"));
        shares.add(new TShare("AMD","BBG000BBQCY0"));
        shares.add(new TShare("ATVI","BBG000CVWGS6"));
        shares.add(new TShare("AMZN","BBG000BVPV84"));
        shares.add(new TShare("GOOGL","BBG009S39JX6"));
        shares.add(new TShare("IBM","BBG000BLNNH6"));
    }

    public Optional<TShare> findShareByFigi(@NonNull String figi) {
        return shares.stream()
                     .filter(share -> share.getFigi().equals(figi))
                     .findAny();
    }

    public void calculateMetrics(@NonNull CandleInterval interval) {
        getShares().forEach(share -> {
            TUtils.calculateSimpleMovingAverage(share, interval);
            TUtils.calculateBollingerUpAndDown(share, interval);
        } );
    }

    @NonNull
    public String toStringMessage() {
        int totalCandlesCount = 0;
        for (TShare share : shares) {
            val candles = share.getCandlesMap().values();
            for (List<TCandle> candle : candles) {
                totalCandlesCount += candle.size();
            }
        }

        val activeShares = shares.stream().map(TShare::getActiveShare).collect(Collectors.toList());
        double rubSharesSum = 0.0;
        double usdSharesSum = 0.0;
        for (TActiveShare activeShare : activeShares) {
            if (activeShare.getCurrency().equals(TCurrency.RUB)) {
                rubSharesSum += activeShare.getPrice() * activeShare.getCount();
            }
            if (activeShare.getCurrency().equals(TCurrency.USD)) {
                usdSharesSum += activeShare.getPrice() * activeShare.getCount();
            }
        }

        StringBuilder result = new StringBuilder()
                .append("Balance:" + "\n")
                .append("USD: " + TUtils.formatDouble(dollarBalance + usdSharesSum) + "\n")
                .append("RUB: " + TUtils.formatDouble(rubBalance + rubSharesSum) + "\n")
                .append("buyOperations: " + buyOperationsCount + "\n")
                .append("sellOperations: " + sellOperationsCount + "\n")
                .append("candlesCount: " + totalCandlesCount + "\n");

        result.append("SHARES: \n");
        for (TActiveShare activeShare : activeShares) {
            if (activeShare.getCount() == 0.0) {
                continue;
            }
            val count = activeShare.getCount();
            val price = activeShare.getPrice();
            val total = count * price;
            val currency = activeShare.getCurrency().toString();
            result.append(activeShare.getShareId() + ": " + count + " * " + price + " = " + total + " " + currency + "\n" );
        }
        return result.toString();
    }

    @NonNull
    public String toStringCandles(@NonNull String shareId) {
        StringBuilder result = new StringBuilder();
        for (TShare share : shares) {
            if (share.getId().toLowerCase().equals(shareId)) {
                val candles = share.getCandlesMap().get(CANDLE_INTERVAL_1_MIN);
                for (TCandle candle : candles) {
                    result
                            .append(candle.getInstant()).append(" / ")
                            .append(candle.getOpen()).append(" / ")
                            .append(candle.getClose()).append(" / ")
                            .append(candle.getSimpleMovingAverage()).append(" / ")
                            .append(candle.getBollingerUp()).append(" / ")
                            .append(candle.getBollingerDown()).append("\n");
                }
            }
        }
        return result.toString();
    }

    public void updatePortfolio(@NonNull Portfolio portfolio) {
        val positions = portfolio.getPositions();
        for (TShare share : shares) {
            share.setActiveShare(new TActiveShare());
        }
        for (Position position : positions) {
            val figi = position.getFigi();
            double count = position.getQuantity().doubleValue();
            if (count <= 0) {
                continue;
            }
            val price = position.getCurrentPrice().getValue().doubleValue();
            val currencyString = position.getCurrentPrice().getCurrency();
            val currency = TCurrency.getFromString(currencyString);
            if (figi.equals(dollarBalanceFigi)) {
                dollarBalance = count;
            }
            if (figi.equals(rubBalanceFigi)) {
                rubBalance = count;
            }
            for (TShare share : shares) {
                if (figi.equals(share.getFigi())) {
                    share.setActiveShare(new TActiveShare(share.getId(),
                                                              share.getFigi(),
                                                              currency,
                                                              price,
                                                              count,
                                                              Instant.now()));
                }
            }
        }
    }

    @NonNull
    public Instant getLastOperationDate() {
        if (operations.size() > 0) {
            val lastOperation = operations.get(operations.size() - 1);
            return lastOperation.getInstant().plus(1L, ChronoUnit.SECONDS);
        }
        return Instant.now().minus(1, ChronoUnit.DAYS);
    }


    public void updateOperations(@NonNull List<Operation> newOperationsFromApi,
                                 @NonNull BotService botService) {
        val newOperations = newOperationsFromApi.stream()
                                                .map(operation -> new TOperation(operation, this))
                                                .collect(Collectors.toList());

        buyOperationsCount += newOperations.stream().filter(o -> o.getType().equals(TOperationType.BUY)).count();
        sellOperationsCount += newOperations.stream().filter(o -> o.getType().equals(TOperationType.SELL)).count();


        if (operations.isEmpty()) {
            operations.addAll(newOperations);
            operations.forEach(operation -> botService.sendMassage(operation.toString()));
        } else {
            val lastOperation = operations.get(operations.size() - 1);
            for (TOperation newOperation : newOperations) {
                if (newOperation.getInstant().isAfter(lastOperation.getInstant())) {
                    operations.add(newOperation);
                    botService.sendMassage(newOperation.toString());
                }
            }
        }

        if (newOperations.size() > 0) {
            log.info("Got new operations: " + newOperations.size());
            for (TOperation newOperation : newOperations) {
                log.info(newOperation.getInstant() + " / " + newOperation.getShareId() + " / " + newOperation.getType() + " / " + newOperation.getPrice() + " / " + newOperation.getCurrency());
            }
            TUtils.saveLastActiveLongShares(this);
        }

        while(operations.size() > 100) {
            operations.remove(0);
        }
    }

    public void updateLastPrices(@NonNull List<LastPrice> lastPrices) {
        for (LastPrice lastPrice : lastPrices) {
            for (TShare share : shares) {
                if (share.getFigi().equals(lastPrice.getFigi())) {
                    val activeShare = share.getActiveShare();
                    activeShare.setPrice(TUtils.QuotationToDouble(lastPrice.getPrice()));
                    activeShare.setUpdateTime(TUtils.timeStampToInstant(lastPrice.getTime()));
                }
            }

            Double price = TUtils.QuotationToDouble(lastPrice.getPrice());
            Instant instant = TUtils.timeStampToInstant(lastPrice.getTime());
            updateLastCandle(CANDLE_INTERVAL_1_MIN, instant.truncatedTo(ChronoUnit.MINUTES), price);
            updateLastCandle(CANDLE_INTERVAL_5_MIN, TUtils.getTruncatedTo5Min(instant), price);
            updateLastCandle(CANDLE_INTERVAL_15_MIN, TUtils.getTruncatedTo15Min(instant), price);
            updateLastCandle(CANDLE_INTERVAL_HOUR, instant.truncatedTo(ChronoUnit.HOURS), price);
            updateLastCandle(CANDLE_INTERVAL_DAY, instant.truncatedTo(ChronoUnit.DAYS), price);
        }
    }

    private void updateLastCandle(@NonNull CandleInterval interval,
                                  @NonNull Instant instant,
                                  @NonNull Double price) {
        for (TShare share : shares) {
            val candles = share.getCandlesMap().get(interval);
            if (candles.isEmpty()) {
                continue;
            }
            val lastCandle = candles.get(candles.size() - 1);
            if (lastCandle.getInstant().equals(instant)) {
                lastCandle.setClose(price);
                if (lastCandle.getHigh() < price) {
                    lastCandle.setHigh(price);
                }
                if (lastCandle.getLow() > price) {
                    lastCandle.setLow(price);
                }
            }
        }
    }


}
