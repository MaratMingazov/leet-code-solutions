package maratmingazovr.leetcode.tinkof;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.tinkoff.piapi.contract.v1.CandleInterval;
import ru.tinkoff.piapi.contract.v1.PostOrderResponse;
import ru.tinkoff.piapi.contract.v1.StopOrder;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.tinkoff.piapi.contract.v1.CandleInterval.CANDLE_INTERVAL_15_MIN;
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
    }

    @Scheduled(cron = "0 0/1  * * * *") // every minute
    public void executeEveryMinute() {



        val accountId = apiService.getAccountFromApi();
//        apiService.closeSandboxAccount(accountId);
//        apiService.openSandboxAccount();

        updateOperations(accountId, portfolio);



        val interval = CandleInterval.CANDLE_INTERVAL_1_MIN;
        updateSharesFromApi(interval);
        calculateMetrics(interval);
        val sharesToSell = findActiveSharesToSellSandbox(portfolio);
        sharesToSell.forEach(activeShare -> apiService.sellShareFromApi(accountId, activeShare.getShare().getFigi()));

        var candlesToBuyLong = findCandlesToBuyLong(portfolio, CandleInterval.CANDLE_INTERVAL_DAY);
        if (candlesToBuyLong.isEmpty()) {
            candlesToBuyLong = findCandlesToBuyLong(portfolio, CandleInterval.CANDLE_INTERVAL_HOUR);
        }
        if (candlesToBuyLong.isEmpty()) {
            candlesToBuyLong = findCandlesToBuyLong(portfolio, CandleInterval.CANDLE_INTERVAL_15_MIN);
        }
        if (candlesToBuyLong.size() > 0) {
            log.info("candles to buy = " + candlesToBuyLong.size());
            buySharesLong(accountId, candlesToBuyLong);
        }

    }

//    @Scheduled(cron = "0 0/5  * * * *") // every 5 minutes
//    public void executeEvery5Minutes() {
//        log.info("start 5 minute");
//        val interval = CandleInterval.CANDLE_INTERVAL_5_MIN;
//        updateSharesFromApi(interval);
//        calculateMetrics(interval);
//        log.info("finish 5 minute");
//    }

    @Scheduled(cron = "0 0/15  * * * *") // every 15 minutes
    public void executeEvery15Minutes() {
        log.info("start 15 minute");
        val interval = CandleInterval.CANDLE_INTERVAL_15_MIN;
        updateSharesFromApi(interval);
        calculateMetrics(interval);
        log.info("finish 15 minute");
    }

    @Scheduled(cron = "0 0 0/1 * * *") // every 1 hour
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

    private synchronized void buySharesLong(@NonNull String accountId,
                                            @NonNull List<TCandle> candles) {
        for (TCandle candle : candles) {
            if(!candle.getShare().getActiveShares().isEmpty()) {
                continue;
            }
            val figi = candle.getShare().getFigi();
            val order = apiService.buyShareFromApi(accountId, figi);
            val comission = TUtils.moneyValueToDouble(order.getExecutedCommission());
            val comissionCurrency = order.getExecutedCommission().getCurrency();
            val price = TUtils.moneyValueToDouble(order.getExecutedOrderPrice());
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
            result.append(activeShare.getShare().getId())
                  .append(" count: ").append(activeShare.getCount())
                  .append(" price: ").append(String.format("%.2f", activeShare.getPrice()))
                  .append(" ").append(activeShare.getCurrency()).append("\n");
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
                defaultFrom = Instant.now().minus(5, ChronoUnit.DAYS);
                break;
            case CANDLE_INTERVAL_DAY:
                defaultFrom = Instant.now().minus(50, ChronoUnit.DAYS);
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
        }
        val newCandles = apiService.getCandlesFromApi(share, from, interval);
        val candles = share.getCandlesMap().get(interval);
        candles.addAll(newCandles);
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
    private List<TCandle> findCandlesToBuyLong(@NonNull TPortfolio portfolio,
                                               @NonNull CandleInterval interval) {
        List<TCandle> candlesToBuy = new ArrayList<>();
        if (portfolio.getDollarBalance() < 2000 || portfolio.getRubBalance() < 2000) {
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
            val currentPrice = minuteCandles.get(minuteCandles.size() - 1).getLow();
            val candleToBuyOpt = findCandleToBuyLong(currentPrice, share.getCandlesMap().get(interval));
            candleToBuyOpt.ifPresent(candlesToBuy::add);
        }
        return candlesToBuy;
    }

    @NonNull
    private Optional<TCandle> findCandleToBuyLong(@NonNull Double currentPrice, @NonNull List<TCandle> candles) {
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
        if (currentPrice < lastCandle.getBollingerDown()
                && (currentPrice + currentPrice * TUtils.TAKE_PROFIT_PERCENT) < lastCandle.getBollingerUp() ) {
            return Optional.of(lastCandle);
        }
        return Optional.empty();
    }
}
