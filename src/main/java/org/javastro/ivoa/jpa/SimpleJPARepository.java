/* 
 *  Created on 07-Jun-2011 by Paul Harrison  (paul.harrison@manchester.ac.uk) 
 * 
 * This software is published under the terms of the Academic
 * Free License, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 * 
 *  Copyright (c) The University of Manchester. All rights reserved.
 *
 */
package org.javastro.ivoa.jpa;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.javastro.ivoa.jpa.exceptions.NonexistentEntityException;
import org.javastro.ivoa.jpa.exceptions.PreexistingEntityException;

/**
 *
 * @author Paul Harrison  (paul.harrison@manchester.ac.uk) 
 */
public class SimpleJPARepository<T, ID> extends BaseJPARepository implements Repository<T, ID>  {

    
    private final EntityMetadata<T, ID> meta;
    
    /**
     * @param name
     * @param emf
     */
    public SimpleJPARepository(String name, EntityManagerFactory emf, EntityMetadata<T, ID> meta) {
        super(name, emf);
        this.meta = meta;
    }

    protected ID entityID(T entity) {
        return meta.getID(entity);
    }

    protected Class<T> entityClass() {
        return meta.getJavaType();
    }

    @Override
    public <S extends T> S create(S entity)  {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
            return entity;
            
        } catch (Exception ex) {
            if (findById(meta.getID(entity)) != null) {
                throw new PreexistingEntityException("Resource " + entity + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

     @Override
     public <S extends T> S update(S entity) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            entity = em.merge(entity);
            em.getTransaction().commit();
            return entity;
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
               
                if (findById(meta.getID(entity)) == null) {
                    throw new NonexistentEntityException("The entity with id " + entityID(entity) + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Override
    public void deleteById(ID id) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            T entity;
            try {
                entity =  em.getReference(entityClass(), id);
                entityID(entity);
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The entity with id " + id + " no longer exists.", enfe);
            }
            em.remove(entity);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }


 
      @Override
    public Iterable<T> findAll() {
        return findEntities(true, -1, -1);
    }

    @Override
    public List<T> findSome(int maxResults, int firstResult) {
        return findEntities(false, maxResults, firstResult);
    }

    private List<T> findEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery<T> cq = em.getCriteriaBuilder().createQuery(entityClass());
            cq.select(cq.from(entityClass()));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }


     @Override
     public long count() {
        EntityManager em = getEntityManager();
        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Long> cq = builder.createQuery(Long.class);
            Root<T> rt = cq.from(entityClass());
            cq.select(builder.count(rt));
            return em.createQuery(cq).getSingleResult();
        } finally {
            em.close();
        }
    }


  /**
     * {@inheritDoc}
     * overrides @see org.javastro.ivoa.entities.jpa.Repository#findById(java.lang.Object)
     */
    @Override
    public Optional<T> findById(ID id) {
        EntityManager em = getEntityManager();
        try {
            return Optional.ofNullable(em.find(entityClass(), id));
        } finally {
            em.close();
        }
        
    }

    /**
     * {@inheritDoc}
     * overrides @see org.javastro.ivoa.entities.jpa.Repository#destroy(java.lang.Object)
     */
    @Override
    public void destroy(T entity) {
       EntityManager em = getEntityManager();
       em.remove(entity);
        
    }




  
    
}
