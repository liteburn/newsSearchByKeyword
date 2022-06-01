package data_layer.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import data_layer.entity.Keyword;

import java.util.List;
public interface KeywordRepository extends CrudRepository<Keyword, Long> {
    Keyword getByKeywordId(int keywordId);
    Keyword findKeywordByKeyword(String keyword);

    @Query("SELECT k FROM Keyword k")
    List<Keyword> getAll();
}
