package com.data_preprocessing.consumer;

import com.data_preprocessing.kafkaEntity.Post;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class PravdaLifeConsumer extends Consumer {

    public Post getNewsFromHref(String href) throws IOException {
        Document page = Jsoup.connect(href).get();
        String header = getHeader(page);
        Elements elements = getElementsByClass(page, "article");

        return createPostFromElements(elements, header, href);
    }
}
