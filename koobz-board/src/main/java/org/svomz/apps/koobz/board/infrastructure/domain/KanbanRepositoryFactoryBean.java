package org.svomz.apps.koobz.board.infrastructure.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import java.io.Serializable;

import javax.persistence.EntityManager;

/**
 * Created by eric on 09/07/15.
 */
public class KanbanRepositoryFactoryBean <R extends JpaRepository<T, I>, T,
  I extends Serializable> extends JpaRepositoryFactoryBean<R, T, I> {

  protected RepositoryFactorySupport createRepositoryFactory(EntityManager em) {
    return new KanbanRepositoryFactory(em);
  }

  private static class KanbanRepositoryFactory<T, I extends Serializable>
    extends JpaRepositoryFactory {

    private final EntityManager em;

    public KanbanRepositoryFactory(EntityManager em) {
      super(em);
      this.em = em;
    }

    @Override
    protected <T, ID extends Serializable> SimpleJpaRepository<?, ?> getTargetRepository(RepositoryInformation information, EntityManager entityManager) {
      return new KanbanRepositoryImpl<T, I>((Class<T>) information.getDomainType(), this.em);
    }

    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
      return KanbanRepositoryImpl.class;
    }
  }

}
