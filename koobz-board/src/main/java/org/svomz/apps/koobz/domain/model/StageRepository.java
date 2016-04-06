package org.svomz.apps.koobz.domain.model;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.svomz.apps.koobz.infrastructure.domain.KanbanRepository;

public interface StageRepository extends KanbanRepository<Stage, String> {

  @Query("SELECT s FROM Stage s WHERE s.id = :stageId AND s.workflow.id = :workflowId")
  Stage findByBoardIdAndStageId(@Param("workflowId") String workflowId, @Param("stageId") String stageId);
}
