package telegram_bot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import data_layer.repository.KeywordRepository;
import data_layer.repository.PostRepository;
import data_layer.repository.SubscriberKeywordRepository;
import lombok.extern.slf4j.Slf4j;
import telegram_bot.service.NewsSearchBot;


@SpringBootApplication(scanBasePackages = {"telegram_bot", "telegram_bot.service"})
@EnableJpaRepositories(basePackages = {"data_layer.repository"})
@EntityScan(basePackages = {"data_layer.entity"})
@Slf4j
public class TelegramBotApp {

    @Autowired KeywordRepository keywordRepository;
    @Autowired SubscriberKeywordRepository subscriberKeywordRepository;

    @Autowired NewsSearchBot newsSearchBot;
    @Autowired
    PostRepository postRepository;

    public static void main(String[] args) {
        SpringApplication.run(TelegramBotApp.class, args);
    }

    @Bean
    public void telegram() throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);

        try {
            telegramBotsApi.registerBot(newsSearchBot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Bean
    NewsSearchBot getNewsSearchBot() {
        return new NewsSearchBot(keywordRepository, subscriberKeywordRepository, postRepository);
    }

}
