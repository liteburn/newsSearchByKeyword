package telegram_bot.algorithms;

import java.util.Set;

public interface KeywordSearch {
    public Set<String> getKeywords(String text, Set<String> keywords);
}
