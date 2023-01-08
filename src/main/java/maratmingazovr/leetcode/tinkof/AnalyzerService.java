package maratmingazovr.leetcode.tinkof;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import maratmingazovr.leetcode.tinkof.long_share.TActiveShare;
import maratmingazovr.leetcode.tinkof.long_share.TActiveShareInfo;
import maratmingazovr.leetcode.tinkof.long_share.TShareToBuy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.tinkoff.piapi.contract.v1.CandleInterval;
import ru.tinkoff.piapi.contract.v1.OrderState;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        TUtils.loadLastActiveLongShares(portfolio);
        execute();
        log.info(portfolio.toStringMessage());
        execute(CandleInterval.CANDLE_INTERVAL_DAY);
    }

    @Scheduled(cron = "0/10 * * * * *")
    public void executePrices() {
        execute();
    }

    private void execute() {
        val accountId = apiService.getAccountFromApi();
        val figis = portfolio.getShares().stream().map(TShare::getFigi).collect(Collectors.toList());
        val from = portfolio.getLastOperationDate();

        val lastPrices = apiService.getLastPrices(figis);
        val newOperationsFromApi = apiService.getOperationsFromApi(accountId, from);
        val portfolioUpdate =  apiService.updatePortfolioFromApi(accountId);
        val activeOrders = apiService.getActiveOrdersFromApi(accountId);

        portfolio.updatePortfolio(portfolioUpdate);
        portfolio.updateLastPrices(lastPrices);
        portfolio.updateOperations(newOperationsFromApi, botService);


        val sharesToSellLong = findActiveSharesToSellSandboxLong(portfolio);
        sharesToSellLong.forEach(activeShare -> apiService.sellShareFromApiSanddox(accountId, activeShare.getShareFigi()));
        val sharesToSellShort = findActiveSharesToSellSandboxShort(portfolio);
        sharesToSellShort.forEach(activeShare -> apiService.buyShareFromApiSanddox(accountId, activeShare.getShareFigi()));

        checkSharesToBuyLong(accountId, portfolio, activeOrders);
        checkSharesToBuyShort(accountId, portfolio, activeOrders);
    }

//    //@Scheduled(cron = "3 0/1  * * * *") // every minute
//    public void executeEveryMinute() {
//        val accountId = apiService.getAccountFromApi();
//        //        apiService.closeSandboxAccount(accountId);
//        //        apiService.openSandboxAccount();
//        //        log.info("finish");
//        //        if(true) {
//        //            return;
//        //        }
//    }

    @Scheduled(cron = "5 0/5  * * * *") // every 5 minutes
    public void executeEvery5Minutes() {
        execute(CandleInterval.CANDLE_INTERVAL_5_MIN);
    }

    @Scheduled(cron = "5 0/15  * * * *") // every 15 minutes
    public void executeEvery15Minutes() {
        execute(CandleInterval.CANDLE_INTERVAL_15_MIN);
    }

    @Scheduled(cron = "5 0 0/1 * * *")
    public void executeEvery1Hour() {
        execute(CandleInterval.CANDLE_INTERVAL_HOUR);
    }

    @Scheduled(cron = "5 0 10 * * *") // every  day 10 o clock
    public void executeEvery1Day() {
        execute(CandleInterval.CANDLE_INTERVAL_DAY);
    }

    private synchronized void execute(@NonNull CandleInterval interval) {
        log.info("start: " + interval);
        updateSharesFromApi(interval);
        portfolio.calculateMetrics(interval);
        log.info("finish: " + interval);
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
            val activeShare = candle.getShare().getActiveShare();
            if(activeShare.getCount() != 0.0) {
                continue;
            }
            val share = candle.getShare();
            val figi = share.getFigi();
            log.info("want to buy long: " + candle.getShare().getId() + " / " + shareToBuy.getPriceToBuy());
            apiService.buyOrderLong(accountId, figi, shareToBuy.getPriceToBuy());
            val activeShareInfo = new TActiveShareInfo(shareToBuy.getPriceToBuy(),
                                                       0.0,
                                                       candle.getSimpleMovingAverage(),
                                                       candle.getBollingerUp(),
                                                       candle.getBollingerDown(),
                                                       candle.getRsi(),
                                                       candle.getPreviousExtremumRSI(),
                                                       candle.getInterval());
            share.setActiveShareInfo(activeShareInfo);
            //val stopLoss = apiService.stopLossOrder(accountId, figi, orderPrice);
            //val takeProfit = apiService.takeProfitOrder(accountId, figi, orderPrice);
        }
        TUtils.saveLastActiveLongShares(portfolio);
    }

    private synchronized void buySharesShort(@NonNull String accountId,
                                             @NonNull List<TShareToBuy> sharesToBuy) {
        for (TShareToBuy shareToBuy : sharesToBuy) {
            val candle = shareToBuy.getCandle();
            val activeShare = candle.getShare().getActiveShare();
            if(activeShare.getCount() != 0.0) {
                continue;
            }
            val share = candle.getShare();
            val figi = share.getFigi();
            log.info("want to buy short: " + candle.getShare().getId() + " / " + shareToBuy.getPriceToBuy());
            apiService.buyOrderShort(accountId, figi, shareToBuy.getPriceToBuy());
            val activeShareInfo = new TActiveShareInfo(0.0,
                                                       shareToBuy.getPriceToBuy(),
                                                       candle.getSimpleMovingAverage(),
                                                       candle.getBollingerUp(),
                                                       candle.getBollingerDown(),
                                                       candle.getRsi(),
                                                       candle.getPreviousExtremumRSI(),
                                                       candle.getInterval());
            share.setActiveShareInfo(activeShareInfo);
            //val stopLoss = apiService.stopLossOrder(accountId, figi, orderPrice);
            //val takeProfit = apiService.takeProfitOrder(accountId, figi, orderPrice);
        }
        TUtils.saveLastActiveLongShares(portfolio);
    }

    public String getPortfolio() {
        return portfolio.toStringMessage();
    }

    private List<TActiveShare> findActiveSharesToSellSandboxLong(@NonNull TPortfolio portfolio) {
        List<TActiveShare> result = new ArrayList<>();
        for (TShare share : portfolio.getShares()) {
            val activeShare = share.getActiveShare();
            if (activeShare.getCount() <= 0) {
                continue;
            }
            val info = share.getActiveShareInfo();
            val takeProfit = info.getBuyTakeProfit();
            val stopLoss = info.getBuyStopLoss();
            if (activeShare.getPrice() >= takeProfit || activeShare.getPrice() <= stopLoss) {
                result.add(activeShare);
            }
        }
        return result;
    }

    private List<TActiveShare> findActiveSharesToSellSandboxShort(@NonNull TPortfolio portfolio) {
        List<TActiveShare> result = new ArrayList<>();
        for (TShare share : portfolio.getShares()) {
            val activeShare = share.getActiveShare();
            if (activeShare.getCount() >= 0) {
                continue;
            }
            val info = share.getActiveShareInfo();
            val takeProfit = info.getSellTakeProfit();
            val stopLoss = info.getSellStopLoss();
            if (activeShare.getPrice() <= takeProfit || activeShare.getPrice() >= stopLoss) {
                result.add(activeShare);
            }
        }
        return result;
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


    private void checkSharesToBuyLong(@NonNull String accountId,
                                      @NonNull TPortfolio portfolio,
                                      @NonNull List<OrderState> activeOrders) {
        var candlesToBuyLong = findCandlesToBuyLong(portfolio, CandleInterval.CANDLE_INTERVAL_DAY, activeOrders);
        if (candlesToBuyLong.isEmpty()) {
            candlesToBuyLong = findCandlesToBuyLong(portfolio, CandleInterval.CANDLE_INTERVAL_HOUR, activeOrders);
        }
        if (candlesToBuyLong.isEmpty()) {
            candlesToBuyLong = findCandlesToBuyLong(portfolio, CandleInterval.CANDLE_INTERVAL_15_MIN, activeOrders);
        }
        if (candlesToBuyLong.isEmpty()) {
            candlesToBuyLong = findCandlesToBuyLong(portfolio, CANDLE_INTERVAL_5_MIN, activeOrders);
        }
        if (candlesToBuyLong.size() > 0) {
            buySharesLong(accountId, candlesToBuyLong);
        }
    }

    private void checkSharesToBuyShort(@NonNull String accountId,
                                      @NonNull TPortfolio portfolio,
                                      @NonNull List<OrderState> activeOrders) {
        var candlesToBuyShort = findCandlesToBuyShort(portfolio, CandleInterval.CANDLE_INTERVAL_DAY, activeOrders);
        if (candlesToBuyShort.isEmpty()) {
            candlesToBuyShort = findCandlesToBuyShort(portfolio, CandleInterval.CANDLE_INTERVAL_HOUR, activeOrders);
        }
        if (candlesToBuyShort.isEmpty()) {
            candlesToBuyShort = findCandlesToBuyShort(portfolio, CandleInterval.CANDLE_INTERVAL_15_MIN, activeOrders);
        }
        if (candlesToBuyShort.isEmpty()) {
            candlesToBuyShort = findCandlesToBuyShort(portfolio, CANDLE_INTERVAL_5_MIN, activeOrders);
        }
        if (candlesToBuyShort.size() > 0) {
            buySharesShort(accountId, candlesToBuyShort);
        }
    }

    @NonNull
    private List<TShareToBuy> findCandlesToBuyLong(@NonNull TPortfolio portfolio,
                                                   @NonNull CandleInterval interval,
                                                   @NonNull List<OrderState> activeOrders) {
        List<TShareToBuy> candlesToBuy = new ArrayList<>();
        if (portfolio.getDollarBalance() < 2000 || portfolio.getRubBalance() < 2000) {
            log.info("balance is low, will not buy");
            return candlesToBuy;
        }
        for (TShare share : portfolio.getShares()) {
            val activeShare = share.getActiveShare();
            if (activeShare.getCount() != 0.0) {
                continue;
            }
            val activeOrder = activeOrders.stream().filter(order -> order.getFigi().equals(share.getFigi())).findAny();
            if (activeOrder.isPresent()) {
                //log.info("I can not but share long, because have active order. " + share.getId());
                continue;
            }

            val candles = share.getCandlesMap().get(interval);
            if (candles.isEmpty()) {
                continue;
            }
            val currentPrice = candles.get(candles.size() - 1).getClose();
            val candleToBuyOpt = findCandleToBuyLong(currentPrice, share.getCandlesMap().get(interval));
            candleToBuyOpt.ifPresent(candlesToBuy::add);
        }
        return candlesToBuy;
    }

    @NonNull
    private List<TShareToBuy> findCandlesToBuyShort(@NonNull TPortfolio portfolio,
                                                   @NonNull CandleInterval interval,
                                                   @NonNull List<OrderState> activeOrders) {
        List<TShareToBuy> candlesToBuy = new ArrayList<>();
        if (portfolio.getDollarBalance() < 2000 || portfolio.getRubBalance() < 2000) {
            log.info("balance is low, will not buy");
            return candlesToBuy;
        }
        for (TShare share : portfolio.getShares()) {
            val activeShare = share.getActiveShare();
            if (activeShare.getCount() != 0.0) {
                continue;
            }
            val activeOrder = activeOrders.stream().filter(order -> order.getFigi().equals(share.getFigi())).findAny();
            if (activeOrder.isPresent()) {
                //log.info("I can not buy share short, because have active order. " + share.getId());
                continue;
            }

            val candles = share.getCandlesMap().get(interval);
            if (candles.isEmpty()) {
                continue;
            }
            val currentPrice = candles.get(candles.size() - 1).getOpen();
            val candleToBuyOpt = findCandleToBuyShort(currentPrice, share.getCandlesMap().get(interval));
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
            log.info("long candidate: " + lastCandle.getShare().getId() + " / " + lastCandle.getInstant() + " / " + lastCandle.getInterval() + " / " + currentPrice + " < " + lastCandle.getBollingerDown() + " check: " + (currentPrice + currentPrice * TUtils.TAKE_PROFIT_PERCENT) + " / " + lastCandle.getBollingerUp());
            if ((currentPrice + currentPrice * TUtils.TAKE_PROFIT_PERCENT) < lastCandle.getBollingerUp()) {
                // значит цена тейк профита не выходит за вернюю границу
                return Optional.of(new TShareToBuy(lastCandle, currentPrice));
            }
        }
        return Optional.empty();
    }

    @NonNull
    private Optional<TShareToBuy> findCandleToBuyShort(@NonNull Double currentPrice,
                                                       @NonNull List<TCandle> candles) {
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
        if (currentPrice > lastCandle.getBollingerUp()) {
            log.info("short candidate: " + lastCandle.getShare().getId() + " / " + lastCandle.getInstant() + " / " + lastCandle.getInterval() + " / " + currentPrice + " < " + lastCandle.getBollingerUp() + " check: " + (currentPrice - currentPrice * TUtils.TAKE_PROFIT_PERCENT) + " / " + lastCandle.getBollingerDown());
            if ((currentPrice - currentPrice * TUtils.TAKE_PROFIT_PERCENT) > lastCandle.getBollingerDown()) {
                // значит цена тейк профита не выходит за нижнюю границу
                return Optional.of(new TShareToBuy(lastCandle, currentPrice));
            }
        }
        return Optional.empty();
    }

    public String getCandlesMessage(@NonNull String shareId) {
        return portfolio.toStringCandles(shareId);
    }
}
