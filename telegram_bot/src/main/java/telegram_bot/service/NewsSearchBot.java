package telegram_bot.service;

import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.GetMyCommands;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import data_layer.entity.Keyword;
import data_layer.entity.SubscriberKeyword;
import data_layer.repository.KeywordRepository;
import data_layer.repository.PostRepository;
import data_layer.repository.SubscriberKeywordRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import telegram_bot.service.commands.GetWithKeywordAndDates;

import static telegram_bot.service.constants.BotSettings.DELETE_ID;
import static telegram_bot.service.constants.BotSettings.GET_BY_KEYWORD_AND_DATES_ID;

@RequiredArgsConstructor
@Configuration
@Slf4j
public class NewsSearchBot extends TelegramLongPollingBot {

    private final Map<Long, Integer> userLastCommand = new HashMap<>();
    private final KeywordRepository keywordRepository;
    private final SubscriberKeywordRepository subscriberKeywordRepository;
    private final PostRepository postRepository;

    {
        List<BotCommand> commandsList = new ArrayList<>();
        commandsList.add(new BotCommand("keywords", "Вивести усі ключові слова"));
        commandsList.add(new BotCommand("get_news_by_keyword_and_dates",
                "Отримати новини по ключовому слову та датам"));
        commandsList.add(new BotCommand("delete", "Видалити слово'"));
        try {
            execute(SetMyCommands.builder().commands(commandsList).build());
            execute(new GetMyCommands());
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getBotUsername() {
        return "keyword_news_search_bot";
    }

    @Override
    public String getBotToken() {
        return "TOKEN";
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        // We check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message_text = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            SendMessage message = new SendMessage();
            message.setChatId(String.valueOf(chatId));
            if (message_text.startsWith("/")) {
                if (message_text.startsWith("/get_news_by_keyword_and_dates")) {
                    userLastCommand.put(chatId, 2);
                    message.setText("Напишіть ключове слово та дати в такому форматі: \nслово 2022-02-15 2022-03-24");
                    execute(message);
                } else if (message_text.matches("/delete")) {
                    userLastCommand.put(chatId, DELETE_ID);
                    message.setText("Яке слово видалити?");
                    writeMessage(message);
                } else if (message_text.matches("/keywords")) {
                    sendUserKeywordsToUser(update);
                }
            } else {
                if (userLastCommand.containsKey(chatId)) {
                    if (userLastCommand.get(chatId).equals(DELETE_ID)){
                        message.setText(deleteKeyword(message_text, chatId));
                        if (message.getText().length() > 0) {
                            writeMessage(message);
                        }
                    } else if (userLastCommand.get(chatId).equals(GET_BY_KEYWORD_AND_DATES_ID)) {
                        GetWithKeywordAndDates getWithKeywordAndDates = new GetWithKeywordAndDates(postRepository);
                        message.setText(getWithKeywordAndDates.getMessageText(message_text));
                        writeMessage(message);
                    }
                }  else if (message_text.matches("\\p{L}+")){
                    saveKeywordToDB(update);
                }
            }
            userLastCommand.remove(chatId);
        }
    }

    public Keyword getKeywordId(String keyword) {
        if (keyword.matches("\\p{L}+")) {
            keyword = keyword.toLowerCase();
            Keyword key = keywordRepository.findKeywordByKeyword(keyword);
            if (key == null) {
                key = keywordRepository.save(Keyword.builder().keyword(keyword).build());
            }
            return key;
        }
        return null;
    }

    public boolean checkIfSubscriberKeywordConnectionExists(Keyword keyword, long chatId) {
        SubscriberKeyword subscriberKeyword = subscriberKeywordRepository.getByChatIdAndKeyword(chatId, keyword);
        if (subscriberKeyword == null) {
            subscriberKeywordRepository.save(SubscriberKeyword.builder()
                    .chatId(chatId)
                    .keyword(keyword)
                    .build());
            return false;
        }
        return true;
    }

    public String deleteKeyword(String keywordName, long chatId) {
        Keyword keyword = keywordRepository.findKeywordByKeyword(keywordName);
        if (keyword != null) {
            SubscriberKeyword subscriberKeyword = subscriberKeywordRepository.getByChatIdAndKeyword(chatId, keyword);
            if (subscriberKeyword != null) {
                subscriberKeywordRepository.delete(subscriberKeyword);
                if (subscriberKeywordRepository.getAllByKeyword(keyword).size() == 0) {
                    keywordRepository.delete(keyword);
                }
                return "Word " + keywordName + " removed.";
            }
        }
        return "";
    }

    private void saveKeywordToDB(Update update) {
        long chatId = update.getMessage().getChatId();
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        String message_text = update.getMessage().getText();
        if (message_text.length() < 2){
            message.setText("Слово має складатись мінімум з двох букв");
        } else {
            Keyword keyword = getKeywordId(message_text);

            if (keyword != null) {
                if (checkIfSubscriberKeywordConnectionExists(keyword, chatId)) {
                    message.setText("Слово вже було додано");
                } else {
                    message.setText("Слово додано");
                }
            } else {
                message.setText("Слово повинне складатися лише з букв");
            }
        }
        writeMessage(message);
    }

    public void sendMessageToUser(String chatId, String message) {
        try {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setText(message);
            sendMessage.setChatId(chatId);
            execute(sendMessage);
        } catch (TelegramApiException telegramApiException) {
            log.error("Telegram send is not working properly");
        }
    }

    private void writeMessage(SendMessage sendMessage){
        try {
            execute(sendMessage);
        } catch (TelegramApiException telegramApiException){
            telegramApiException.printStackTrace();
        }
    }

    private void sendUserKeywordsToUser(Update update){
        long chatId = update.getMessage().getChatId();
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        List<SubscriberKeyword> subscriberKeywords = subscriberKeywordRepository.getAllByChatId(chatId);
        StringBuilder words = new StringBuilder().append("Ваші ключові слова: ");
        subscriberKeywords.forEach(sk -> words.append('\n').append(sk.getKeyword().getKeyword()));
        message.setText(words.toString());
        writeMessage(message);
    }
    // TODO: SAVE EACH USER LAST COMMANDS NEEDED FOR EXECUTION (expected 10^7, not too big set)
}
