package telegram_bot;


import com.google.gson.Gson;

import org.apache.kafka.clients.admin.NewTopic;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.telegram.telegrambots.meta.TelegramBotsApi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import data_layer.entity.Keyword;
import data_layer.entity.Post;
import data_layer.entity.SubscriberKeyword;
import data_layer.repository.KeywordRepository;
import data_layer.repository.SubscriberKeywordRepository;
import telegram_bot.algorithms.suffix_automaton.SuffixAutomation;
import telegram_bot.algorithms.suffix_automaton.SuffixAutomationAlgorithm;
import telegram_bot.service.NewsSearchBot;

@Configuration
public class KafkaTopicsConsumer {

    @Autowired KafkaTemplate<String, String> template;
    @Autowired KeywordRepository keywordRepository;
    @Autowired SubscriberKeywordRepository subscriberKeywordRepository;
    @Autowired NewsSearchBot newsSearchBot;

    Gson gson = new Gson();

    @Bean
    public NewTopic telegramTopic() {
        return TopicBuilder.name("telegram")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @KafkaListener(id = "telegramId", topics = "telegram")
    public void telegramConsumer(String postStr) {
        System.out.println(postStr);
        Post post = gson.fromJson(postStr, Post.class);
        List<Keyword> keywords = keywordRepository.getAll();
        Set<Keyword> res = containingKeywords(keywords, post.getPathToFile());
        System.out.println(res);
        List<SubscriberKeyword> subscriberKeywords = subscriberKeywordRepository.getAllByKeywordIn(res);
        subscriberKeywords.sort(Comparator.comparing(SubscriberKeyword::getChatId));
        for (SubscriberKeyword subscriberKeyword : subscriberKeywords) {
            System.out.print(subscriberKeyword.getChatId());
            System.out.println("\n" + subscriberKeyword.getKeyword().getKeyword());
        }
        long chatId = -1;
        List<Keyword> keywordsCurrent = new ArrayList<>();
        for (SubscriberKeyword subscriberKeyword : subscriberKeywords) {
            if (chatId == subscriberKeyword.getChatId()) {
                keywordsCurrent.add(subscriberKeyword.getKeyword());
            } else {
                if (keywordsCurrent.size() > 0) {
                    newsSearchBot.sendMessageToUser(String.valueOf(chatId), createMessage(keywordsCurrent, post));
                    keywordsCurrent = new ArrayList<>();
                }
                keywordsCurrent.add(subscriberKeyword.getKeyword());
                chatId = subscriberKeyword.getChatId();
            }
        }
        if (keywordsCurrent.size() > 0) {
            System.out.println("WTF");
            newsSearchBot.sendMessageToUser(String.valueOf(chatId), createMessage(keywordsCurrent, post));
        }
    }

    public String getDataFromFile(String filePath) throws IOException {
        try {

            File file = new File(filePath);
            Scanner scanner = new Scanner(file);
            return scanner.nextLine();
        } catch (FileNotFoundException e) {

            throw new RuntimeException(e);
        }
    }

    public String createMessage(List<Keyword> keywords, Post post) {
        StringBuilder res = new StringBuilder();
        res.append("Contains keywords: ");
        for (int i = 0; i < keywords.size() - 1; i++) {
            res.append(keywords.get(i).getKeyword()).append(", ");
        }
        res.append(keywords.get(keywords.size() - 1).getKeyword()).append(".\n");
        res.append(post.getHref());
        return res.toString();
    }

    private Set<Keyword> containingKeywords(List<Keyword> keywords, String pathToFile) {
        try {
            SuffixAutomationAlgorithm suffixAutomation = new SuffixAutomationAlgorithm();
            String file = getDataFromFile("texts/" + pathToFile);
            Scanner scanner = new Scanner(file);
            String text = scanner.nextLine();
            return suffixAutomation.getKeywordEntities(text, keywords);
        } catch (IOException ioException) {
            return null;
        }

    }

}
