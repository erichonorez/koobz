package org.svomz.apps.koobz.board.application;

import com.google.common.base.Preconditions;

import org.springframework.stereotype.Service;
import org.svomz.apps.koobz.board.domain.model.Board;
import org.svomz.apps.koobz.board.domain.model.BoardRepository;
import org.svomz.apps.koobz.board.domain.model.Stage;
import org.svomz.apps.koobz.board.domain.model.StageNotEmptyException;
import org.svomz.apps.koobz.board.domain.model.StageNotInProcessException;
import org.svomz.apps.koobz.board.domain.model.WorkItem;
import org.svomz.apps.koobz.board.domain.model.WorkItemNotArchivedException;
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
  public void changeBoardName(final String boardId, final String newBoardName)
    throws BoardNotFoundException {
    Preconditions.checkNotNull(boardId);
    Preconditions.checkNotNull(newBoardName);

    Board board = this.boardOfId(boardId);
    board.setName(newBoardName);
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
  public void changeStageName(final String boardId, final String aStageId, final String newStageName)
    throws BoardNotFoundException, StageNotInProcessException {
    Preconditions.checkNotNull(boardId);
    Preconditions.checkNotNull(aStageId);
    Preconditions.checkNotNull(newStageName);

    Board board = this.boardOfId(boardId);
    Optional<Stage> optionalStage = board.stageOfId(aStageId);
    if (!optionalStage.isPresent()) {
      throw new StageNotInProcessException();
    }

    optionalStage.get().setName(newStageName);
  }

  @Transactional
  public void deleteStage(final String boardId, final String aStageId)
    throws BoardNotFoundException, StageNotInProcessException, StageNotEmptyException {
    Preconditions.checkNotNull(boardId);
    Preconditions.checkNotNull(aStageId);

    Board board = this.boardOfId(boardId);
    Optional<Stage> optionalStage = board.stageOfId(aStageId);

    if (!optionalStage.isPresent()) {
      throw new StageNotInProcessException();
    }

    board.removeStage(optionalStage.get());
  }

  @Transactional
  public WorkItem createWorkItem(final String boardId, final String stageId, final String aWorkItemTitle,
    final String aWorkItemDescription) throws BoardNotFoundException, StageNotInProcessException {
    Preconditions.checkNotNull(boardId);
    Preconditions.checkNotNull(stageId);
    Preconditions.checkNotNull(aWorkItemDescription);

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
  public void changeWorkItemPosition(final String boardId, final String workItemId, int newPosition)
    throws BoardNotFoundException, WorkItemNotInProcessException, WorkItemNotInStageException {
    Preconditions.checkNotNull(boardId);
    Preconditions.checkNotNull(workItemId);

    Board board = this.boardOfId(boardId);
    Optional<WorkItem> optionalWorkItem = board.workItemOfId(workItemId);
    if (!optionalWorkItem.isPresent()) {
      throw new WorkItemNotInProcessException();
    }

    board.putWorkItemAtPosition(optionalWorkItem.get(), newPosition);
  }

  @Transactional
  public void archiveWorkItem(final String boardId, final String workItemId)
    throws BoardNotFoundException, WorkItemNotInProcessException {
    Preconditions.checkNotNull(boardId);
    Preconditions.checkNotNull(workItemId);

    Board board = this.boardOfId(boardId);
    Optional<WorkItem> optionalWorkItem = board.workItemOfId(workItemId);
    if (!optionalWorkItem.isPresent()) {
      throw new WorkItemNotInProcessException();
    }

    board.archive(optionalWorkItem.get());
  }

  @Transactional
  public void sendWorkItemBackToBoard(final String boardId, final String workItemId)
    throws BoardNotFoundException, WorkItemNotArchivedException {
    Preconditions.checkNotNull(boardId);
    Preconditions.checkNotNull(workItemId);

    Board board = this.boardOfId(boardId);
    board.unarchive(workItemId);
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
