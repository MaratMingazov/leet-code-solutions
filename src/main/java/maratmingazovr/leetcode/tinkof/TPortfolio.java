package maratmingazovr.leetcode.tinkof;

import lombok.Data;
import lombok.NonNull;
import lombok.val;
import maratmingazovr.leetcode.tinkof.enums.TCurrency;
import maratmingazovr.leetcode.tinkof.long_share.TActiveLongShare;
import ru.tinkoff.piapi.contract.v1.CandleInterval;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.tinkoff.piapi.contract.v1.CandleInterval.CANDLE_INTERVAL_1_MIN;

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
        for (TActiveLongShare activeShare : activeShares) {
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
        for (TActiveLongShare activeShare : activeShares) {
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


}
