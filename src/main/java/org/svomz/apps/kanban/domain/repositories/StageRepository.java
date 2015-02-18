package org.svomz.apps.kanban.domain.repositories;

import org.svomz.apps.kanban.domain.entities.Stage;
import org.svomz.commons.persistence.EntityNotFoundException;
import org.svomz.commons.persistence.Repository;

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
