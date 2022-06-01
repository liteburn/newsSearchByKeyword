package telegram_bot.algorithms;

import telegram_bot.algorithms.aho_corasick.AhoCorasick;
import telegram_bot.algorithms.brute.Brute;
import telegram_bot.algorithms.suffix_automaton.SuffixAutomationAlgorithm;
import lombok.Getter;
import org.apache.commons.io.IOUtils;
import org.opentest4j.AssertionFailedError;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import tests_generator.LargeTestsGenerator;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PerformanceTest {
    private LargeTestsGenerator test_generator = new LargeTestsGenerator();
    private List<List<String>> inputDataFiles = test_generator.generateTests();

    @DataProvider
    private Object[] data() {
        System.out.println(inputDataFiles);
        return inputDataFiles.stream()
                .map(data -> new TestData(data.get(0), data.get(1), data.get(2))).toArray();
    }

    @Test(dataProvider = "data")
    public void testPerformanceSuffixAutomation(TestData testData) {
        String text = testData.getInputData();
        Set<String> keywords = testData.getInputKeyWords();

        KeywordSearch algorithm = new SuffixAutomationAlgorithm();

        compareSets(testData.getOutputKeyWords(), algorithm.getKeywords(text, keywords));
    }

    @Test(dataProvider = "data")
    public void testPerformanceAhoCorasick(TestData testData) {
        String text = testData.getInputData();
        Set<String> keywords = testData.getInputKeyWords();

        AhoCorasick algorithm = new AhoCorasick();

        compareSets(testData.getOutputKeyWords(), algorithm.getKeywords(text, keywords));
    }

    @Test(dataProvider = "data")
    public void testPerformanceBrute(TestData testData) {
        String text = testData.getInputData();
        Set<String> keywords = testData.getInputKeyWords();

        KeywordSearch algorithm = new Brute();

        compareSets(testData.getOutputKeyWords(), algorithm.getKeywords(text, keywords));
    }

    public void compareSets(Set<String> expected, Set<String> actual) {
        if (expected.size() != actual.size()) {
            System.out.println(expected.size());
            System.out.println(actual.size());
            actual.removeAll(expected);
            System.out.println(actual);
            throw new AssertionFailedError("Expected: " + expected + " but was: " + actual + ".");
        }
        for (String key : expected) {
            if (!actual.contains(key)) {
                throw new AssertionFailedError("Expected: " + expected + "but was: " + actual + ".");
            }
        }
    }
}

@Getter
class TestData {
    private final String inputData;
    private final Set<String> inputKeyWords;
    private final Set<String> outputKeyWords;

    public TestData(String inputDataFile, String inputKeyWordsFile, String outputKeyWordsFile){
        inputData = readFile(inputDataFile);
        inputKeyWords = getKeyWords(inputKeyWordsFile);
        outputKeyWords = getKeyWords(outputKeyWordsFile);
    }

    private Set<String> getKeyWords(String keyWordsFile){
        return Arrays.stream(readFile(keyWordsFile)
                        .split(", "))
                .filter(word -> word.length() > 0)
                .collect(Collectors.toSet());
    }

    private String readFile(String fileName){
        try{

            FileInputStream file = new FileInputStream(fileName);
            return IOUtils.toString(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

}