package telegram_bot.algorithms.suffix_automaton;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;


@NoArgsConstructor
@Data
public class AutomationState {
    private int length = 0;
    private int link = 0;
    private HashMap<Character, Integer> edges = new HashMap<>();
}
