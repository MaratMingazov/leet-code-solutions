package maratmingazovr.leetcode.tinkof;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.tinkoff.piapi.contract.v1.Account;
import ru.tinkoff.piapi.contract.v1.CandleInterval;
import ru.tinkoff.piapi.contract.v1.HistoricCandle;
import ru.tinkoff.piapi.contract.v1.LastPrice;
import ru.tinkoff.piapi.contract.v1.MoneyValue;
import ru.tinkoff.piapi.contract.v1.Operation;
import ru.tinkoff.piapi.contract.v1.OrderDirection;
import ru.tinkoff.piapi.contract.v1.OrderState;
import ru.tinkoff.piapi.contract.v1.OrderType;
import ru.tinkoff.piapi.contract.v1.PostOrderResponse;
import ru.tinkoff.piapi.contract.v1.Quotation;
import ru.tinkoff.piapi.contract.v1.StopOrderDirection;
import ru.tinkoff.piapi.contract.v1.StopOrderType;
import ru.tinkoff.piapi.core.InvestApi;
import ru.tinkoff.piapi.core.models.Portfolio;

import java.time.Instant;
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

    public void buyOrderLong(@NonNull String accountId,
                                          @NonNull String shareFigi,
                                          @NonNull Double price) {
        api.getOrdersService().postOrderSync(shareFigi,
                                             1,
                                             TUtils.DoubleToQuotation(price),
                                             OrderDirection.ORDER_DIRECTION_BUY,
                                             accountId,
                                             OrderType.ORDER_TYPE_LIMIT,
                                             UUID.randomUUID().toString());
    }

    public void buyOrderShort(@NonNull String accountId,
                                           @NonNull String shareFigi,
                                           @NonNull Double price) {
        api.getOrdersService().postOrderSync(shareFigi,
                                             1,
                                             TUtils.DoubleToQuotation(price),
                                             OrderDirection.ORDER_DIRECTION_SELL,
                                             accountId,
                                             OrderType.ORDER_TYPE_LIMIT,
                                             UUID.randomUUID().toString());
    }

    public void takeProfitOrderShort(@NonNull String accountId,
                                     @NonNull String shareFigi,
                                     @NonNull Double currentPrice,
                                     @NonNull Double takeProfitPrice) {
        api.getStopOrdersService()
           .postStopOrderGoodTillCancelSync(shareFigi,
                                          1,
                                          TUtils.DoubleToQuotation(takeProfitPrice),
                                          TUtils.DoubleToQuotation(currentPrice),
                                          StopOrderDirection.STOP_ORDER_DIRECTION_BUY,
                                          accountId,
                                          StopOrderType.STOP_ORDER_TYPE_TAKE_PROFIT);
    }

    public void takeProfitOrderLong(@NonNull String accountId,
                                     @NonNull String shareFigi,
                                     @NonNull Double currentPrice,
                                     @NonNull Double takeProfitPrice) {
        api.getStopOrdersService()
           .postStopOrderGoodTillCancelSync(shareFigi,
                                            1,
                                            TUtils.DoubleToQuotation(takeProfitPrice),
                                            TUtils.DoubleToQuotation(currentPrice),
                                            StopOrderDirection.STOP_ORDER_DIRECTION_SELL,
                                            accountId,
                                            StopOrderType.STOP_ORDER_TYPE_TAKE_PROFIT);
    }

    public void stopLossOrderShort(@NonNull String accountId,
                                   @NonNull String shareFigi,
                                   @NonNull Double currentPrice,
                                   @NonNull Double takeProfitPrice) {
        api.getStopOrdersService()
           .postStopOrderGoodTillCancelSync(shareFigi,
                                            1,
                                            TUtils.DoubleToQuotation(takeProfitPrice),
                                            TUtils.DoubleToQuotation(currentPrice),
                                            StopOrderDirection.STOP_ORDER_DIRECTION_SELL,
                                            accountId,
                                            StopOrderType.STOP_ORDER_TYPE_STOP_LOSS);
    }

    public void stopLossOrderLong(@NonNull String accountId,
                                  @NonNull String shareFigi,
                                  @NonNull Double currentPrice,
                                  @NonNull Double takeProfitPrice) {
        api.getStopOrdersService()
           .postStopOrderGoodTillCancelSync(shareFigi,
                                            1,
                                            TUtils.DoubleToQuotation(takeProfitPrice),
                                            TUtils.DoubleToQuotation(currentPrice),
                                            StopOrderDirection.STOP_ORDER_DIRECTION_BUY,
                                            accountId,
                                            StopOrderType.STOP_ORDER_TYPE_STOP_LOSS);
    }

    public synchronized Portfolio updatePortfolioFromApi(@NonNull String accountId) {
        return api.getOperationsService().getPortfolioSync(accountId);
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
    public List<Operation> getOperationsFromApi(@NonNull String accountId,
                                                @NonNull Instant from) {
        return api.getOperationsService().getAllOperationsSync(accountId, from, Instant.now());
    }

    @NonNull
    public synchronized List<OrderState> getActiveOrdersFromApi(@NonNull String accountId) {
        return api.getOrdersService().getOrdersSync(accountId);
    }

    @NonNull
    public List<LastPrice> getLastPrices(@NonNull List<String> figis) {
        return api.getMarketDataService().getLastPricesSync(figis);
    }

    @NonNull
    public PostOrderResponse sellShareFromApiSanddox(@NonNull String accountId,
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

    @NonNull
    public PostOrderResponse buyShareFromApiSanddox(@NonNull String accountId,
                                                     @NonNull String shareFigi) {
        Quotation lastPrice = api.getMarketDataService().getLastPricesSync(List.of(shareFigi)).get(0).getPrice();

        //Выставляем заявку на покупку по рыночной цене
        return api.getOrdersService().postOrderSync(shareFigi,
                                                    1,
                                                    lastPrice,
                                                    OrderDirection.ORDER_DIRECTION_BUY,
                                                    accountId,
                                                    OrderType.ORDER_TYPE_MARKET,
                                                    UUID.randomUUID().toString());
    }
}
