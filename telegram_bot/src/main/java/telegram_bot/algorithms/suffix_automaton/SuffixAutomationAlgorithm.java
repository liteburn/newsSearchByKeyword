package telegram_bot.algorithms.suffix_automaton;

import data_layer.entity.Keyword;
import telegram_bot.algorithms.KeywordSearch;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SuffixAutomationAlgorithm implements KeywordSearch {
    static final int MAXN = 100000;

    private SuffixAutomation automation = new SuffixAutomation();

    private int curr;

    private int sz;

    public Set<Keyword> getKeywordEntities(String text, List<Keyword> keywords) {
        buildAutomation(text);

        Set<Keyword> output = new HashSet<>();
        for(Keyword keyword: keywords) {
            if(isAccepted(keyword.getKeyword())) {
                output.add(keyword);
            }
        }
        return output;
    }

    public Set<String> getKeywords(String text, Set<String> keywords) {
        buildAutomation(text);

        Set<String> output = new HashSet<>();
        for(String keyword: keywords) {
            if(isAccepted(keyword)) {
                output.add(keyword);
            }
        }
        return output;
    }

    private void buildAutomation(String text) {
        curr = 0;
        sz = 1;

        for (int i = 0; i < text.length(); i++)
            addCharacter(text.charAt(i), i == text.length() - 1);
    }

    private void addCharacter(char c, boolean mark) {
        int prev = curr;

        automation.addState();

        curr = sz++;

        automation.setLength(curr, automation.getLength(prev) + 1);
        while (automation.getTransition(prev, c) == null) {
            automation.setTransition(prev, curr, c);
            prev = automation.getLink(prev);
        }
        if (automation.getTransition(prev, c) == curr) {
            automation.setLink(curr, 0);
            return;
        }

        int next = automation.getTransition(prev, c);
        if (automation.getLength(next) == automation.getLength(prev) + 1) {
            automation.setLink(curr, next);
            return;
        }

        int copy = sz++;
        automation.addState();

        automation.copyAllTransitions(copy, next);

        automation.setLink(copy, automation.getLink(next));
        automation.setLength(copy, automation.getLength(curr) + 1);

        automation.setLink(curr, copy);
        automation.setLink(next, copy);

        while (automation.getTransition(prev, c) == next) {
            automation.setTransition(prev, copy, c);
            prev = automation.getLink(prev);
        }
    }

    public boolean isAccepted(String s) {
        int currState = 0;
        for (int i = 0; i < s.length(); i++) {
            if (automation.getTransition(currState, s.charAt(i)) == null)
                return false;
            currState = automation.getTransition(currState, s.charAt(i));
        }
        return true;
    }

}