package maratmingazovr.leetcode.tinkof;

import com.github.kshashov.telegram.api.MessageType;
import com.github.kshashov.telegram.api.TelegramMvcController;
import com.github.kshashov.telegram.api.bind.annotation.BotPathVariable;
import com.github.kshashov.telegram.api.bind.annotation.BotRequest;
import com.github.kshashov.telegram.api.bind.annotation.request.MessageRequest;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import ru.tinkoff.piapi.contract.v1.CandleInterval;

@com.github.kshashov.telegram.api.bind.annotation.BotController
@RequiredArgsConstructor
@Log4j2
public class BotController implements TelegramMvcController {

    private final TelegramBot bot;

    private final BotService botService;

    private final AnalyzerService analyzerService;



    @Override
    public String getToken() {
        return bot.getToken();
    }

    @BotRequest(value = "/start", type = {MessageType.CALLBACK_QUERY, MessageType.MESSAGE})
    public BaseRequest start(User user, Chat chat) {
        log.info("start from " + user.firstName() + " " + user.lastName());
        botService.addSubscriber(chat, user);
        return new SendMessage(chat.id(), "Hi " + user.firstName() + "! You are welcome.");
    }

    @BotRequest(value = "/stop", type = {MessageType.CALLBACK_QUERY, MessageType.MESSAGE})
    public BaseRequest stop(User user, Chat chat) {
        botService.removeSubscriber(chat);
        return new SendMessage(chat.id(), "You was removed from subscribers list");
    }

    @BotRequest(value = "/subscribers", type = {MessageType.CALLBACK_QUERY, MessageType.MESSAGE})
    public BaseRequest subscribers(User user, Chat chat) {
        val subscribers = botService.getSubscribers();
        return new SendMessage(chat.id(), subscribers);
    }

    @BotRequest(value = "/balance", type = {MessageType.CALLBACK_QUERY, MessageType.MESSAGE})
    public BaseRequest balance(User user, Chat chat) {
        log.info("balance from " + user.firstName() + " " + user.lastName());
        val portfolio = analyzerService.getPortfolio();
        return new SendMessage(chat.id(), portfolio);
    }



    @MessageRequest("/stat {data:[\\S]+}")
    public String stat(@BotPathVariable("data") String data) {
        try{
            String[] objects = data.split("-");
            String shareId = objects[0].toLowerCase();
            String intervalValue = objects[1];
            CandleInterval interval = getInterval(intervalValue);
            Integer index = Integer.parseInt(objects[2]);
            return analyzerService.getStatMessage(shareId, interval, index);
        } catch (Exception e) {
            log.error(e);
            return "exception";
        }
    }

    @MessageRequest("/candles {data:[\\S]+}")
    public String candles(@BotPathVariable("data") String data) {
        try{
            return analyzerService.getCandlesMessage(data.toLowerCase());
        } catch (Exception e) {
            log.error(e);
            return "exception";
        }
    }

    @NonNull
    private CandleInterval getInterval(@NonNull String value) {
        switch (value) {
            case "1": return CandleInterval.CANDLE_INTERVAL_1_MIN;
            case "15": return CandleInterval.CANDLE_INTERVAL_15_MIN;
            case "60": return CandleInterval.CANDLE_INTERVAL_HOUR;
            case "24": return CandleInterval.CANDLE_INTERVAL_DAY;
            default: throw new IllegalArgumentException("Invalid argument");
        }
    }
//
//    @MessageRequest("/helloCallback")
//    public String helloWithCustomCallback(TelegramRequest request, User user) {
//        request.setCallback(new Callback() {
//            @Override
//            public void onResponse(BaseRequest request, BaseResponse response) {
//                System.out.println("gello");
//                // TODO
//            }
//
//            @Override
//            public void onFailure(BaseRequest request, IOException e) {
//                System.out.println("hello");
//                // TODO
//            }
//        });
//        return "Hello, " + user.firstName() + "!";
//    }
}
