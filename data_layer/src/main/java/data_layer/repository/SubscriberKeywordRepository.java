package data_layer.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

import data_layer.entity.Keyword;
import data_layer.entity.SubscriberKeyword;

public interface SubscriberKeywordRepository extends CrudRepository<SubscriberKeyword, Long> {

    SubscriberKeyword getByChatIdAndKeyword(long chatId, Keyword keyword);

    List<SubscriberKeyword> getAllByKeyword(Keyword keyword);

    List<SubscriberKeyword> getAllByKeywordIn(Set<Keyword> keywords);

    List<SubscriberKeyword> getAllByChatId(long chatId);
}
