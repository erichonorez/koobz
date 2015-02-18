package org.svomz.apps.kanban.domain.repositories;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.svomz.apps.kanban.domain.entities.Stage;
import org.svomz.commons.persistence.EntityNotFoundException;
import org.svomz.commons.persistence.jpa.AbstractJpaRepository;

public class StageRepositoryImpl extends AbstractJpaRepository<Stage, Long> implements
    StageRepository {

  @Inject
  public StageRepositoryImpl(EntityManager entityManager) {
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
