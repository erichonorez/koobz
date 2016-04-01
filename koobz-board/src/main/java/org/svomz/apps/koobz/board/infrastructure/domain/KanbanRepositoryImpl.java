package org.svomz.apps.koobz.board.infrastructure.domain;

import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.EntityManager;

/**
 * Created by eric on 09/07/15.
 */
@NoRepositoryBean
public class KanbanRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements KanbanRepository<T, ID> {

  public KanbanRepositoryImpl(Class<T> domainClass, EntityManager em) {
    super(domainClass, em);
  }

  @Override
  public T findOrThrowException(ID primaryKey) throws EntityNotFoundException {
    T entity = this.findOne(primaryKey);
    if (entity == null) {
      throw new EntityNotFoundException();
    }
    return entity;
  }

  @Override
  public String nextIdentity() {
    return UUID.randomUUID().toString();
  }
}
