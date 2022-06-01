package telegram_bot.algorithms.aho_corasick;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AutomationState {
    public HashMap<Character, Integer> links = new HashMap<>();;

    public boolean isLeaf = false;

    public int parent = -1;

    public char parentChar;

    public int suffixLink = -1;

    public int endWordLink = -1;

    public List<Integer> wordIDs = new ArrayList<>();

}
