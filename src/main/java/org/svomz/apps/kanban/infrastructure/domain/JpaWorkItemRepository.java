package org.svomz.apps.kanban.infrastructure.domain;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.svomz.apps.kanban.domain.entities.WorkItem;
import org.svomz.apps.kanban.domain.repositories.WorkItemRepository;
import org.svomz.commons.persistence.jpa.AbstractJpaRepository;

public class JpaWorkItemRepository extends AbstractJpaRepository<WorkItem, Long> implements
    WorkItemRepository {

  @Inject
  public JpaWorkItemRepository(EntityManager entityManager) {
    super(entityManager);
  }

}
