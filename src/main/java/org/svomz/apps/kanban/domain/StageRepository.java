package org.svomz.apps.kanban.domain;

import org.svomz.apps.kanban.infrastructure.domain.EntityNotFoundException;
import org.svomz.apps.kanban.infrastructure.domain.Repository;

public interface StageRepository extends Repository<Stage, Long> {

  /**
   * Find a stage in a board.
   * 
   * @param boardId
   * @param stageId
   * @return 
   * @throws EntityNotFoundException
   */
  Stage find(long boardId, long stageId) throws EntityNotFoundException;

}
