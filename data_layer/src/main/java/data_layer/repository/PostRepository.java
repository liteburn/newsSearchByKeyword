package data_layer.repository;

import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

import data_layer.entity.Post;

public interface PostRepository extends CrudRepository<Post, Long> {
    List<Post> getAllByDateAfterAndDateBefore(Date before, Date after);
}
