package maratmingazovr.leetcode.tinkof;

import com.github.kshashov.telegram.api.MessageType;
import com.github.kshashov.telegram.api.TelegramMvcController;
import com.github.kshashov.telegram.api.bind.annotation.BotRequest;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.val;

@com.github.kshashov.telegram.api.bind.annotation.BotController
@RequiredArgsConstructor
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
        botService.addSubscriber(chat, user);
        return new SendMessage(chat.id(), "Hi " + user.firstName() + "! You are welcome.");
    }

    @BotRequest(value = "/stop", type = {MessageType.CALLBACK_QUERY, MessageType.MESSAGE})
    public BaseRequest sopt(User user, Chat chat) {
        botService.removeSubscriber(chat);
        return new SendMessage(chat.id(), "You was removed from subscribers list");
    }

    @BotRequest(value = "/subscribers", type = {MessageType.CALLBACK_QUERY, MessageType.MESSAGE})
    public BaseRequest subscribers(User user, Chat chat) {
        val subscribers = botService.getSubscribers();
        return new SendMessage(chat.id(), subscribers);
    }

    @BotRequest(value = "/balance", type = {MessageType.CALLBACK_QUERY, MessageType.MESSAGE})
    public BaseRequest BaseRequest(User user, Chat chat) {
        val portfolio = analyzerService.getPortfolio();
        return new SendMessage(chat.id(), portfolio);
    }



//    @MessageRequest("/hello {name:[\\S]+}")
//    public String helloWithName(@BotPathVariable("name") String userName) {
//        // Return a string if you need to reply with a simple message
//        return "Hello, " + userName;
//    }
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
