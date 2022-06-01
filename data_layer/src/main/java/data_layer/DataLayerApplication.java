package data_layer;

import com.google.gson.Gson;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Date;

import data_layer.entity.Post;
import data_layer.repository.KeywordRepository;
import data_layer.repository.PostRepository;
import data_layer.repository.SubscriberKeywordRepository;

@SpringBootApplication
public class DataLayerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataLayerApplication.class, args);
    }

    @Autowired KafkaTemplate<String, String> template;
    @Autowired PostRepository postRepository;
    Gson gson = new Gson();
    @KafkaListener(id = "postId", topics = "post")
    public void listen(String kafkaPost) {
        Post post = gson.fromJson(kafkaPost, Post.class);
        post.setDate(new Date(System.currentTimeMillis()));
        postRepository.save(post);
    }
    @Autowired SubscriberKeywordRepository subscriberKeywordRepository;
    @Autowired KeywordRepository keywordRepository;
}
