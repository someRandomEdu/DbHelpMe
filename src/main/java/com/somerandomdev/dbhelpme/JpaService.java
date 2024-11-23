package com.somerandomdev.dbhelpme;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public abstract class JpaService<T, Id> {
    protected final JpaRepository<T, Id> repository;

    protected JpaService(JpaRepository<T, Id> repository) {
        this.repository = repository;
    }

    public long count() {
        return repository.count();
    }

    public <S extends T> S save(S entity) {
        return repository.save(entity);
    }

    public <S extends T> Iterable<S> saveAll(Iterable<S> entities) {
        return repository.saveAll(entities);
    }

    public Optional<T> findById(Id id) {
        return repository.findById(id);
    }

    public List<T> findAll() {
        return repository.findAll();
    }

    public List<T> findAllById(Iterable<Id> ids) {
        return repository.findAllById(ids);
    }

    public void deleteById(Id id) {
        repository.deleteById(id);
    }

    public void delete(T entity) {
        repository.delete(entity);
    }

    public void deleteAllById(Iterable<? extends Id> ids) {
        repository.deleteAllById(ids);
    }

    public void deleteAll() {
        repository.deleteAll();
    }

    public void deleteAll(Iterable<? extends T> entities) {
        repository.deleteAll(entities);
    }

    public List<T> findAllBy(Predicate<? super T> predicate) {
        var result = new ArrayList<T>();

        for (T entity : repository.findAll()) {
            if (predicate.test(entity)) {
                result.add(entity);
            }
        }

        return result;
    }

    public Optional<T> findFirstBy(Predicate<? super T> predicate) {
        var result = findAllBy(predicate);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.getFirst());
    }

    public Optional<T> findLastBy(Predicate<? super T> predicate) {
        var result = findAllBy(predicate);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.getLast());
    }

    public Optional<T> findOneBy(Predicate<? super T> predicate) {
        return findFirstBy(predicate);
    }
}
