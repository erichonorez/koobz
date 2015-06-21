package org.svomz.apps.kanban.infrastructure.domain;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.svomz.apps.kanban.domain.Stage;
import org.svomz.apps.kanban.domain.StageRepository;

public class JpaStageRepository extends AbstractJpaRepository<Stage, Long> implements
    StageRepository {

  @Inject
  public JpaStageRepository(EntityManager entityManager) {
    super(entityManager);
  }

  @Override
  public Stage find(long boardId, long stageId) throws EntityNotFoundException {
    TypedQuery<Stage> query = this.getEntityManager().createNamedQuery(
        "Stage.findByBoardIdAndStageId", Stage.class);
    query.setParameter("boardId", boardId);
    query.setParameter("stageId", stageId);
    query.setMaxResults(1);
    
    try {
      return query.getSingleResult();
    } catch (NoResultException ex) {
      throw new EntityNotFoundException(Stage.class, String.valueOf(stageId));
    }
  }

}
