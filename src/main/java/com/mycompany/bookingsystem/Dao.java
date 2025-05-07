package com.mycompany.bookingsystem;

import java.util.List;
import java.util.Optional;

/**
 * Generic DAO interface for CRUD operations
 *
 * @param <T> Entity type
 * @param <ID> Primary key type
 */
public interface Dao<T, ID> {

    /**
     * Save entity to database
     *
     * @param entity Entity to save
     * @return Saved entity with generated ID
     */
    T save(T entity);

    /**
     * Update existing entity
     *
     * @param entity Entity to update
     * @return Updated entity
     */
    T update(T entity);

    /**
     * Find entity by ID
     *
     * @param id Entity ID
     * @return Optional containing entity if found
     */
    Optional<T> findById(ID id);

    /**
     * Find all entities
     *
     * @return List of all entities
     */
    List<T> findAll();

    /**
     * Delete entity by ID
     *
     * @param id Entity ID
     * @return true if deletion was successful
     */
    boolean deleteById(ID id);
}
