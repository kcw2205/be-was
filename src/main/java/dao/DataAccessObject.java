package dao;

import java.util.Collection;
import java.util.Optional;

public interface DataAccessObject<T, K> {

    void addRecord(K key, T data);

    Optional<T> findById(K key);

    Collection<T> findAll();
}
