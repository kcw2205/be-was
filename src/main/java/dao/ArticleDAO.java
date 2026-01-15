package dao;

import model.Article;

import java.util.Optional;

public interface ArticleDAO extends DataAccessObject<Article, Long> {

    Optional<Article> findPreviousById(long id);

    Optional<Article> findNextById(long id);

    Optional<Article> findFirstOrderByIdDesc();
}
