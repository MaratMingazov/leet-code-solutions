package maratmingazovr.leetcode.tinkof;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import maratmingazovr.leetcode.tinkof.enums.TCurrency;
import maratmingazovr.leetcode.tinkof.enums.TOperationType;
import maratmingazovr.leetcode.tinkof.long_share.TActiveLongShare;
import maratmingazovr.leetcode.tinkof.long_share.TActiveLongShareInfo;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.tinkoff.piapi.contract.v1.CandleInterval;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.tinkoff.piapi.contract.v1.CandleInterval.CANDLE_INTERVAL_1_MIN;
import static ru.tinkoff.piapi.contract.v1.CandleInterval.CANDLE_INTERVAL_5_MIN;

@Log4j2
@Service
@AllArgsConstructor
public class AnalyzerService {


    private final ApiService apiService;


    private final TPortfolio portfolio = new TPortfolio();

    @NonNull
    private final BotService botService;

    @PostConstruct
    public void init() {
        val accountId = apiService.getAccountFromApi();
        apiService.updatePortfolioFromApi(accountId, portfolio);
        log.info(getPortfolio());
        TUtils.loadLastActiveLongShares(portfolio);
    }

    @Scheduled(cron = "3 0/1  * * * *") // every minute
    public void executeEveryMinute() {
        val accountId = apiService.getAccountFromApi();
        //        apiService.closeSandboxAccount(accountId);
        //        apiService.openSandboxAccount();
        //        log.info("finish");
        //        if(true) {
        //            return;
        //        }

        updateOperations(accountId, portfolio);
        apiService.updatePortfolioFromApi(accountId, portfolio);

//        val interval = CandleInterval.CANDLE_INTERVAL_1_MIN;
        updateSharesFromApi(CANDLE_INTERVAL_1_MIN);
        calculateMetrics(CANDLE_INTERVAL_1_MIN);


        val sharesToSell = findActiveSharesToSellSandbox(portfolio);
        sharesToSell.forEach(activeShare -> apiService.sellShareFromApi(accountId, activeShare.getShareFigi()));

        var candlesToBuyLong = findCandlesToBuyLong(portfolio, CandleInterval.CANDLE_INTERVAL_DAY);
        if (candlesToBuyLong.isEmpty()) {
            candlesToBuyLong = findCandlesToBuyLong(portfolio, CandleInterval.CANDLE_INTERVAL_HOUR);
        }
        if (candlesToBuyLong.isEmpty()) {
            candlesToBuyLong = findCandlesToBuyLong(portfolio, CandleInterval.CANDLE_INTERVAL_15_MIN);
        }
        if (candlesToBuyLong.isEmpty()) {
            candlesToBuyLong = findCandlesToBuyLong(portfolio, CANDLE_INTERVAL_5_MIN);
        }
        if (candlesToBuyLong.size() > 0) {
            log.info("candles to buy = " + candlesToBuyLong.size());
            for (TShareToBuy shareToBuy : candlesToBuyLong) {
                log.info("want to buy: " + shareToBuy.getCandle().getShare().getId() + " / " + shareToBuy.getPriceToBuy());
            }
            buySharesLong(accountId, candlesToBuyLong);
        }
    }

    @Scheduled(cron = "5 0/5  * * * *") // every 5 minutes
    public void executeEvery5Minutes() {
        log.info("start 5 minute");
        val interval = CandleInterval.CANDLE_INTERVAL_5_MIN;
        updateSharesFromApi(interval);
        calculateMetrics(interval);
        log.info("finish 5 minute");
    }

    @Scheduled(cron = "5 0/15  * * * *") // every 15 minutes
    public void executeEvery15Minutes() {
        log.info("start 15 minute");
        val interval = CandleInterval.CANDLE_INTERVAL_15_MIN;
        updateSharesFromApi(interval);
        calculateMetrics(interval);
        log.info("finish 15 minute");
    }

    @Scheduled(cron = "5 0 0/1 * * *")
    public void executeEvery1Hour() {
        log.info("start 1 hour");
        val interval = CandleInterval.CANDLE_INTERVAL_HOUR;
        updateSharesFromApi(interval);
        calculateMetrics(interval);
        log.info("finish 1 hour");
    }

    @Scheduled(cron = "0 0 10 * * *") // every  day 10 o clock
    public void executeEvery1Day() {
        log.info("start 1 day");
        val interval = CandleInterval.CANDLE_INTERVAL_DAY;
        updateSharesFromApi(interval);
        calculateMetrics(interval);
        log.info("finish 1 day");
    }

    public String getStatMessage(@NonNull String shareId,
                                 @NonNull CandleInterval interval,
                                 @NonNull Integer index) {
        for (TShare share : portfolio.getShares()) {
            if (share.getId().toLowerCase().equals(shareId)) {
                val candles = share.getCandlesMap().get(interval);
                if (candles.size() > index ) {
                    return candles.get(index).toString();
                }
            }
        }
        return "nothing";
    }

    private synchronized void buySharesLong(@NonNull String accountId,
                                            @NonNull List<TShareToBuy> sharesToBuy) {
        for (TShareToBuy shareToBuy : sharesToBuy) {
            val candle = shareToBuy.getCandle();
            if(!candle.getShare().getActiveShares().isEmpty()) {
                continue;
            }
            val share = candle.getShare();
            val figi = share.getFigi();
            log.info("want to buy: " + candle.getShare().getId() + " / " + shareToBuy.getPriceToBuy());
            apiService.sendByLimitLongOrder(accountId, figi, shareToBuy.getPriceToBuy());
            val activeLongShareInfo = new TActiveLongShareInfo(share.getId(),
                                                               shareToBuy.getPriceToBuy(),
                                                               candle.getSimpleMovingAverage(),
                                                               candle.getBollingerUp(),
                                                               candle.getBollingerDown(),
                                                               candle.getInterval());
            share.setActiveLongShareInfo(activeLongShareInfo);
            //val stopLoss = apiService.stopLossOrder(accountId, figi, orderPrice);
            //val takeProfit = apiService.takeProfitOrder(accountId, figi, orderPrice);
        }
        TUtils.saveLastActiveLongShares(portfolio);
    }

    public String getPortfolio() {
        return generatePortfolioMessage(portfolio);
    }

    private synchronized void updateOperations(@NonNull String accountId,
                                               @NonNull TPortfolio portfolio) {
        var from = Instant.now().minus(1, ChronoUnit.DAYS);
        val operations = portfolio.getOperations();
        if (portfolio.getOperations().size() > 0) {
            val lastOperation = operations.get(operations.size() - 1);
            from = lastOperation.getInstant().plus(1L, ChronoUnit.SECONDS);
        }
        //log.info("Update operations from: " + from);


        val newOperations = apiService.getOperationsFromApi(accountId, from, portfolio);

        val buyOperations = newOperations.stream().filter(o -> o.getType().equals(TOperationType.BUY)).count();
        val sellOperations = newOperations.stream().filter(o -> o.getType().equals(TOperationType.SELL)).count();
        portfolio.setBuyOperationsCount(portfolio.getBuyOperationsCount() + buyOperations);
        portfolio.setSellOperationsCount(portfolio.getSellOperationsCount() + sellOperations);
        val currentOperations = portfolio.getOperations();
        if (currentOperations.isEmpty()) {
            currentOperations.addAll(newOperations);
            currentOperations.forEach(operation -> botService.sendMassage(operation.toString()));
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
            TUtils.saveLastActiveLongShares(portfolio);
        }

        while(operations.size() > 100) {
            operations.remove(0);
        }
    }

    private List<TActiveLongShare> findActiveSharesToSellSandbox(@NonNull TPortfolio portfolio) {
        List<TActiveLongShare> result = new ArrayList<>();
        for (TShare share : portfolio.getShares()) {
            for (TActiveLongShare activeShare : share.getActiveShares()) {
                val activeLongShareInfo = share.getActiveLongShareInfo();
                val lastActiveLongShareTakeProfit = activeLongShareInfo.getTakeProfit();
                val lastActiveLongShareStopLoss = activeLongShareInfo.getStopLoss();
                if (activeShare.getPrice() > lastActiveLongShareTakeProfit || activeShare.getPrice() < lastActiveLongShareStopLoss) {
                    result.add(activeShare);
                }
            }
        }
        return result;
    }

    private String generatePortfolioMessage(@NonNull TPortfolio portfolio) {
        int totalCandlesCount = 0;
        for (TShare share : portfolio.getShares()) {
            val candles = share.getCandlesMap().values();
            for (List<TCandle> candle : candles) {
                totalCandlesCount += candle.size();
            }
        }


        val activeShares = portfolio.getShares().stream().flatMap(share -> share.getActiveShares().stream()).collect(Collectors.toList());
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
                .append("USD: " + String.format("%.2f", portfolio.getDollarBalance() + usdSharesSum) + "\n")
                .append("RUB: " + String.format("%.2f", portfolio.getRubBalance() + rubSharesSum) + "\n")
                .append("buyOperations: " + portfolio.getBuyOperationsCount() + "\n")
                .append("sellOperations: " + portfolio.getSellOperationsCount() + "\n")
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

    private synchronized void updateSharesFromApi(@NonNull CandleInterval interval) {
        int periodsCount = 100;
        Instant defaultFrom;
        switch (interval) {
            case CANDLE_INTERVAL_1_MIN:
                defaultFrom = Instant.now().minus(periodsCount, ChronoUnit.MINUTES);
                break;
            case CANDLE_INTERVAL_5_MIN:
                defaultFrom = Instant.now().minus(periodsCount * 5, ChronoUnit.MINUTES);
                break;
            case CANDLE_INTERVAL_15_MIN:
                defaultFrom = Instant.now().minus(  20, ChronoUnit.HOURS);
                break;
            case CANDLE_INTERVAL_HOUR:
                defaultFrom = Instant.now().minus(2, ChronoUnit.DAYS);
                break;
            case CANDLE_INTERVAL_DAY:
                defaultFrom = Instant.now().minus(41, ChronoUnit.DAYS);
                //defaultFrom = Instant.now().minus(100, ChronoUnit.DAYS);
                break;
            default: throw new IllegalArgumentException("Invalid candleInterval");
        }
        portfolio.getShares().forEach(share -> addNewCandlesFromApi(share, interval, defaultFrom));
    }

    private void addNewCandlesFromApi(@NonNull TShare share,
                                      @NonNull CandleInterval interval,
                                      @NonNull Instant defaultFrom) {

        var from = defaultFrom;
        val existingCandles = share.getCandlesMap().get(interval);
        if (!existingCandles.isEmpty()) {
            from = existingCandles.get(existingCandles.size()-1).getInstant();
            if (from.isBefore(defaultFrom)) {
                from = defaultFrom;
            }

        }
        val newCandles = apiService.getCandlesFromApi(share, from, interval);
        val candles = share.getCandlesMap().get(interval);
        if (!candles.isEmpty()) {
            val lastCandle = candles.get(candles.size() - 1);
            for (TCandle newCandle : newCandles) {
                if (newCandle.getInstant().isAfter(lastCandle.getInstant())) {
                    candles.add(newCandle);
                }
                if (newCandle.getInstant().equals(lastCandle.getInstant())) {
                    lastCandle.setOpen(newCandle.getOpen());
                    lastCandle.setClose(newCandle.getClose());
                    lastCandle.setHigh(newCandle.getHigh());
                    lastCandle.setLow(newCandle.getLow());
                    lastCandle.setVolume(newCandle.getVolume());
                }
            }
        } else {
            candles.addAll(newCandles);
        }
        while(candles.size() > 100) {
            candles.remove(0);
        }
    }

    private void calculateMetrics(@NonNull CandleInterval interval) {
        portfolio.getShares().forEach(share -> {
            TUtils.calculateSimpleMovingAverage(share, interval);
            TUtils.calculateBollingerUpAndDown(share, interval);
        } );
    }

    @NonNull
    private List<TShareToBuy> findCandlesToBuyLong(@NonNull TPortfolio portfolio,
                                               @NonNull CandleInterval interval) {
        List<TShareToBuy> candlesToBuy = new ArrayList<>();
        if (portfolio.getDollarBalance() < 2000 || portfolio.getRubBalance() < 2000) {
            log.info("balance is low, will not buy");
            return candlesToBuy;
        }
        for (TShare share : portfolio.getShares()) {
            if (!share.getActiveShares().isEmpty()) {
                continue;
            }
            val minuteCandles = share.getCandlesMap().get(CandleInterval.CANDLE_INTERVAL_1_MIN);
            if (minuteCandles.isEmpty()) {
                continue;
            }
            val currentPrice = minuteCandles.get(minuteCandles.size() - 1).getClose();
            val candleToBuyOpt = findCandleToBuyLong(currentPrice, share.getCandlesMap().get(interval));
            candleToBuyOpt.ifPresent(candlesToBuy::add);
        }
        return candlesToBuy;
    }

    @NonNull
    private Optional<TShareToBuy> findCandleToBuyLong(@NonNull Double currentPrice, @NonNull List<TCandle> candles) {
        if (candles.isEmpty()) {
            // we have no candles to check
            return Optional.empty();
        }
        val lastCandle = candles.get(candles.size() - 1);
        if (lastCandle.getBollingerDown() == null) {
            return Optional.empty();
        }
        if (lastCandle.getBollingerUp() == null) {
            return Optional.empty();
        }
        if (currentPrice < lastCandle.getBollingerDown()) {
            //log.info(lastCandle.getShare().getId() + " / " + lastCandle.getInstant() + " / " + lastCandle.getInterval() + " / " + currentPrice + " < " + lastCandle.getBollingerDown() + " check: " + (currentPrice + currentPrice * TUtils.TAKE_PROFIT_PERCENT) + " / " + lastCandle.getBollingerUp());
            if ((currentPrice + currentPrice * TUtils.TAKE_PROFIT_PERCENT) < lastCandle.getBollingerUp()) {
                return Optional.of(new TShareToBuy(lastCandle, currentPrice));
            }
        }
        return Optional.empty();
    }

    public String getCandlesMessage(@NonNull String shareId) {
        StringBuilder result = new StringBuilder();
        for (TShare share : portfolio.getShares()) {
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
