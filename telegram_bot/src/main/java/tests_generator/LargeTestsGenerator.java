package tests_generator;

import telegram_bot.algorithms.brute.Brute;
import com.google.common.collect.ImmutableList;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class LargeTestsGenerator {
    private final int[] TEXT_SIZES = {4};
    private final int[] KEYS_NUMBERS = {6};
    private final int NUMBER_OF_TESTS = 10;
    private final int MAX_KEY_LENGTH = 50;
    private final String FILE_PATH = "src/test/resources/algorithms/large_tests";

    private String test_text;
    private Set<String> test_keywords;

    private List<List<String>> inputDataFiles = new ArrayList<>();


    public List<List<String>> generateTests() {
        for(int i = 0; i < TEXT_SIZES.length; i++) {
            for(int j = 0; j < KEYS_NUMBERS.length; j++) {
                generateTest(TEXT_SIZES[i], KEYS_NUMBERS[j]);
            }
        }
        return inputDataFiles;
    }

    private void generateTest(int text_size, int keys) {
        for(int test_number = 0; test_number < NUMBER_OF_TESTS; test_number++) {
            generateTestInput(text_size, keys, test_number);
            generateTestKeywords(text_size, keys, test_number);
            generateTestOutput(text_size, keys, test_number);

            addInputDataFiles(text_size, keys, test_number);
        }
    }



    private void generateTestInput(int text_size, int keys, int test_number) {
        String text = generateText(text_size);
        String fileName = generateTestFileName(text_size, keys, test_number, "input");

        writeTest(text, fileName);
    }

    private String generateText(int text_size) {
        test_text = getRandomString(getRandomNumberOfPower(text_size));
        return test_text;
    }

    private void generateTestKeywords(int text_size, int keys, int test_number) {
        String keywords = generateKeywords(keys);
        String fileName = generateTestFileName(text_size, keys, test_number, "key_words");

        writeTest(keywords, fileName);
    }

    private String generateKeywords(int keys) {
        Random random = new Random();
        Set<String> keywords = new HashSet<>();
        for(int i = 0; i < getRandomNumberOfPower(keys); i++) {
            int keywordSize = 5 + random.nextInt(MAX_KEY_LENGTH);
            String keyword = getRandomString(keywordSize);
            keywords.add(keyword);
        }
        test_keywords = keywords;
        return String.join(", ", keywords).strip();
    }

    private void generateTestOutput(int text_size, int keys, int test_number) {
        String output = getOutwords();
        String fileName = generateTestFileName(text_size, keys, test_number, "out_words");

        writeTest(output, fileName);
    }

    private String getOutwords() {
        Brute brute = new Brute();
        Set<String> outwords = brute.getKeywords(test_text, test_keywords);
        return String.join(", ", outwords).strip();
    }

    private void writeTest(String test, String fileName) {
        try{
            FileOutputStream file = new FileOutputStream(FILE_PATH + fileName);
            file.write(test.getBytes());
            file.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int getRandomNumberOfPower(int power) {
        return (int) (Math.pow(10, power) + Math.random() * Math.pow(10, power - 1));
    }

    private String getRandomString(int size) {
        String text = "";
        for(int i = 0; i < size; i++) {
            text += getRandomCharacter();
        }
        return text;
    }

    private char getRandomCharacter() {
        Random r = new Random();
        return (char)(r.nextInt(26) + 'a');
    }

    private String generateTestFileName(int text_size, int keys, int test_number, String testInputType) {
        return "algorithm_test_" + test_number + "_text_" + text_size + "_keys_" + keys + "_" + testInputType;
    }

    private void addInputDataFiles(int text_size, int keys, int test_number) {
        String input = FILE_PATH + generateTestFileName(text_size, keys, test_number, "input");
        String key_words = FILE_PATH + generateTestFileName(text_size, keys, test_number, "key_words");
        String out_words = FILE_PATH + generateTestFileName(text_size, keys, test_number, "out_words");

        inputDataFiles.add(ImmutableList.of(input, key_words, out_words));
    }
}
