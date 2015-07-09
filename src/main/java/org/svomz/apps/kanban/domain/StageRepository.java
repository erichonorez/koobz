package org.svomz.apps.kanban.domain;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.svomz.apps.kanban.infrastructure.domain.KanbanRepository;

import javax.persistence.NamedQuery;

public interface StageRepository extends KanbanRepository<Stage, Long> {

  @Query("SELECT s FROM Stage s WHERE s.id = :stageId AND s.board.id = :boardId")
  Stage findByBoardIdAndStageId(@Param("boardId") long boardId, @Param("stageId") long stageId);
}
