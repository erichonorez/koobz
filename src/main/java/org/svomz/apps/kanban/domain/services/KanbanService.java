package org.svomz.apps.kanban.domain.services;

import java.util.List;

import org.svomz.apps.kanban.application.resources.CreateOrUpdateWorkItemRequest;
import org.svomz.apps.kanban.domain.entities.Board;
import org.svomz.apps.kanban.domain.entities.Stage;
import org.svomz.apps.kanban.domain.entities.StageNotEmptyException;
import org.svomz.apps.kanban.domain.entities.StageNotInProcessException;
import org.svomz.apps.kanban.domain.entities.WorkItem;
import org.svomz.apps.kanban.domain.entities.WorkItemNotOnBoardException;
import org.svomz.commons.infrastructure.persistence.EntityNotFoundException;

public interface KanbanService {
  
  List<Board> getAll();
  
  Board get(long boardId) throws EntityNotFoundException;

  Board create(Board board);
  
  Board update(Board board) throws EntityNotFoundException;
  
  void delete(long boardId) throws EntityNotFoundException;

  Stage addStageToBoard(long boardId, Stage stage) throws EntityNotFoundException;

  void removeStageFromBoard(long boardId, long stageId) throws EntityNotFoundException, StageNotInProcessException, StageNotEmptyException;

  Stage updateStage(Stage stage) throws EntityNotFoundException;

  List<Stage> getStages(long boardId) throws EntityNotFoundException;

  List<WorkItem> getWorkItems(long boardId) throws EntityNotFoundException;

  WorkItem addWorkItemToBoard(long boardId, CreateOrUpdateWorkItemRequest request) throws EntityNotFoundException, StageNotInProcessException;

  void removeWorkItemFromBoard(long boardId, long workItemId) throws EntityNotFoundException, WorkItemNotOnBoardException;

  WorkItem updateWorkItem(long boardId, long workItemId, CreateOrUpdateWorkItemRequest updateWorkItemRequest)
      throws EntityNotFoundException, WorkItemNotOnBoardException, StageNotInProcessException;;
  
}
