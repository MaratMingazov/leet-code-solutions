package maratmingazovr.leetcode.tinkof;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.tinkoff.piapi.contract.v1.Account;
import ru.tinkoff.piapi.contract.v1.CandleInterval;
import ru.tinkoff.piapi.contract.v1.HistoricCandle;
import ru.tinkoff.piapi.contract.v1.MoneyValue;
import ru.tinkoff.piapi.contract.v1.OrderDirection;
import ru.tinkoff.piapi.contract.v1.OrderType;
import ru.tinkoff.piapi.contract.v1.PostOrderResponse;
import ru.tinkoff.piapi.contract.v1.Quotation;
import ru.tinkoff.piapi.contract.v1.StopOrder;
import ru.tinkoff.piapi.contract.v1.StopOrderDirection;
import ru.tinkoff.piapi.contract.v1.StopOrderType;
import ru.tinkoff.piapi.core.InvestApi;
import ru.tinkoff.piapi.core.models.Position;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Log4j2
@Service
public class ApiService {

    private final InvestApi api;

    private String accountId;

    public ApiService(@Autowired InvestApi api) {
        this.api = api;
    }

    public synchronized  void closeSandboxAccount(@NonNull String accountId) {
        api.getSandboxService().closeAccountSync(accountId);
    }

    public synchronized void openSandboxAccount() {
        String accountId = api.getSandboxService().openAccountSync();
        log.info("открыт новый аккаунт в песочнице {}", accountId);
        var accounts = api.getUserService().getAccountsSync();
        Account mainAccount = accounts.get(0);
        for (Account account : accounts) {
            log.info("sandbox account id: {}, access level: {}", account.getId(), account.getAccessLevel().name());
        }
        //Убеждаемся, что мы в режиме песочницы
        log.info("тариф должен быть sandbox. фактический тариф: {}", api.getUserService().getInfoSync().getTariff());

        //пополняем счет песочницы на 10_000 рублей и 10_000 долларов
        api.getSandboxService().payInSync(mainAccount.getId(), MoneyValue.newBuilder().setUnits(10000).setCurrency("RUB").build());
        api.getSandboxService().payInSync(mainAccount.getId(), MoneyValue.newBuilder().setUnits(10000).setCurrency("USD").build());
    }

    @NonNull
    public List<TCandle> getCandlesFromApi(@NonNull TShare share,
                                               @NonNull Instant from,
                                               @NonNull CandleInterval interval) {
        List<HistoricCandle> candles = api.getMarketDataService().getCandlesSync(share.getFigi(), from, Instant.now(), interval);
        return candles.stream().map(hCandle -> new TCandle(hCandle, share, interval)).collect(Collectors.toList());
    }

    @NonNull
    public PostOrderResponse sendByLimitLongOrder(@NonNull String accountId,
                                             @NonNull String shareFigi,
                                             @NonNull Double price) {

        val value = BigDecimal.valueOf(price);
        Quotation quotationPrice = Quotation.newBuilder()
                                       .setUnits(value.longValue() )
                                       .setNano(value.remainder(BigDecimal.ONE).multiply(BigDecimal.valueOf(1_000_000_000)).intValue())
                                       .build();

        return api.getOrdersService().postOrderSync(shareFigi,
                                                    1,
                                                    quotationPrice,
                                                    OrderDirection.ORDER_DIRECTION_BUY,
                                                    accountId,
                                                    OrderType.ORDER_TYPE_LIMIT,
                                                    UUID.randomUUID().toString());
    }

    @NonNull
    public PostOrderResponse sellShareFromApi(@NonNull String accountId,
                                              @NonNull String shareFigi) {
        Quotation lastPrice = api.getMarketDataService().getLastPricesSync(List.of(shareFigi)).get(0).getPrice();

        //Выставляем заявку на продажу по рыночной цене
        return api.getOrdersService().postOrderSync(shareFigi,
                                                    1,
                                                    lastPrice,
                                                    OrderDirection.ORDER_DIRECTION_SELL,
                                                    accountId,
                                                    OrderType.ORDER_TYPE_MARKET,
                                                    UUID.randomUUID().toString());
    }

    public synchronized void updatePortfolioFromApi(@NonNull String accountId,
                                      @NonNull TPortfolio portfolio) {
        val portfolioSync = api.getOperationsService().getPortfolioSync(accountId);
        val positions = portfolioSync.getPositions();
        for (TShare share : portfolio.getShares()) {
            share.getActiveShares().clear();
        }
        for (Position position : positions) {
            val figi = position.getFigi();
            double count = position.getQuantity().doubleValue();
            if (count <= 0) {
                continue;
            }
            val price = position.getCurrentPrice().getValue().doubleValue();
            val currency = position.getCurrentPrice().getCurrency();
            if (figi.equals(portfolio.getDollarBalanceFigi())) {
                portfolio.setDollarBalance(count);
            }
            if (figi.equals(portfolio.getRubBalanceFigi())) {
                portfolio.setRubBalance(count);
            }
            for (TShare share : portfolio.getShares()) {
                if (figi.equals(share.getFigi())) {
                    share.getActiveShares().add(new TActiveShare(currency, price, count, share));
                }
            }
        }
    }

    @NonNull
    public synchronized String getAccountFromApi() {
        if (accountId == null) {
            var accounts = api.getUserService().getAccountsSync();
            accountId =  accounts.get(0).getId();
        }
        return accountId;
    }

    @NonNull
    public  StopOrder stopLossOrder(@NonNull String accountId,
                                     @NonNull String figi,
                                     @NonNull MoneyValue sharePrice) {

        var stopPrice = Quotation.newBuilder()
                                 .setUnits(sharePrice.getUnits() - sharePrice.getUnits() * 100 / 5)
                                 .setNano(sharePrice.getNano() - sharePrice.getNano() * 100 / 5)
                                 .build();

        var stopOrderId = api.getStopOrdersService()
                             .postStopOrderGoodTillDateSync(figi,
                                                            1,
                                                            stopPrice,
                                                            stopPrice,
                                                            StopOrderDirection.STOP_ORDER_DIRECTION_SELL,
                                                            accountId,
                                                            StopOrderType.STOP_ORDER_TYPE_STOP_LOSS,
                                                            Instant.now().plus(30, ChronoUnit.DAYS));


        //Получаем список стоп-заявок и смотрим, что наша заявка в ней есть
        var stopOrders = api.getStopOrdersService().getStopOrdersSync(accountId);
        return stopOrders.stream().filter(el -> el.getStopOrderId().equals(stopOrderId)).findAny().orElseThrow();
    }

    @NonNull
    public  StopOrder takeProfitOrder(@NonNull String accountId,
                                       @NonNull String figi,
                                       @NonNull MoneyValue sharePrice) {

        var stopPrice = Quotation.newBuilder()
                                 .setUnits(sharePrice.getUnits() + sharePrice.getUnits() * 100 / 5)
                                 .setNano(sharePrice.getNano() + sharePrice.getNano() * 100 / 5)
                                 .build();

        var stopOrderId = api.getStopOrdersService()
                             .postStopOrderGoodTillDateSync(figi,
                                                            1,
                                                            stopPrice,
                                                            stopPrice,
                                                            StopOrderDirection.STOP_ORDER_DIRECTION_SELL,
                                                            accountId,
                                                            StopOrderType.STOP_ORDER_TYPE_TAKE_PROFIT,
                                                            Instant.now().plus(1, ChronoUnit.YEARS));

        //Получаем список стоп-заявок и смотрим, что наша заявка в ней есть
        var stopOrders = api.getStopOrdersService().getStopOrdersSync(accountId);
        return stopOrders.stream().filter(el -> el.getStopOrderId().equals(stopOrderId)).findAny().orElseThrow();
    }

    @NonNull
    public List<TOperation> getOperationsFromApi(@NonNull String accountId,
                                                 @NonNull Instant from,
                                                 @NonNull TPortfolio portfolio) {
        return api.getOperationsService()
                            .getAllOperationsSync(accountId, from, Instant.now())
                .stream().map(operation -> new TOperation(operation, portfolio))
                .collect(Collectors.toList());
    }
}
