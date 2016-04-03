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
  private final BoardIdentityService boardIdentityService;

  @Inject
  public BoardApplicationService(final BoardRepository aBoardRepository,
    BoardIdentityService aBoardIdentityService) {
    this.boardRepository = Preconditions.checkNotNull(aBoardRepository);
    this.boardIdentityService = Preconditions.checkNotNull(aBoardIdentityService);
  }

  @Transactional
  public Board createBoard(final String aBoardName) {
    Preconditions.checkNotNull(aBoardName);

    Board board = new Board(
      this.boardIdentityService().nextBoardIdentity(),
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
  public Stage addStageToBoard(final String aBoardId, final String aStageTitle) throws BoardNotFoundException {
    Preconditions.checkNotNull(aBoardId);
    Preconditions.checkNotNull(aStageTitle);

    Board board = this.boardOfId(aBoardId);
    Stage stage = board.addStageToBoard(
      this.boardIdentityService.nextStageIdentity(),
      aStageTitle
    );

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

    Stage stage = optionalStage.get();
    stage.setName((String) newStageName);
  }

  @Transactional
  public void removeStageFromBoard(final String boardId, final String aStageId)
    throws BoardNotFoundException, StageNotInProcessException, StageNotEmptyException {
    Preconditions.checkNotNull(boardId);
    Preconditions.checkNotNull(aStageId);

    Board board = this.boardOfId(boardId);
    board.removeStageWithId(aStageId);
  }

  @Transactional
  public WorkItem createWorkItem(final String boardId, final String stageId, final String aWorkItemTitle,
    final String aWorkItemDescription) throws BoardNotFoundException, StageNotInProcessException {
    Preconditions.checkNotNull(boardId);
    Preconditions.checkNotNull(stageId);
    Preconditions.checkNotNull(aWorkItemDescription);

    Board board = this.boardOfId(boardId);
    return board.addWorkItemToStage(
      stageId,
      this.boardIdentityService().nextWorkItemIdentity(),
      aWorkItemTitle,
      aWorkItemDescription
    );
  }

  @Transactional
  public void changeWorkItemInformation(final String boardId,final String workItemId,
    final String newWorkItemTitle, final String newWorkItemDescription)
    throws BoardNotFoundException, WorkItemNotInProcessException {
    Preconditions.checkNotNull(boardId);
    Preconditions.checkNotNull(workItemId);
    Preconditions.checkNotNull(newWorkItemTitle);
    Preconditions.checkNotNull(newWorkItemDescription);

    Board board = this.boardOfId(boardId);
    Optional<WorkItem> optionalWorkItem = board.workItemOfId(workItemId);
    if (!optionalWorkItem.isPresent()) {
      throw new WorkItemNotInProcessException();
    }

    WorkItem workItem = optionalWorkItem.get();
    workItem.setTitle(newWorkItemTitle);
    workItem.setDescription(newWorkItemDescription);
  }

  @Transactional
  public void removeWorkItemFromBoard(final String boardId, final String workItemId)
    throws BoardNotFoundException, WorkItemNotInProcessException {
    Preconditions.checkNotNull(boardId);
    Preconditions.checkNotNull(workItemId);

    Board board = this.boardOfId(boardId);
    board.removeWorkItemWithId(workItemId);
  }

  @Transactional
  public void moveWorkItemToStage(final String boardId, final String aWorkItemId, final String aStageId)
    throws BoardNotFoundException, WorkItemNotInProcessException, StageNotInProcessException {
    Preconditions.checkNotNull(boardId);
    Preconditions.checkNotNull(aWorkItemId);

    Board board = this.boardOfId(boardId);
    board.moveWorkItemWithIdToStageWithId(aWorkItemId, aStageId);
  }

  @Transactional
  public void moveWorkItemToPosition(final String boardId, final String workItemId, int newPosition)
    throws BoardNotFoundException, WorkItemNotInProcessException, WorkItemNotInStageException {
    Preconditions.checkNotNull(boardId);
    Preconditions.checkNotNull(workItemId);

    Board board = this.boardOfId(boardId);
    board.moveWorkItemWithIdToPosition(workItemId, newPosition);
  }

  @Transactional
  public void archiveWorkItem(final String boardId, final String workItemId)
    throws BoardNotFoundException, WorkItemNotInProcessException {
    Preconditions.checkNotNull(boardId);
    Preconditions.checkNotNull(workItemId);

    Board board = this.boardOfId(boardId);
    board.archiveWorkItemWithId(workItemId);
  }

  @Transactional
  public void sendWorkItemBackToBoard(final String boardId, final String workItemId)
    throws BoardNotFoundException, WorkItemNotArchivedException {
    Preconditions.checkNotNull(boardId);
    Preconditions.checkNotNull(workItemId);

    Board board = this.boardOfId(boardId);
    board.sendBackToBoardWorkItemWithId(workItemId);
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

  private BoardIdentityService boardIdentityService() {
    return this.boardIdentityService;
  }

}
