package dao;

import java.util.Collection;
import java.util.Optional;

public interface DataAccessObject<T, K> {

    T save(T data);

    Optional<T> findById(K key);

    Collection<T> findAll();
}
