package org.svomz.apps.koobz.board.domain.model;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.svomz.apps.koobz.board.infrastructure.domain.KanbanRepository;

public interface StageRepository extends KanbanRepository<Stage, String> {

  @Query("SELECT s FROM Stage s WHERE s.id = :stageId AND s.board.id = :boardId")
  Stage findByBoardIdAndStageId(@Param("boardId") String boardId, @Param("stageId") String stageId);
}
