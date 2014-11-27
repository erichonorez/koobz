package org.svomz.apps.kanban.domain.repositories;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.svomz.apps.kanban.domain.entities.WorkItem;
import org.svomz.commons.infrastructure.persistence.jpa.AbstractJpaRepository;

public class WorkItemRepositoryImpl extends AbstractJpaRepository<WorkItem, Long> implements WorkItemRepository {

  @Inject
  public WorkItemRepositoryImpl(EntityManager entityManager) {
    super(entityManager);
  }

}
