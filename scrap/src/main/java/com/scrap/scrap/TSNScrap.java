package com.scrap.scrap;

import com.scrap.utils.DateChecker;
import com.scrap.constants.TopicConstants;

import org.apache.commons.lang3.time.DateUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.kafka.core.KafkaTemplate;

import java.text.ParseException;
import java.util.Date;
import java.util.TimerTask;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import static com.scrap.constants.TimeConstants.TSN_DATE_FORMAT;

@Slf4j
@RequiredArgsConstructor
public class TSNScrap extends TimerTask {

    private final KafkaTemplate<String, String> template;
    private String lastHref;

    @SneakyThrows
    @Override
    public void run() {
        System.out.println(lastHref);
        Elements newsLst = Jsoup.connect("https://tsn.ua/news")
                .get()
                .getElementsByTag("article");
        for (Element news: newsLst) {
            String href = getHref(news);
            if (lastHref == null){
                lastHref = href;
                break;
            }
            else if (!lastHref.equals(news.getElementsByTag("a").attr("href"))) {
                template.send(TopicConstants.TSN, href);
            } else{
                break;
            }
        }

        lastHref = getHref(newsLst.get(0));
    }

    private String getHref(Element element){
        return element.getElementsByClass("c-card__link").attr("href");
    }


    private Date getDate(Element news){
        String postDate = news.getElementsByTag("time").get(0).text();
        int yearDifference = 2000;
        Date date = null;
        try {
            date = TSN_DATE_FORMAT.parse(postDate);
            date = DateUtils.addYears(date, yearDifference);
        } catch (ParseException e) {
            log.error("Date format for TSN was changed.");
        }
        return date;
    }
}
