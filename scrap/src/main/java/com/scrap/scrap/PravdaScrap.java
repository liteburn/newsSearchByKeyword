package com.scrap.scrap;

import com.scrap.constants.TopicConstants;

import org.apache.commons.lang3.time.DateUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.EOFException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Date;
import java.util.TimerTask;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import static com.scrap.constants.TimeConstants.DELAY_IN_MS;
import static com.scrap.constants.TimeConstants.PRAVDA_DATE_FORMAT;
import static com.scrap.utils.DateChecker.checkIfOutdatedPost;

@Slf4j
@RequiredArgsConstructor
public class PravdaScrap extends TimerTask {

    private final KafkaTemplate<String, String> template;

    private String lastHref;

    @SneakyThrows
    @Override
    public void run() {
        System.out.println(lastHref);
        try {
            Elements newsList = Jsoup.connect("https://www.pravda.com.ua/news/")
                    .get()
                    .getElementsByClass("article_news_list");
            for (Element news : newsList) {
                String href = getHref(news);
                if (lastHref == null){
                    lastHref = news.getElementsByTag("a").attr("href");
                    break;
                }
                else if (!lastHref.equals(news.getElementsByTag("a").attr("href"))) {
                    sendHrefToTopic(href);
                } else{
                    break;
                }
            }
            lastHref = newsList.get(0).getElementsByTag("a").attr("href");
        } catch (UnknownHostException | EOFException unknownHostException) {
            log.error("Bad connection");
            wait(10000);
            run();
        }
    }

    private String getHref(Element news) {
        String href = news.getElementsByTag("a").attr("href");
        if (!href.startsWith("https")) {
            return "https://www.pravda.com.ua" + href;
        }
        return href;
    }

    private void sendHrefToTopic(String href) {
        if (href.startsWith("https://life.pravda.com.ua/")) {
            template.send(TopicConstants.PRAVDA_LIFE, href);
            return;
        }
        template.send(TopicConstants.PRAVDA, href);
    }

    private Date getDate(Element news) {
        String time = news.getElementsByClass("article_time").text();
        LocalDate localDate = LocalDate.now();
        Date date = null;
        try {
            date = PRAVDA_DATE_FORMAT.parse(time);
            date = DateUtils.setYears(date, localDate.getYear());
            date = DateUtils.setMonths(date, localDate.getMonthValue());
            date = DateUtils.setDays(date, localDate.getDayOfMonth());
        } catch (ParseException e) {
            log.error("Date format for Pravda was changed.");
        }
        return date;
    }
}
