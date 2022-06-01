package com.data_preprocessing.consumer;

import com.data_preprocessing.kafkaEntity.Post;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public abstract class Consumer {

    void writeFile(String fileName, List<String> elements) throws IOException {
        System.out.println(fileName);
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("texts/" + fileName));
        String res = String.join(" ", elements);
        bufferedWriter.write(res);
        bufferedWriter.close();
    }

    List<String> getWordsFromString(String header, Elements elements) {
        StringBuilder words = new StringBuilder(header + " ");
        for (Element element : elements) {
            words.append(element.text().toLowerCase()).append(".");
        }
        return Arrays.stream(words.toString().split("[^\\p{L}]+"))
                .filter(word -> word.length() > 0).toList();
    }

    String getFileName(Date date, String href, String type) {
        String[] hrefSplit = href.split("[-./]");
        return type + "_" + date.getTime() + "_" + hrefSplit[hrefSplit.length - 2] + ".txt";
    }

    Post createPostFromElements(Elements elements, String header, String href) throws IOException {
        List<String> words = getWordsFromString(header.toLowerCase(), elements);
        Date date = new Date(System.currentTimeMillis());
        String filePath = getFileName(date, href, this.getClass().getTypeName().replace("Consumer", ""));
        writeFile(filePath, words);
        return new Post(href, filePath, header);
    }

    String getHeader(Document page) {
        return page.getElementsByTag("h1").text();
    }

    Elements getElementsByClass(Document page, String className) {
        if (page.getElementsByClass(className).size() == 0){
            return new Elements();
        }
        return page.getElementsByClass(className).get(0)
                .getElementsByTag("p");
    }

    public abstract Post getNewsFromHref(String href) throws IOException;
}
