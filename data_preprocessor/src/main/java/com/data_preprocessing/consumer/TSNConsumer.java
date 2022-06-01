package com.data_preprocessing.consumer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

import com.data_preprocessing.kafkaEntity.Post;

public class TSNConsumer extends Consumer {

    public Post getNewsFromHref(String href) throws IOException {
        Document page = Jsoup.connect(href).get();
        String header = getHeader(page);
        Elements elements = getElementsByClass(page, "c-article__box");

        return createPostFromElements(elements, header, href);
    }
}
