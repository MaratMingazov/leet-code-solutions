package maratmingazovr.leetcode.tinkof;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
@Data
public class BotService {

    @NonNull
    private final Map<Long, String> subscribers = new HashMap<>();

    @NonNull
    private final TelegramBot bot;

    public void addSubscriber(@NonNull Chat chat,
                              @NonNull User user) {
        subscribers.put(chat.id(), user.firstName() + " " + user.lastName());
    }

    public void removeSubscriber(@NonNull Chat chat) {
        subscribers.remove(chat.id());
    }

    @NonNull
    public String getSubscribers() {
        return String.join(",", subscribers.values());
    }

    public void sendMassage(@NonNull String message) {
        subscribers.keySet().forEach(key -> bot.execute(new SendMessage(key, message)));
    }


}
