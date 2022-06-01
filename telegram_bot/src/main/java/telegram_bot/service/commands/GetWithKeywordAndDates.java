package telegram_bot.service.commands;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import data_layer.entity.Post;
import data_layer.repository.PostRepository;
import lombok.AllArgsConstructor;

import static telegram_bot.service.constants.BotSettings.GET_BY_KEYWORD_AND_DATES_ID;
import static telegram_bot.service.constants.DateConstants.DATE_FORMAT;

@AllArgsConstructor
public class GetWithKeywordAndDates {
    PostRepository postRepository;

    public String getMessageText(String message_text){
        try{
            List<String> arr = Arrays.stream(message_text.split(" ")).toList();
            Date from = DATE_FORMAT.parse(arr.get(1));
            Calendar c = Calendar.getInstance();
            if (arr.size() > 2){
                c.setTime(DATE_FORMAT.parse(arr.get(2)));
            } else {
                c.setTime(DATE_FORMAT.parse(arr.get(1)));
            }
            c.add(Calendar.DAY_OF_MONTH, GET_BY_KEYWORD_AND_DATES_ID);
            Date to = c.getTime();
            String keyword = arr.get(0);
            return getNewsByTimeAndKeyword(keyword, from, to);
        } catch (ParseException | FileNotFoundException parseException){
            return "WTF";
        }
    }
    private boolean checkIfAcceptable(String message){
        return message.matches("\\p{L}+( \\p{N}{4}-\\p{N}{1,2}-\\p{N}{1,2}){1,2}");
    }

    public String getNewsByTimeAndKeyword(String keyword, Date after, Date before) throws FileNotFoundException {
        List<Post> posts = postRepository.getAllByDateAfterAndDateBefore(after, before);
        StringBuilder builder = new StringBuilder();
        builder.append(keyword).append("\n");
        for (Post post : posts) {
            File myObj = new File("texts/" + post.getPathToFile());
            Scanner myReader = new Scanner(myObj);
            String data;
            data = myReader.nextLine();
            if (data.contains(keyword)) {
                builder.append(post.getTitle()).append('\n').append(post.getHref()).append('\n');
            }
        }
        return builder.toString();
    }
}
