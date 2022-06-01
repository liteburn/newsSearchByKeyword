package telegram_bot.algorithms.suffix_automaton;

import java.util.ArrayList;

public class SuffixAutomation {
    private ArrayList<AutomationState> automationStates = new ArrayList<>();

    public SuffixAutomation() {
        automationStates.add(new AutomationState());
    }

    public void addState() {
        automationStates.add(new AutomationState());
    }

    public int getLink(int state) {
        return automationStates.get(state).getLink();
    }

    public void setLink(int from, int to) {
        automationStates.get(from).setLink(to);
    }

    public int getLength(int state) {
        return automationStates.get(state).getLength();
    }

    public void setLength(int state, int length) {
        automationStates.get(state).setLength(length);
    }

    public Integer getTransition(int state, char c) {
        return automationStates.get(state).getEdges().get(c);
    }

    public void setTransition(int from, int to, char c) {
        automationStates.get(from).getEdges().put(c, to);
    }

    public void copyAllTransitions(int state, int copyFrom) {
        automationStates.get(state).getEdges().putAll(automationStates.get(copyFrom).getEdges());
    }

}
