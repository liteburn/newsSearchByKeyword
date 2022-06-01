package com.data_preprocessing.consumer;

import com.data_preprocessing.kafkaEntity.Post;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CensorConsumer extends Consumer {

    public Post getNewsFromHref(String href) throws IOException {
        try {

            Document page = Jsoup.connect(href).userAgent("Mozilla").get();
            String header = getHeader(page);
            Elements elements = getElementsByClass(page, "news-text");
            return createPostFromElements(elements, header, href);
        } catch (HttpStatusException httpStatusException) {
            log.error("Censor html is not fetching");
        }
        return null;
    }
}
