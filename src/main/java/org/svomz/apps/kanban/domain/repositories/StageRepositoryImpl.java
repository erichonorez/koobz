package org.svomz.apps.kanban.domain.repositories;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.svomz.apps.kanban.domain.entities.Stage;
import org.svomz.commons.persistence.jpa.AbstractJpaRepository;

public class StageRepositoryImpl extends AbstractJpaRepository<Stage, Long> implements
    StageRepository {

  @Inject
  public StageRepositoryImpl(EntityManager entityManager) {
    super(entityManager);
  }

}
