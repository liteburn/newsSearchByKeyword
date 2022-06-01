package telegram_bot.algorithms.suffix_automation;

import telegram_bot.algorithms.suffix_automaton.SuffixAutomationAlgorithm;
import com.google.common.collect.ImmutableList;
import lombok.Getter;
import org.apache.commons.io.IOUtils;
import org.opentest4j.AssertionFailedError;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SuffixAutomationTest {
    List<List<String>> inputDataFiles = ImmutableList.of(
            ImmutableList.of("input_test_1.txt", "key_words_1.txt", "out_words_1.txt"),
            ImmutableList.of("input_test_2.txt", "key_words_2.txt", "out_words_2.txt"),
            ImmutableList.of("input_test_3.txt", "key_words_3.txt", "out_words_3.txt"),
            ImmutableList.of("input_test_4.txt", "key_words_4.txt", "out_words_4.txt")
    );

    @DataProvider
    private Object[] data() {
        return inputDataFiles.stream()
                .map(data -> new SuffixAutomationTestData(data.get(0), data.get(1), data.get(2))).toArray();
    }

    @Test(dataProvider = "data")
    private void test(SuffixAutomationTestData testData) {
        String text = testData.getInputData();
        Set<String> keywords = testData.getInputKeyWords();

        SuffixAutomationAlgorithm automation = new SuffixAutomationAlgorithm();

        compareSets(testData.getOutputKeyWords(), automation.getKeywords(text, keywords));
    }

    private void compareSets(Set<String> expected, Set<String> actual) {
        if (expected.size() != actual.size()) {
            System.out.println(expected.size());
            System.out.println(actual.size());
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
class SuffixAutomationTestData {
    private final String inputData;
    private final Set<String> inputKeyWords;
    private final Set<String> outputKeyWords;
    private final String FILE_PATH = "src/test/resources/algorithms/suffix_automation/";

    public SuffixAutomationTestData(String inputDataFile, String inputKeyWordsFile, String outputKeyWordsFile){
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

            FileInputStream file = new FileInputStream(FILE_PATH + fileName);
            return IOUtils.toString(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

}