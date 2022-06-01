package com.data_preprocessing;

import com.google.gson.Gson;

import com.data_preprocessing.consumer.CensorConsumer;
import com.data_preprocessing.consumer.PravdaConsumer;
import com.data_preprocessing.consumer.PravdaLifeConsumer;
import com.data_preprocessing.consumer.TSNConsumer;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;

import java.io.IOException;


@Configuration
public class KafkaConfiguration {

    @Autowired KafkaTemplate<String, String> template;

    Gson gson = new Gson();

    CensorConsumer censorConsumer = new CensorConsumer();
    PravdaConsumer pravdaConsumer = new PravdaConsumer();
    PravdaLifeConsumer pravdaLifeConsumer = new PravdaLifeConsumer();
    TSNConsumer tsnConsumer = new TSNConsumer();

    @Bean
    public NewTopic pravdaTopic() {
        return TopicBuilder.name("pravda_test")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic censorTopic() {
        return TopicBuilder.name("censor")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic tsnTopic() {
        return TopicBuilder.name("tsn")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean NewTopic pravdaLifeTopic() {
        return TopicBuilder.name("pravda_life")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic outTopic() {
        return TopicBuilder.name("post")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @KafkaListener(id = "pravdaId", topics = "pravda")
    public void listenPravda(String href) throws IOException {
        template.send("post", gson.toJson(pravdaConsumer.getNewsFromHref(href)));
        template.send("telegram", gson.toJson(pravdaConsumer.getNewsFromHref(href)));
    }

    @KafkaListener(id = "pravdaLifeId", topics = "pravda_life")
    public void listenPravdaLife(String href) throws IOException {
        template.send("post", gson.toJson(pravdaLifeConsumer.getNewsFromHref(href)));
        template.send("telegram", gson.toJson(pravdaLifeConsumer.getNewsFromHref(href)));
    }

    @KafkaListener(id = "tsnId", topics = "tsn")
    public void listenTSN(String href) throws IOException {
        template.send("post", gson.toJson(tsnConsumer.getNewsFromHref(href)));
        template.send("telegram", gson.toJson(tsnConsumer.getNewsFromHref(href)));
    }

    @KafkaListener(id = "censorId", topics = "censor")
    public void listenCensor(String href) throws IOException {
        template.send("post", gson.toJson(censorConsumer.getNewsFromHref(href)));
        template.send("telegram", gson.toJson(censorConsumer.getNewsFromHref(href)));
    }
}
