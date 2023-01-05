package maratmingazovr.leetcode.tinkof;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import maratmingazovr.leetcode.neural_network.NetworkConfiguration;
import maratmingazovr.leetcode.neural_network.Util;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.tinkoff.piapi.contract.v1.CandleInterval;
import ru.tinkoff.piapi.contract.v1.PostOrderResponse;
import ru.tinkoff.piapi.contract.v1.StopOrder;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
        String filename = "src/main/java/maratmingazovr/leetcode/tinkof/data.txt";
        val shares = Util.loadCSV(filename);
        int count = 0;
        for (List<String> share : shares) {
            if (share.size() < 2) {
                continue;
            }
            val shareId = share.get(0);
            val shareBuyPrice = Double.valueOf(share.get(1));
            for (TShare portfolioShare : portfolio.getShares()) {
                if (portfolioShare.getId().equals(shareId)) {
                    portfolioShare.setLastSharePrice(shareBuyPrice);
                    portfolioShare.setLastShareTakeProfit(shareBuyPrice + shareBuyPrice * TUtils.TAKE_PROFIT_PERCENT);
                    portfolioShare.setLastShareStopLoss(shareBuyPrice - shareBuyPrice * TUtils.TAKE_PROFIT_PERCENT);
                    count++;
                }
            }
        }
        log.info("load shares = " + count);
    }

    @Scheduled(cron = "0/30 *  * * * *") // every minute
    public void executeEveryMinute() {
        val accountId = apiService.getAccountFromApi();
        //        apiService.closeSandboxAccount(accountId);
        //        apiService.openSandboxAccount();
        //        log.info("finish");
        //        if(true) {
        //            return;
        //        }

        updateOperations(accountId, portfolio);

//        val interval = CandleInterval.CANDLE_INTERVAL_1_MIN;
        updateSharesFromApi(CANDLE_INTERVAL_1_MIN);
        calculateMetrics(CANDLE_INTERVAL_1_MIN);


        val sharesToSell = findActiveSharesToSellSandbox(portfolio);
        sharesToSell.forEach(activeShare -> apiService.sellShareFromApi(accountId, activeShare.getShare().getFigi()));

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

 //   @Scheduled(cron = "5 0/4  * * * *") // every 5 minutes
    @Scheduled(cron = "5 0/3  * * * *") // every 5 minutes
    public void executeEvery5Minutes() {
        log.info("start 5 minute");
        val interval = CandleInterval.CANDLE_INTERVAL_5_MIN;
        updateSharesFromApi(interval);
        calculateMetrics(interval);
        log.info("finish 5 minute");
    }

//    @Scheduled(cron = "5 0/10  * * * *") // every 15 minutes
    @Scheduled(cron = "5 0/7  * * * *") // every 15 minutes
    public void executeEvery15Minutes() {
        log.info("start 15 minute");
        val interval = CandleInterval.CANDLE_INTERVAL_15_MIN;
        updateSharesFromApi(interval);
        calculateMetrics(interval);
        log.info("finish 15 minute");
    }

    @Scheduled(cron = "5 0/50  * * * *")
    public void executeEvery1Hour() {
        log.info("start 1 hour");
        val interval = CandleInterval.CANDLE_INTERVAL_HOUR;
        updateSharesFromApi(interval);
        calculateMetrics(interval);
        log.info("finish 1 hour");
    }

    @Scheduled(cron = "5 0 0/8 * * *") // every  day 10 o clock
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
            val figi = candle.getShare().getFigi();
            log.info("want to buy: " + candle.getShare().getId() + " / " + shareToBuy.getPriceToBuy());
            val order = apiService.buyShareFromApi(accountId, figi, shareToBuy.getPriceToBuy());
            val comission = TUtils.moneyValueToDouble(order.getExecutedCommission());
            val comissionCurrency = order.getExecutedCommission().getCurrency();
            //val price = TUtils.moneyValueToDouble(order.getExecutedOrderPrice());
            val price = shareToBuy.getPriceToBuy();
            val takeProfit = price + TUtils.TAKE_PROFIT_PERCENT * price;
            val stopLoss = price - TUtils.STOP_LOSS_PERCENT * price;
            val sma = String.format("%.2f", candle.getSimpleMovingAverage());
            val bollingerUp = String.format("%.2f", candle.getBollingerUp());
            val bollingerDown = String.format("%.2f", candle.getBollingerDown());
            candle.getShare().setLastShareStopLoss(stopLoss);
            candle.getShare().setLastShareTakeProfit(takeProfit);
            candle.getShare().setLastSharePrice(price);
            candle.getShare().setLastSharePosition("LONG");
            candle.getShare().setLastShareComission(comission);
            candle.getShare().setLastShareComissionCurrency(comissionCurrency);
            candle.getShare().setLastShareSMA(sma);
            candle.getShare().setLastShareBollingerUp(bollingerUp);
            candle.getShare().setLastShareBollingerDown(bollingerDown);
            candle.getShare().setLastShareInterval(candle.getInterval().toString());
            //val stopLoss = apiService.stopLossOrder(accountId, figi, orderPrice);
            //val takeProfit = apiService.takeProfitOrder(accountId, figi, orderPrice);
            //val message = generateBuyShareMessage(order, stopLoss, takeProfit, candle, "LONG", Instant.now());
            //val message = generateBuyShareMessageSandbox(order, stopLoss, takeProfit, candle, "LONG", Instant.now());
            //log.info(message);
            //botService.sendMassage(message);
        }
        TUtils.saveLastShares(portfolio);
    }

    public String getPortfolio() {
        val accountId = apiService.getAccountFromApi();
        apiService.updatePortfolioFromApi(accountId, portfolio);
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
            apiService.updatePortfolioFromApi(accountId, portfolio);
            log.info("Got new operations: " + newOperations.size());
            for (TOperation newOperation : newOperations) {
                log.info(newOperation.getInstant() + " / " + newOperation.getShareId() + " / " + newOperation.getType() + " / " + newOperation.getPrice() + " / " + newOperation.getCurrency());
            }
            TUtils.saveLastShares(portfolio);
        }

        while(operations.size() > 100) {
            operations.remove(0);
        }
    }

    private List<TActiveShare> findActiveSharesToSellSandbox(@NonNull TPortfolio portfolio) {
        List<TActiveShare> result = new ArrayList<>();
        for (TShare share : portfolio.getShares()) {
            for (TActiveShare activeShare : share.getActiveShares()) {
                if (activeShare.getPrice() > share.getLastShareTakeProfit() || activeShare.getPrice() < share.getLastShareStopLoss()) {
                    result.add(activeShare);
                }
            }
        }
        return result;
    }

    private String generateBuyShareMessageSandbox(@NonNull PostOrderResponse order,
                                           @NonNull Double stopLoss,
                                           @NonNull Double takeProfit,
                                           @NonNull TCandle candle,
                                           @NonNull String position,
                                           @NonNull Instant instant) {

        val price = TUtils.moneyValueToDouble(order.getExecutedOrderPrice());
        val priceCurrency = order.getExecutedOrderPrice().getCurrency();
        val comission = TUtils.moneyValueToDouble(order.getExecutedCommission());
        val comissionCurrency = order.getExecutedCommission().getCurrency();
        val takeProfitPrice = String.format("%.2f", takeProfit);
        val stopLossPrice = String.format("%.2f", stopLoss);
        val sma = String.format("%.2f", candle.getSimpleMovingAverage());
        val bollingerUp = String.format("%.2f", candle.getBollingerUp());
        val bollingerDown = String.format("%.2f", candle.getBollingerDown());

        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy:hh:mm").withZone(ZoneId.systemDefault());

        return "Buy:" + "\n"
                + "id: " + candle.getShare().getId() + "\n"
                + "position: " + position + "\n"
                + "date: " + formatter.format(instant) + "\n"
                + "price: " + price + " " + priceCurrency + "\n"
                + "comission: " + comission + " " + comissionCurrency + "\n"
                + "takeProfit: " + takeProfitPrice + "\n"
                + "stopLoss: " + stopLossPrice + "\n"
                + "BB: " + sma + " " + bollingerUp + " " + bollingerDown + "\n";
    }

    private String generateBuyShareMessage(@NonNull PostOrderResponse order,
                                           @Nullable StopOrder stopLoss,
                                           @Nullable StopOrder takeProfit,
                                           @NonNull TCandle candle,
                                           @NonNull String position,
                                           @NonNull Instant instant) {

        val price = TUtils.moneyValueToDouble(order.getExecutedOrderPrice());
        val priceCurrency = order.getExecutedOrderPrice().getCurrency();
        val comission = TUtils.moneyValueToDouble(order.getExecutedCommission());
        val comissionCurrency = order.getExecutedCommission().getCurrency();
//        val takeProfitPrice = TUtils.moneyValueToDouble(takeProfit.getPrice());
//        val takeProfitCurrency = takeProfit.getCurrency();
//        val takeProfitExpiration = timestampToString(takeProfit.getExpirationTime());
//        val stopLossPrice = TUtils.moneyValueToDouble(stopLoss.getPrice());
//        val stopLossCurrency = stopLoss.getCurrency();
//        val stopLossExpiration = timestampToString(stopLoss.getExpirationTime());
        val sma = String.format("%.2f", candle.getSimpleMovingAverage());
        val bollingerUp = String.format("%.2f", candle.getBollingerUp());
        val bollingerDown = String.format("%.2f", candle.getBollingerDown());

        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy:hh:mm").withZone(ZoneId.systemDefault());

        return "Buy:" + "\n"
                + "id: " + candle.getShare().getId() + "\n"
                + "position: " + position + "\n"
                + "date: " + formatter.format(instant) + "\n"
                + "price: " + price + " " + priceCurrency + "\n"
                + "comission: " + comission + " " + comissionCurrency + "\n"
//                + "takeProfit: " + takeProfitPrice + " " + takeProfitCurrency + " " + takeProfitExpiration + "\n"
//                + "stopLoss: " + stopLossPrice + " " + stopLossCurrency + " " + stopLossExpiration + "\n"
                + "BB: " + sma + " " + bollingerUp + " " + bollingerDown + "\n";
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
                .append("USD: " + String.format("%.2f", portfolio.getDollarBalance() + usdSharesSum) + "\n")
                .append("RUB: " + String.format("%.2f", portfolio.getRubBalance() + rubSharesSum) + "\n")
                .append("buyOperations: " + portfolio.getBuyOperationsCount() + "\n")
                .append("sellOperations: " + portfolio.getSellOperationsCount() + "\n")
                .append("candlesCount: " + totalCandlesCount + "\n");

        result.append("SHARES: \n");
        for (TActiveShare activeShare : activeShares) {
            val count = activeShare.getCount();
            val price = activeShare.getPrice();
            val total = count * price;
            val currency = activeShare.getCurrency().toString();
            result.append(activeShare.getShare().getId() + ": " + count + " * " + price + " = " + total + " " + currency + "\n" );
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
                log.info(share.getId() + " already active");
                continue;
            }
            val minuteCandles = share.getCandlesMap().get(CandleInterval.CANDLE_INTERVAL_1_MIN);
            if (minuteCandles.isEmpty()) {
                log.info(share.getId() + " minute candles empty");
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
            log.info(lastCandle.getShare().getId() + " / " + lastCandle.getInstant() + " / " + lastCandle.getInterval() + " / " + currentPrice + " < " + lastCandle.getBollingerDown() + " check: " + currentPrice + currentPrice * TUtils.TAKE_PROFIT_PERCENT + " / " + lastCandle.getBollingerUp());
            if ((currentPrice + currentPrice * TUtils.TAKE_PROFIT_PERCENT) < lastCandle.getBollingerUp()) {
                log.info("want to buy: " + lastCandle.getShare().getId());
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
