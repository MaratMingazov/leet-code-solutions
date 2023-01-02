package maratmingazovr.leetcode.config;

import com.pengrad.telegrambot.TelegramBot;
import lombok.val;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import ru.tinkoff.piapi.core.InvestApi;

@Configuration
@EnableScheduling
public class InvestConfig {

    @Bean
    public InvestApi investApi() {
        var investApiToken = "";
        return InvestApi.createSandbox(investApiToken);
        //return InvestApi.create(token);
    }

    @Bean
    public TelegramBot telegramBot() {
        val telegramBotToken = "";
        return new TelegramBot(telegramBotToken);
    }
}
