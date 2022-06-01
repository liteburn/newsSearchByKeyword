package data_layer.entity;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Keyword {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int keywordId;

    private String keyword;

    @OneToMany(mappedBy = "keyword")
    private Set<SubscriberKeyword> subscriberKeywords;
}
