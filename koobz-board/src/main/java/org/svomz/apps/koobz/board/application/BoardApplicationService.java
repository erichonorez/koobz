package org.svomz.apps.koobz.board.application;

import com.google.common.base.Preconditions;

import org.springframework.stereotype.Service;
import org.svomz.apps.koobz.board.domain.model.Board;
import org.svomz.apps.koobz.board.domain.model.BoardRepository;
import org.svomz.apps.koobz.board.domain.model.Stage;
import org.svomz.apps.koobz.board.domain.model.StageNotInProcessException;
import org.svomz.apps.koobz.board.domain.model.WorkItem;
import org.svomz.apps.koobz.board.domain.model.WorkItemNotInProcessException;
import org.svomz.apps.koobz.board.domain.model.WorkItemNotInStageException;


import java.util.Optional;

import javax.inject.Inject;
import javax.transaction.Transactional;

@Service
public class BoardApplicationService {

  private final BoardRepository boardRepository;

  @Inject
  public BoardApplicationService(final BoardRepository aBoardRepository) {
    this.boardRepository = aBoardRepository;
  }

  @Transactional
  public Board createBoard(final String aBoardName) {
    Preconditions.checkNotNull(aBoardName);

    Board board = new Board(
      this.boardRepository().nextIdentity(),
      aBoardName
    );
    this.boardRepository().save(board);
    return board;
  }

  @Transactional
  public Stage createStage(final String boardId, final String title) throws BoardNotFoundException {
    Preconditions.checkNotNull(boardId);
    Preconditions.checkNotNull(title);

    Board board = this.boardOfId(boardId);
    Stage stage = new Stage(title);

    stage.addToBoard(board);

    return stage;
  }

  @Transactional
  public WorkItem createWorkItem(final String boardId, final String stageId, final String aWorkItemTitle,
    final String aWorkItemDescription) throws BoardNotFoundException, StageNotInProcessException {

    Board board = this.boardOfId(boardId);

    Optional<Stage> stage = board.stageOfId(stageId);
    if (!stage.isPresent()) {
      throw new StageNotInProcessException();
    }

    WorkItem workItem = new WorkItem(aWorkItemTitle, aWorkItemDescription);
    board.addWorkItem(workItem, stage.get());
    return workItem;
  }

  @Transactional
  public void moveWorkItemToStage(final String boardId, final String aWorkItemId, final String aStageId)
    throws BoardNotFoundException, WorkItemNotInProcessException, StageNotInProcessException {
    Preconditions.checkNotNull(boardId);
    Preconditions.checkNotNull(aWorkItemId);

    Board board = this.boardOfId(boardId);
    Optional<WorkItem> optionalWorkItem = board.workItemOfId(aWorkItemId);
    if (!optionalWorkItem.isPresent()) {
      throw new WorkItemNotInProcessException();
    }

    Optional<Stage> optionalStage = board.stageOfId(aStageId);
    if (!optionalStage.isPresent()) {
      throw new StageNotInProcessException();
    }

    board.moveWorkItemToStage(optionalWorkItem.get(), optionalStage.get());
  }

  @Transactional
  public void changeWorkItemOrder(final String boardId, final String workItemId, int newOrder)
    throws BoardNotFoundException, WorkItemNotInProcessException, WorkItemNotInStageException {
    Preconditions.checkNotNull(boardId);
    Preconditions.checkNotNull(workItemId);

    Board board = this.boardOfId(boardId);
    Optional<WorkItem> optionalWorkItem = board.workItemOfId(workItemId);
    if (!optionalWorkItem.isPresent()) {
      throw new WorkItemNotInProcessException();
    }

    board.reoderWorkItem(optionalWorkItem.get(), newOrder);
  }

  private Board boardOfId(String boardId) throws BoardNotFoundException {
    Board board = this.boardRepository().findOne(boardId);
    if (board == null) {
      throw new BoardNotFoundException(boardId);
    }
    return board;
  }

  private BoardRepository boardRepository() {
    return boardRepository;
  }
}
