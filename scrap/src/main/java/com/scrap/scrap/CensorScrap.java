package com.scrap.scrap;

import com.scrap.constants.TopicConstants;

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

import static com.scrap.constants.TimeConstants.CENSOR_DATE_FORMAT;
import static com.scrap.utils.DateChecker.checkIfOutdatedPost;

@Slf4j
@RequiredArgsConstructor
public class CensorScrap extends TimerTask {
    private final KafkaTemplate<String, String> template;
    private String lastHref = null;

    @SneakyThrows
    @Override
    public void run() {
        System.out.println(lastHref);

        Elements newsLst = Jsoup.connect("https://censor.net/ua/news/all")
                .get()
                .getElementsByClass("news-list-item");
        for (Element news : newsLst) {
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

    private String getHref(Element element) {
        return element.getElementsByTag("a").attr("href");
    }


    private Date getDate(Element news) {
        String postDate = news.getElementsByTag("time").attr("datetime");
        Date date = null;
        try {
            date = CENSOR_DATE_FORMAT.parse(postDate);
        } catch (ParseException e) {
            log.error("Date format for TSN was changed.");
        }
        return date;
    }
}
