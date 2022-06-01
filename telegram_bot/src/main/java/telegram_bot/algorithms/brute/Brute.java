package telegram_bot.algorithms.brute;

import telegram_bot.algorithms.KeywordSearch;

import java.util.HashSet;
import java.util.Set;

public class Brute implements KeywordSearch {

    public Set<String> getKeywords(String text, Set<String> keywords) {
        Set<String> output = new HashSet<>();
        for (String keyword: keywords) {
            if(checkKeyword(text, keyword)) {
                output.add(keyword);
            }
        }
        return output;
    }

    private boolean checkKeyword(String text, String keyword) {
        return text.contains(keyword);
    }

}
