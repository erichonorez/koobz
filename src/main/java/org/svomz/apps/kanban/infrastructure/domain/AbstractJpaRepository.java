package org.svomz.apps.kanban.infrastructure.domain;

import java.lang.reflect.ParameterizedType;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.google.common.base.Preconditions;

/**
 * This implementation of the Repository interface is backed by a JPA implementation. Moreover this
 * implementation let the transaction management responsibility to the caller or an higher level
 * (e.g. container).
 * 
 * @author Eric Honorez
 *
 * @param <T> The entity type
 * @param <PK> The entity primary key type
 */
public abstract class AbstractJpaRepository<T, PK> implements Repository<T, PK> {

  private final EntityManager entityManager;
  private final Class<T> entityClass;

  @SuppressWarnings("unchecked")
  public AbstractJpaRepository(EntityManager entityManager) {
    Preconditions.checkNotNull(entityManager, "Supplied entity manager can't be null.");

    this.entityManager = entityManager;
    this.entityClass =
        (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass())
            .getActualTypeArguments()[0];
  }

  @Override
  public T find(PK primaryKey) throws EntityNotFoundException {
    Preconditions.checkNotNull(primaryKey, "Supplied primary key can't be null.");

    T entity = this.getEntityManager().find(this.entityClass, primaryKey);
    if (entity == null) {
      throw new EntityNotFoundException(this.entityClass, primaryKey.toString());
    }
    return entity;
  }

  @Override
  public List<T> findAll() {
    CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();
    Class<T> entityClass = this.getEntityClass();
    CriteriaQuery<T> cq = cb.createQuery(entityClass);
    Root<T> rootEntry = cq.from(entityClass);
    CriteriaQuery<T> all = cq.select(rootEntry);
    TypedQuery<T> allQuery = this.getEntityManager().createQuery(all);
    return allQuery.getResultList();
  }

  @Override
  public T create(T entity) {
    Preconditions.checkNotNull(entity, "Supplied entity key can't be null.");

    this.getEntityManager().persist(entity);
    return entity;
  }

  /**
   * In this implementation the behavior of this method is a create or update. If the given entity
   * does not exist in the database it will be created. If it exists this method will simply update
   * the record in database. In this implementation this method is backed by
   * {@link EntityManager#merge(Object)}.
   */
  @Override
  public T update(T entity) {
    Preconditions.checkNotNull(entity, "Supplied entity key can't be null.");

    return this.getEntityManager().merge(entity);
  }

  @Override
  public void delete(T entity) {
    Preconditions.checkNotNull(entity, "Supplied entity key can't be null.");

    this.getEntityManager().remove(entity);
  }

  protected EntityManager getEntityManager() {
    return this.entityManager;
  }

  protected Class<T> getEntityClass() {
    return this.entityClass;
  }

}
