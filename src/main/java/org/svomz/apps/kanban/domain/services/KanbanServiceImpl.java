package org.svomz.apps.kanban.domain.services;

import java.util.ArrayList;
import java.util.List;

import org.svomz.apps.kanban.application.resources.CreateOrUpdateWorkItemRequest;
import org.svomz.apps.kanban.domain.entities.Board;
import org.svomz.apps.kanban.domain.entities.Stage;
import org.svomz.apps.kanban.domain.entities.StageNotEmptyException;
import org.svomz.apps.kanban.domain.entities.StageNotInProcessException;
import org.svomz.apps.kanban.domain.entities.WorkItem;
import org.svomz.apps.kanban.domain.entities.WorkItemNotOnBoardException;
import org.svomz.apps.kanban.domain.repositories.BoardRepository;
import org.svomz.apps.kanban.domain.repositories.StageRepository;
import org.svomz.apps.kanban.domain.repositories.WorkItemRepository;
import org.svomz.commons.infrastructure.persistence.EntityNotFoundException;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

public class KanbanServiceImpl implements KanbanService {

  private final BoardRepository boardRepository;
  private final StageRepository stageRepository;
  private final WorkItemRepository workItemRepository;

  @Inject
  public KanbanServiceImpl(final BoardRepository boardRepository,
      final StageRepository stageRepository, final WorkItemRepository workItemRepository) {
    Preconditions.checkNotNull(boardRepository);
    Preconditions.checkNotNull(stageRepository);
    Preconditions.checkNotNull(workItemRepository);

    this.boardRepository = boardRepository;
    this.stageRepository = stageRepository;
    this.workItemRepository = workItemRepository;
  }

  @Override
  public List<Board> getAll() {
    return this.boardRepository.findAll();
  }

  @Override
  public Board get(long boardId) throws EntityNotFoundException {
    return this.boardRepository.find(boardId);
  }

  @Override
  @Transactional
  public Board create(Board board) {
    Preconditions.checkArgument(board != null, "Supplied board object can't be null.");
    Preconditions.checkArgument(board.getId() == 0, "Supplied board can't already be an entity.");

    return this.boardRepository.create(board);
  }

  @Override
  @Transactional
  public Board update(Board board) throws EntityNotFoundException {
    Preconditions.checkArgument(board != null, "Supplied board object can't be null.");
    Preconditions.checkArgument(board.getId() > 0, "Supplied board must have an identifier.");

    Board persistedBoard = this.boardRepository.find(board.getId());
    persistedBoard.setName(board.getName());
    return persistedBoard;
  }

  @Override
  @Transactional
  public void delete(long boardId) throws EntityNotFoundException {
    Board persistedBoard = this.boardRepository.find(boardId);
    this.boardRepository.delete(persistedBoard);
  }

  @Override
  @Transactional
  public Stage addStageToBoard(long boardId, Stage stage) throws EntityNotFoundException {
    Preconditions.checkNotNull(stage, "Supplied stage cannot be null.");

    Board board = this.boardRepository.find(boardId);
    board.addStage(stage);
    this.boardRepository.create(board);
    return stage;
  }

  @Override
  @Transactional
  public void removeStageFromBoard(long boardId, long stageId) throws EntityNotFoundException,
      StageNotInProcessException, StageNotEmptyException {
    Board board = this.boardRepository.find(boardId);
    Stage stage = this.stageRepository.find(stageId);
    board.removeStage(stage);
  }

  @Override
  @Transactional
  public Stage updateStage(Stage stage) throws EntityNotFoundException {
    Preconditions.checkNotNull(stage, "Supplied stage cannot be null.");

    Stage persistedStage = this.stageRepository.find(stage.getId());
    persistedStage.setName(stage.getName());
    return persistedStage;
  }

  @Override
  public List<Stage> getStages(long boardId) throws EntityNotFoundException {
    Board board = this.boardRepository.find(boardId);
    return new ArrayList<>(board.getStages());
  }

  @Override
  public List<WorkItem> getWorkItems(long boardId) throws EntityNotFoundException {
    Board board = this.boardRepository.find(boardId);
    return new ArrayList<>(board.getWorkItems());
  }

  @Override
  @Transactional
  public WorkItem addWorkItemToBoard(long boardId, CreateOrUpdateWorkItemRequest request) throws EntityNotFoundException, StageNotInProcessException {
    Preconditions.checkNotNull(request);
    
    Board board = this.boardRepository.find(boardId);
    Stage stage = this.stageRepository.find(request.getStageId());
    WorkItem workItem = new WorkItem(request.getText());
    board.addWorkItem(workItem, stage);
    return workItem;
  }

  @Override
  @Transactional
  public void removeWorkItemFromBoard(long boardId, long workItemId) throws EntityNotFoundException, WorkItemNotOnBoardException {
    Board board = this.boardRepository.find(boardId);
    WorkItem workItem = this.workItemRepository.find(workItemId);
    board.removeWorkItem(workItem);
  }

  @Override
  public WorkItem updateWorkItem(long boardId, long workItemId, CreateOrUpdateWorkItemRequest updateWorkItemRequest)
      throws EntityNotFoundException, WorkItemNotOnBoardException, StageNotInProcessException {
    Preconditions.checkNotNull(updateWorkItemRequest);
    
    WorkItem persistedWorkItem = this.workItemRepository.find(workItemId);
    if (updateWorkItemRequest.getText().compareTo(persistedWorkItem.getText()) != 0) {
      persistedWorkItem.setText(updateWorkItemRequest.getText());
    }
    
    Stage stage = this.stageRepository.find(updateWorkItemRequest.getStageId());
    if (stage.getId() != persistedWorkItem.getStage().getId()) {
        Board board = this.boardRepository.find(boardId);
        board.moveWorkItem(persistedWorkItem, stage);
    }
    
    return persistedWorkItem;
  }

}
