/*
 * Created on 21 Mar 2011 by Paul Harrison (paul.harrison@manchester.ac.uk)
 * Copyright 2011 Astrogrid. All rights reserved.
 *
 * This software is published under the terms of the Astrogrid 
 * Software License, a copy of which has been included 
 * with this distribution in the LICENSE.txt file.  
 *
 */

package org.javastro.ivoa.jpa;

import java.util.function.Consumer;
import java.util.function.Function;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;


/**
 * Base JPA Repository implementation that contains a default {@link EntityManagerFactory}.
 * @author Paul Harrison (paul.harrison@manchester.ac.uk) 13 Sep 2011
 * @version $Revision: 1.2 $ $date$
 */
public abstract class BaseJPARepository {

    /** logger for this class */
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory
            .getLogger(BaseJPARepository.class);
    
    protected final EntityManagerFactory  emf;
    protected final String name; // the name of the persistence unit.
 
    public BaseJPARepository(String name, EntityManagerFactory emf) {
        this.emf = emf;
        this.name = name;
        
    }
    

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }


    
   /**
   * Run a block of code with a newly created EntityManager for the named Persistence Unit.
   *
   * @param readOnly Is the transaction read-only?
   * @param block Block of code to execute
   * @param <T> type of result
   * @return code execution result
   */
  protected <T> T withTransaction(boolean readOnly, Function<EntityManager, T> block) {
    EntityManager entityManager = null;
    EntityTransaction tx = null;

    try {
      entityManager = emf.createEntityManager();

      if (entityManager == null) {
        throw new RuntimeException("Could not create JPA entity manager for '" + name + "'");
      }


      if (!readOnly) {
        tx = entityManager.getTransaction();
        tx.begin();
      }

      T result = block.apply(entityManager);

      if (tx != null) {
        if (tx.getRollbackOnly()) {
          tx.rollback();
        } else {
          tx.commit();
        }
      }

      return result;

    } catch (Throwable t) {
      if (tx != null) {
        try {
          if (tx.isActive()) {
            tx.rollback();
          }
        } catch (Exception e) {
          logger.error("Could not rollback transaction", e);
        }
      }
      throw t;
    } finally {
      if (entityManager != null) {
        entityManager.close();
      }
    }
  }
  
   /**
   * Run a block of code with a newly created EntityManager .
   *
   * @param block Block of code to execute
   */
  protected void withTransaction(Consumer<EntityManager> block) {
    withTransaction(
        em -> {
          block.accept(em);
          return null;
        });
  }
  
   /**
   * Run a block of code with a newly created EntityManager.
   *
   * @param readOnly Is the transaction read-only?
   * @param block Block of code to execute
   */
  protected void withTransaction( boolean readOnly, Consumer<EntityManager> block) {
    withTransaction(
        readOnly,
        em -> {
          block.accept(em);
          return null;
        });
  }

   /**
   * Run a block of code with a newly created EntityManager for the named Persistence Unit.
   *
   * @param block Block of code to execute
   * @param <T> type of result
   * @return code execution result
   */
  protected <T> T withTransaction(Function<EntityManager, T> block) {
    return withTransaction( false, block);
  }
  

}

