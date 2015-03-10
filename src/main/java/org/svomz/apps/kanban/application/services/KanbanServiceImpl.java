package org.svomz.apps.kanban.application.services;

import java.util.ArrayList;
import java.util.List;

import org.svomz.apps.kanban.domain.entities.Board;
import org.svomz.apps.kanban.domain.entities.Stage;
import org.svomz.apps.kanban.domain.entities.WorkItem;
import org.svomz.apps.kanban.domain.exceptions.StageNotEmptyException;
import org.svomz.apps.kanban.domain.exceptions.StageNotInProcessException;
import org.svomz.apps.kanban.domain.exceptions.WorkItemNotOnBoardException;
import org.svomz.apps.kanban.domain.repositories.BoardRepository;
import org.svomz.apps.kanban.domain.repositories.StageRepository;
import org.svomz.apps.kanban.domain.repositories.WorkItemRepository;
import org.svomz.commons.persistence.EntityNotFoundException;

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
  public Board get(final long boardId) throws EntityNotFoundException {
    return this.boardRepository.find(boardId);
  }

  @Override
  @Transactional
  public Board createBoard(final String name) {
    Preconditions.checkNotNull(name);

    Board board = new Board(name);
    return this.boardRepository.create(board);
  }

  @Override
  @Transactional
  public Board updateBoard(final long boardId, final String name) throws EntityNotFoundException {
    Preconditions.checkNotNull(name);

    Board persistedBoard = this.boardRepository.find(boardId);
    persistedBoard.setName(name);
    return persistedBoard;
  }

  @Override
  @Transactional
  public void deleteBoard(final long boardId) throws EntityNotFoundException {
    Board persistedBoard = this.boardRepository.find(boardId);
    this.boardRepository.delete(persistedBoard);
  }

  @Override
  @Transactional
  public Stage addStageToBoard(final long boardId, final String name)
      throws EntityNotFoundException {
    Preconditions.checkNotNull(name);

    Board board = this.boardRepository.find(boardId);
    Stage stage = new Stage(name);
    board.addStage(stage);
    this.boardRepository.create(board);
    return stage;
  }

  @Override
  @Transactional
  public void removeStageFromBoard(final long boardId, final long stageId)
      throws EntityNotFoundException, StageNotInProcessException, StageNotEmptyException {
    Board board = this.boardRepository.find(boardId);
    Stage stage = this.stageRepository.find(stageId);
    board.removeStage(stage);
  }

  @Override
  @Transactional
  public Stage updateStage(final long stageId, final String name) throws EntityNotFoundException {
    Preconditions.checkNotNull(name);

    Stage persistedStage = this.stageRepository.find(stageId);
    persistedStage.setName(name);
    return persistedStage;
  }

  @Override
  public List<Stage> getStages(final long boardId) throws EntityNotFoundException {
    Board board = this.boardRepository.find(boardId);
    return new ArrayList<>(board.getStages());
  }

  @Override
  public List<WorkItem> getWorkItems(final long boardId) throws EntityNotFoundException {
    Board board = this.boardRepository.find(boardId);
    return new ArrayList<>(board.getWorkItems());
  }

  @Override
  @Transactional
  public WorkItem addWorkItemToBoard(final long boardId, final long stageId, final String text)
      throws EntityNotFoundException, StageNotInProcessException {
    Preconditions.checkNotNull(text);

    Board board = this.boardRepository.find(boardId);
    Stage stage = this.stageRepository.find(stageId);
    WorkItem workItem = new WorkItem(text);
    board.addWorkItem(workItem, stage);
    return workItem;
  }

  @Override
  @Transactional
  public void removeWorkItemFromBoard(final long boardId, final long workItemId)
      throws EntityNotFoundException, WorkItemNotOnBoardException {
    Board board = this.boardRepository.find(boardId);
    WorkItem workItem = this.workItemRepository.find(workItemId);
    board.removeWorkItem(workItem);
  }

  @Override
  public WorkItem updateWorkItem(final long boardId, final long workItemId, final String text,
      final long stageId) throws EntityNotFoundException, WorkItemNotOnBoardException,
      StageNotInProcessException {
    Preconditions.checkNotNull(text);

    WorkItem persistedWorkItem = this.workItemRepository.find(workItemId);
    if (!persistedWorkItem.getText().equals(text)) {
      persistedWorkItem.setText(text);
    }

    Stage stage = this.stageRepository.find(boardId, stageId);
    if (stage.getId() != persistedWorkItem.getStage().getId()) {
      Board board = this.boardRepository.find(boardId);
      board.moveWorkItem(persistedWorkItem, stage);
    }

    return persistedWorkItem;
  }

}
