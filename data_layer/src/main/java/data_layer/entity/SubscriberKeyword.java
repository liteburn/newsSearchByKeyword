package data_layer.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SubscriberKeyword {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private long chatId;

    @ManyToOne
    @JoinColumn(name = "keyword_id")
    private Keyword keyword;
}
