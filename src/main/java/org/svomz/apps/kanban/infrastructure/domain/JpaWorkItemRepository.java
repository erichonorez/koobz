package org.svomz.apps.kanban.infrastructure.domain;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.svomz.apps.kanban.domain.WorkItem;
import org.svomz.apps.kanban.domain.WorkItemRepository;

public class JpaWorkItemRepository extends AbstractJpaRepository<WorkItem, Long> implements
    WorkItemRepository {

  @Inject
  public JpaWorkItemRepository(EntityManager entityManager) {
    super(entityManager);
  }

}
