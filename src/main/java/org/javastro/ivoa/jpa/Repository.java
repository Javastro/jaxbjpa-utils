package org.javastro.ivoa.jpa;
/*
 * Created on 12/08/2021 by Paul Harrison (paul.harrison@manchester.ac.uk).
 */

import java.util.List;
import java.util.Optional;

public interface Repository <T, ID>
{

    /**
     * Saves a given entity. Use the returned instance for further operations as the save operation might have changed the
     * entity instance completely.
     *
     * @param entity must not be {@literal null}.
     * @return the saved entity will never be {@literal null}.
     */
    <S extends T> S create(S entity);

    /**
     * updates a given entity. Use the returned instance for further operations as the save operation might have changed the
     * entity instance completely.
     *
     * @param entity must not be {@literal null}.
     * @return the saved entity will never be {@literal null}.
     */
    <S extends T> S update(S entity);

    /**
     * Retrieves an entity by its id.
     *
     * @param id must not be {@literal null}.
     * @return the entity with the given id or {@literal Optional#empty()} if none found
     * @throws IllegalArgumentException if {@code id} is {@literal null}.
     */
    Optional<T> findById(ID id);

    /**
     * Returns all instances of the type.
     *
     * @return all entities
     */
    Iterable<T> findAll();
 
    /**
     * Returns Some instances of the type.
     *
     * @return some entities
     */
     List<T> findSome(int maxResults, int firstResult);

    
    /**
     * Deletes the entity with the given id.
     *
     * @param id must not be {@literal null}.
     * @throws IllegalArgumentException in case the given {@code id} is {@literal null}
     */
    void deleteById(ID id);

    /**
     * Deletes a given entity.
     *
     * @param entity
     * @throws IllegalArgumentException in case the given entity is {@literal null}.
     */
    void destroy(T entity);

        /**
     * Returns the number of entities available.
     *
     * @return the number of entities
     */
    long count();

}
