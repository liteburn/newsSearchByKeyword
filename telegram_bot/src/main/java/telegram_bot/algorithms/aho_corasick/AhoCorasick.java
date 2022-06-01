package telegram_bot.algorithms.aho_corasick;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import telegram_bot.algorithms.KeywordSearch;

import java.util.*;

@AllArgsConstructor
@NoArgsConstructor
public class AhoCorasick implements KeywordSearch {
    private final List<AutomationState> automation = new ArrayList<>();
    private final HashMap<Integer, Integer> keywordLengths = new HashMap<>();
    private final int ROOT = 0;
    private ArrayList<String> keyWords;

    public Set<String> getKeywords(String text, Set<String> input_keywords) {
        keyWords = new ArrayList<>(input_keywords);
        buildAutomation();
        return findKeywords(text);
    }

    private Set<String> findKeywords(String text) {
        int currentState = 0;
        Set<String> result = new HashSet<>();
        String cur_text = "";
        for (int i = 0; i < text.length(); ++i)
        {
            cur_text += text.charAt(i);
            currentState = findNextState(currentState, text.charAt(i));

            if (automation.get(currentState).isLeaf) {
                for (int id: automation.get(currentState).wordIDs) {
                    if(check(cur_text, keyWords.get(id))) {
                        result.add(keyWords.get(id));
                    }
                }

            }
        }

        return result;
    }

    private int findNextState(int currentState, char nextInput)
    {
        int answer = currentState;

        while (hasNotLinkToCharacter(nextInput, answer))
            answer = automation.get(answer).suffixLink;

        return getLink(nextInput, answer);
    }

    private boolean check(String text, String word) {
        if(text.length() < word.length()) {
            return false;
        }

        for(int i = 0; i < word.length(); i++) {
            if(text.charAt(text.length() - i - 1) != word.charAt(word.length() - i - 1)) {
                return false;
            }
        }
        return true;
    }

    private void buildAutomation() {
        initRoot();
        addKeywords(keyWords);
    }

    private void initRoot() {
        automation.add(new AutomationState());
    }

    private void addKeywords(ArrayList<String> keyWords) {
        for(int i = 0; i < keyWords.size(); i++) {
            addKeyword(keyWords.get(i), i);
        }
    }

    private void addKeyword(String keyword, int wordId) {
        int stateIndex = ROOT;

        for (char c : keyword.toCharArray()) {
            if (hasNotLinkToCharacter(c, stateIndex)) {
                addState(c, stateIndex);
                addLink(c, stateIndex);
            }
            stateIndex = getLink(c, stateIndex);
        }

        setAutomationFiniteState(stateIndex, wordId);
        setKeywordLengths(wordId, keyword);
    }

    private boolean hasNotLinkToCharacter(char nextCharacter, int stateIndex) {
        AutomationState state = automation.get(stateIndex);

        return !state.links.containsKey(nextCharacter);
    }

    private void addState(char character, int stateIndex) {
        AutomationState state = new AutomationState();
        state.suffixLink = -1;
        state.parent = stateIndex;
        state.parentChar = character;

        automation.add(state);
    }

    private void addLink(char character, int stateIndex) {
        AutomationState parentState = automation.get(stateIndex);

        parentState.links.put(character, stateIndex);
    }

    private int getLink(char character, int stateIndex) {
        AutomationState state = automation.get(stateIndex);

        return state.links.get(character);
    }

    private void setAutomationFiniteState(int stateIndex, int wordId) {
        AutomationState state = automation.get(stateIndex);
        state.isLeaf = true;
        state.wordIDs.add(wordId);
        automation.set(stateIndex, state);
    }

    private void setKeywordLengths(int wordId, String keyword) {
        keywordLengths.put(wordId, keyword.length());
    }
}
