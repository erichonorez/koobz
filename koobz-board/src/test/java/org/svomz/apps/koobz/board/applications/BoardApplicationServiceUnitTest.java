package org.svomz.apps.koobz.board.applications;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.svomz.apps.koobz.board.application.BoardApplicationService;
import org.svomz.apps.koobz.board.application.BoardIdentityService;
import org.svomz.apps.koobz.board.application.BoardNotFoundException;
import org.svomz.apps.koobz.board.domain.model.Board;
import org.svomz.apps.koobz.board.domain.model.BoardRepository;
import org.svomz.apps.koobz.board.domain.model.Stage;
import org.svomz.apps.koobz.board.domain.model.StageNotEmptyException;
import org.svomz.apps.koobz.board.domain.model.StageNotInProcessException;
import org.svomz.apps.koobz.board.domain.model.WorkItem;
import org.svomz.apps.koobz.board.domain.model.WorkItemNotArchivedException;
import org.svomz.apps.koobz.board.domain.model.WorkItemNotInProcessException;
import org.svomz.apps.koobz.board.domain.model.WorkItemNotInStageException;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(Suite.class)
@Suite.SuiteClasses({
  BoardApplicationServiceUnitTest.CreateBoard.class,
  BoardApplicationServiceUnitTest.CreateStage.class,
  BoardApplicationServiceUnitTest.CreateWorkItem.class,
  BoardApplicationServiceUnitTest.MoveWorkItemToStage.class,
  BoardApplicationServiceUnitTest.MoveWorkItemToPosition.class,
  BoardApplicationServiceUnitTest.ArchiveWorkItem.class,
  BoardApplicationServiceUnitTest.ChangeBoardName.class,
  BoardApplicationServiceUnitTest.ChangeStageName.class,
  BoardApplicationServiceUnitTest.DeleteStage.class,
  BoardApplicationServiceUnitTest.ChangeWorkItemInformation.class,
  BoardApplicationServiceUnitTest.DeleteWorkItem.class
})
public class BoardApplicationServiceUnitTest {

  public static class CreateBoard {

    @Test
    public void itShouldCreateANewBoardSuccessfully() {
      BoardRepository boardRepository = mock(BoardRepository.class);
      BoardIdentityService boardIdentityService = mock(BoardIdentityService.class);
      when(boardIdentityService.nextBoardIdentity()).thenReturn(UUID.randomUUID().toString());

      BoardApplicationService boardService = new BoardApplicationService(boardRepository,
        boardIdentityService);

      String aBoardName = "A name";
      Board board = boardService.createBoard(aBoardName);

      assertThat(board).isNotNull();
      assertThat(board.getName()).isEqualTo(aBoardName);
      assertThat(board.getWorkItems()).isEmpty();
      assertThat(board.getStages()).isEmpty();
      assertThat(board.getId()).isNotNull();
    }

  }

  public static class ChangeBoardName {

    @Test
    public void itShouldSuccessfullyChangeTheNameOfABoard() throws BoardNotFoundException {
      // Given a board with id "35a45cd4-f81f-11e5-9ce9-5e5517507c66" and name ",World! Hello"
      String boardId = "35a45cd4-f81f-11e5-9ce9-5e5517507c66";
      String boardName = ",World! Hello";

      BoardRepository boardRepository = mock(BoardRepository.class);
      Board board = new Board(boardId, boardName);
      when(boardRepository.findOne(boardId)).thenReturn(board);

      // When I update the board name with "Hello, World!"
      String newBoardName = "Hello, World!";

      BoardIdentityService boardIdentityService = mock(BoardIdentityService.class);
      BoardApplicationService boardApplicationService = new BoardApplicationService(boardRepository,
        boardIdentityService);

      boardApplicationService.changeBoardName(boardId, newBoardName);

      // Then the name is updated
      assertThat(board.getName()).isEqualTo(newBoardName);
    }

  }

  public static class CreateStage {

    @Test
    public void itShouldSuccessfullyCreateANewStage() throws BoardNotFoundException {
      // Given a board with id "35a45cd4-f81f-11e5-9ce9-5e5517507c66"
      String boardId = "35a45cd4-f81f-11e5-9ce9-5e5517507c66";

      BoardRepository boardRepository = mock(BoardRepository.class);
      Board board = new Board(boardId, "a board");
      when(boardRepository.findOne(boardId)).thenReturn(board);

      // When I create a new stage on board with id "35a45cd4-f81f-11e5-9ce9-5e5517507c66"
      // And with "to do" as title
      String title = "to do";

      BoardIdentityService boardIdentityService = mock(BoardIdentityService.class);
      when(boardIdentityService.nextStageIdentity()).thenReturn(UUID.randomUUID().toString());

      BoardApplicationService boardApplicationService = new BoardApplicationService(boardRepository,
        boardIdentityService);

      Stage stage = boardApplicationService.createStage(boardId, title);

      // Then the board has a new stage

      assertThat(board.getStages()).contains(stage);
    }

    @Test(expected = BoardNotFoundException.class)
    public void itShouldThrowBoardNotFoundExceptionIfBoardDoesNotExist()
      throws BoardNotFoundException {
      // Given the board with id "35a45cd4-f81f-11e5-9ce9-5e5517507c66" does not exist
      String boardId = "35a45cd4-f81f-11e5-9ce9-5e5517507c66";

      BoardRepository boardRepository = mock(BoardRepository.class);
      when(boardRepository.findOne(boardId)).thenReturn(null);

      BoardIdentityService boardIdentityService = mock(BoardIdentityService.class);

      BoardApplicationService boardApplicationService = new BoardApplicationService(boardRepository,
        boardIdentityService);

      // When I create a new stage on board with id "35a45cd4-f81f-11e5-9ce9-5e5517507c66"
      // And with "to do" as title
      String title = "to do";

      Stage stage = boardApplicationService.createStage(boardId, title);

      // Then I get a BoardNotFoundException
    }

  }

  public static class ChangeStageName {

    @Test
    public void itShouldSuccessfullyChangeTheNameOfAStage()
      throws StageNotInProcessException, BoardNotFoundException {
      // Given a board with id "35a45cd4-f81f-11e5-9ce9-5e5517507c66"
      String boardId = "35a45cd4-f81f-11e5-9ce9-5e5517507c66";
      Board board = new Board(boardId, "a board");
      // And with a stage having id "36a45cd4-f81f-11e5-9ce9-5e5517507c67" and name "do To"
      String aStageId = "36a45cd4-f81f-11e5-9ce9-5e5517507c67";
      String aStageName = "do To";

      Stage stage = board.addStageToBoard(
        aStageId,
        aStageName
      );

      BoardRepository boardRepository = mock(BoardRepository.class);
      when(boardRepository.findOne(boardId)).thenReturn(board);

      // When I update the board name with "To do"
      BoardIdentityService boardIdentityService = mock(BoardIdentityService.class);
      BoardApplicationService boardApplicationService = new BoardApplicationService(boardRepository,
        boardIdentityService);

      String newStageName = "To do";
      boardApplicationService.changeStageName(boardId, aStageId, newStageName);

      // Then the stage name is equal to "To do"
      assertThat(stage.getName()).isEqualTo(newStageName);
    }

  }

  public static class DeleteStage {

    @Test
    public void itShouldSuccessfullyDeleteAnEmptyStage()
      throws BoardNotFoundException, StageNotEmptyException, StageNotInProcessException {
      // Given a board with id "35a45cd4-f81f-11e5-9ce9-5e5517507c66"
      String boardId = "35a45cd4-f81f-11e5-9ce9-5e5517507c66";
      Board board = new Board(boardId, "a board");
      // And with a stage having id "36a45cd4-f81f-11e5-9ce9-5e5517507c67"
      String aStageId = "36a45cd4-f81f-11e5-9ce9-5e5517507c67";
      String aStageName = "do To";

      Stage stage = board.addStageToBoard(aStageId, aStageName);

      BoardRepository boardRepository = mock(BoardRepository.class);
      when(boardRepository.findOne(boardId)).thenReturn(board);

      // When I delete the stage
      BoardIdentityService boardIdentityService = mock(BoardIdentityService.class);
      BoardApplicationService boardApplicationService = new BoardApplicationService(boardRepository,
        boardIdentityService);

      String newStageName = "To do";
      boardApplicationService.deleteStage(boardId, aStageId);

      // Then board does not contains the stage any more
      assertThat(board.getStages()).doesNotContain(stage);
    }

  }

  public static class CreateWorkItem {

    @Test
    public void itShouldSuccessfullyCreateAWorkItem()
      throws StageNotInProcessException, BoardNotFoundException {
      // Given a board with id "35a45cd4-f81f-11e5-9ce9-5e5517507c66" and title "a board"
      String boardId = "35a45cd4-f81f-11e5-9ce9-5e5517507c66";
      String aBoardName = "a board";
      // And having a Stage with id "ac329010-f837-11e5-9ce9-5e5517507c66" and name "to do"
      String aStageId = "35a45cd4-f81f-11e5-9ce9-5e5517507c66";
      String aStageName = "to do";

      Board board = new Board(boardId, aBoardName);
      Stage stage = board.addStageToBoard(aStageId, aStageName);

      BoardRepository boardRepository = mock(BoardRepository.class);
      when(boardRepository.findOne(boardId)).thenReturn(board);

      // When user adds a Work Item with title "Drink coffee" to the stage with id "ac329010-f837-11e5-9ce9-5e5517507c66"
      String aWorkItemTitle = "Drink coffee";
      String aWorkItemDescription = "At Starbuck";

      BoardIdentityService boardIdentityService = mock(BoardIdentityService.class);
      when(boardIdentityService.nextWorkItemIdentity()).thenReturn(UUID.randomUUID().toString());

      BoardApplicationService boardApplicationService = new BoardApplicationService(boardRepository,
        boardIdentityService);

      WorkItem workItem = boardApplicationService.createWorkItem(
        boardId,
        aStageId,
        aWorkItemTitle,
        aWorkItemDescription
      );

      // Then the board contains the work item
      assertThat(board.getWorkItems()).contains(workItem);
      assertThat(workItem.getTitle()).isEqualTo(aWorkItemTitle);
      assertThat(workItem.getDescription()).isEqualTo(aWorkItemDescription);
    }

    @Test(expected = BoardNotFoundException.class)
    public void itShouldThrowBoardNotFoundExceptionIfBoardDoesNotExist()
      throws StageNotInProcessException, BoardNotFoundException {
      // Given the board with id "35a45cd4-f81f-11e5-9ce9-5e5517507c66" does not exist
      String boardId = "35a45cd4-f81f-11e5-9ce9-5e5517507c66";
      String stageId = "35a45cd4-f81f-11e5-9ce9-5e5517507c66";

      BoardRepository boardRepository = mock(BoardRepository.class);
      when(boardRepository.findOne(boardId)).thenReturn(null);

      BoardIdentityService boardIdentityService = mock(BoardIdentityService.class);
      BoardApplicationService boardApplicationService = new BoardApplicationService(boardRepository,
        boardIdentityService);

      // When user adds a Work Item with title "Drink coffee" to the stage with id "ac329010-f837-11e5-9ce9-5e5517507c66"
      String aWorkItemTitle = "Drink coffee";
      String aWorkItemDescription = "At Starbuck";

      WorkItem workItem = boardApplicationService.createWorkItem(
        boardId,
        stageId,
        aWorkItemTitle,
        aWorkItemDescription
      );

      // Then I get a BoardNotFoundException
    }

    @Test(expected = StageNotInProcessException.class)
    public void itShouldThrowStageNotInProcessExceptionIfStageDoesNotExist()
      throws StageNotInProcessException, BoardNotFoundException {
      // Given a board with id "35a45cd4-f81f-11e5-9ce9-5e5517507c66" and title "a board"
      String boardId = "35a45cd4-f81f-11e5-9ce9-5e5517507c66";
      String aBoardName = "a board";
      // And not having a Stage with id "ac329010-f837-11e5-9ce9-5e5517507c66"
      String stageId = "35a45cd4-f81f-11e5-9ce9-5e5517507c66";

      Board board = new Board(boardId, aBoardName);

      BoardRepository boardRepository = mock(BoardRepository.class);
      when(boardRepository.findOne(boardId)).thenReturn(board);

      // When user adds a Work Item with title "Drink coffee" to the stage with id "ac329010-f837-11e5-9ce9-5e5517507c66"
      String aWorkItemTitle = "Drink coffee";
      String aWorkItemDescription = "At Starbuck";

      BoardIdentityService boardIdentityService = mock(BoardIdentityService.class);
      when(boardIdentityService.nextWorkItemIdentity()).thenReturn(UUID.randomUUID().toString());
      BoardApplicationService boardApplicationService = new BoardApplicationService(boardRepository,
        boardIdentityService);

      WorkItem workItem = boardApplicationService.createWorkItem(
        boardId,
        stageId,
        aWorkItemTitle,
        aWorkItemDescription
      );

      // Then I get a StageNotInProcessException
    }

  }

  public static class ChangeWorkItemInformation {

    @Test
    public void itShouldSuccessfullyUpdateWorkItemTitleAndDescription()
      throws StageNotInProcessException, WorkItemNotInProcessException, BoardNotFoundException {
      // Given a board with a stage having a work item A
      String boardId = "35a45cd4-f81f-11e5-9ce9-5e5517507c66";
      String aBoardName = "a board";
      Board board = new Board(boardId, aBoardName);

      String aStageId = "c7c66e8a-610d-40f5-a8b6-455fad0928f6";
      String aStageName = "to do";
      Stage stage = board.addStageToBoard(aStageId, aStageName);

      String workItemTitle = "A";
      String workItemDescription = "A desc";
      String workItemId = "09021d01-3da9-4584-85c0-85211cfa8467";
      WorkItem workItem = board.addWorkItemToStage(aStageId, workItemId, workItemTitle, workItemDescription);

      BoardRepository boardRepository = mock(BoardRepository.class);
      when(boardRepository.findOne(boardId)).thenReturn(board);

      // When I update the work item title with "Ticket 42" and description "Bla bla bla"
      String newWorkItemName = "Ticket 42";
      String newWorkItemDescription = "Bla bla bla";

      BoardIdentityService boardIdentityService = mock(BoardIdentityService.class);
      BoardApplicationService boardApplicationService = new BoardApplicationService(boardRepository,
        boardIdentityService);

      boardApplicationService.changeWorkItemInformation(boardId, workItemId, newWorkItemName, newWorkItemDescription);

      // Then work item name is "Ticket 42" and its description is "Bla bla bla".
      assertThat(board.getWorkItems()).contains(workItem);
    }

  }

  public static class DeleteWorkItem {

    @Test
    public void itShouldSuccessfullyDeleteWorkItem()
      throws StageNotInProcessException, WorkItemNotInProcessException, BoardNotFoundException {
      // Given a board with a stage having a work item A
      String boardId = "35a45cd4-f81f-11e5-9ce9-5e5517507c66";
      String aBoardName = "a board";
      Board board = new Board(boardId, aBoardName);

      String aStageId = "c7c66e8a-610d-40f5-a8b6-455fad0928f6";
      String aStageName = "to do";
      Stage stage = board.addStageToBoard(aStageId, aStageName);

      String workItemTitle = "A";
      String workItemDescription = "A desc";
      String workItemId = "09021d01-3da9-4584-85c0-85211cfa8467";
      WorkItem workItem = board.addWorkItemToStage(aStageId, workItemId, workItemTitle, workItemDescription);

      BoardRepository boardRepository = mock(BoardRepository.class);
      when(boardRepository.findOne(boardId)).thenReturn(board);

      // When I delete the work item
      BoardIdentityService boardIdentityService = mock(BoardIdentityService.class);
      BoardApplicationService boardApplicationService = new BoardApplicationService(boardRepository,
        boardIdentityService);

      boardApplicationService.deleteWorkItem(boardId, workItemId);

      // Then the board does not contains the work item anymore.
      assertThat(board.getWorkItems()).doesNotContain(workItem);
    }

  }

  public static class MoveWorkItemToStage {

    @Test
    public void itShouldSuccessfullyMoveWorkItemToStage()
      throws StageNotInProcessException, BoardNotFoundException, WorkItemNotInProcessException {
      // Given a board having two stages and a work item in the first one
      String boardId = "35a45cd4-f81f-11e5-9ce9-5e5517507c66";
      String aBoardName = "a board";
      Board board = new Board(boardId, aBoardName);

      String stageAId = "c7c66e8a-610d-40f5-a8b6-455fad0928f6";
      String stageAName = "to do";
      Stage stageA = board.addStageToBoard(stageAId, stageAName);

      String stageBId = "7d1eee25-5d9e-4a95-98b0-032a17960a7a";
      String stageBName = "done";
      Stage stageB = board.addStageToBoard(stageBId, stageBName);

      String aWorkItemTitle = "Drink coffee";
      String aWorkItemDescription = "At Starbuck";
      String aWorkItemId = "09021d01-3da9-4584-85c0-85211cfa8467";
      WorkItem workItem = board.addWorkItemToStage(stageAId, aWorkItemId, aWorkItemTitle, aWorkItemDescription);

      BoardRepository boardRepository = mock(BoardRepository.class);
      when(boardRepository.findOne(boardId)).thenReturn(board);

      // When I move the work item from stage A to stage B
      BoardIdentityService boardIdentityService = mock(BoardIdentityService.class);
      BoardApplicationService boardApplicationService = new BoardApplicationService(boardRepository,
        boardIdentityService);

      boardApplicationService.moveWorkItemToStage(boardId, aWorkItemId, stageBId);

      // Then the work item is in stage B
      assertThat(board.workItemsInStage(stageBId)).contains(workItem);
      // And the work item is no more in stage A
      assertThat(board.workItemsInStage(stageAId)).doesNotContain(workItem);
    }

    @Test(expected = StageNotInProcessException.class)
    public void itShouldFailIfTheBoardDoesNotHaveTheSpecifiedStage()
      throws BoardNotFoundException, WorkItemNotInProcessException, StageNotInProcessException {
      // Given a board having two stages and a work item in the first one
      String boardId = "35a45cd4-f81f-11e5-9ce9-5e5517507c66";
      String aBoardName = "a board";
      Board board = new Board(boardId, aBoardName);

      String stageAId = "c7c66e8a-610d-40f5-a8b6-455fad0928f6";
      String stageAName = "to do";
      Stage stageA = board.addStageToBoard(stageAId, stageAName);

      String stageBId = "7d1eee25-5d9e-4a95-98b0-032a17960a7a";
      String stageBName = "done";
      Stage stageB = board.addStageToBoard(stageBId, stageBName);

      String aWorkItemTitle = "Drink coffee";
      String aWorkItemDescription = "At Starbuck";
      String aWorkItemId = "09021d01-3da9-4584-85c0-85211cfa8467";
      WorkItem workItem = board.addWorkItemToStage(stageAId, aWorkItemId, aWorkItemTitle, aWorkItemDescription);

      BoardRepository boardRepository = mock(BoardRepository.class);
      when(boardRepository.findOne(boardId)).thenReturn(board);

      // When I move the work item from stage A to a stage with id "d1a947d2-93b6-4d9a-be8e-b35c47f085ff"
      String stageCId = "d1a947d2-93b6-4d9a-be8e-b35c47f085ff";

      BoardIdentityService boardIdentityService = mock(BoardIdentityService.class);
      BoardApplicationService boardApplicationService = new BoardApplicationService(boardRepository,
        boardIdentityService);
      boardApplicationService.moveWorkItemToStage(boardId, aWorkItemId, stageCId);

      // Then it should fail
    }

    @Test(expected = WorkItemNotInProcessException.class)
    public void itShouldFailIfTheBoardDoesNotHaveTheSpecifiedWorkItem()
      throws StageNotInProcessException, WorkItemNotInProcessException, BoardNotFoundException {
      // Given a board having two stages and a work item in the first one
      String boardId = "35a45cd4-f81f-11e5-9ce9-5e5517507c66";
      String aBoardName = "a board";
      Board board = new Board(boardId, aBoardName);

      String stageAId = "c7c66e8a-610d-40f5-a8b6-455fad0928f6";
      String stageAName = "to do";
      Stage stageA = board.addStageToBoard(stageAId, stageAName);

      String stageBId = "7d1eee25-5d9e-4a95-98b0-032a17960a7a";
      String stageBName = "done";
      Stage stageB = board.addStageToBoard(stageBId, stageBName);

      String aWorkItemTitle = "Drink coffee";
      String aWorkItemDescription = "At Starbuck";
      String aWorkItemId = "09021d01-3da9-4584-85c0-85211cfa8467";
      WorkItem workItem = board.addWorkItemToStage(stageAId, aWorkItemId, aWorkItemTitle, aWorkItemDescription);

      BoardRepository boardRepository = mock(BoardRepository.class);
      when(boardRepository.findOne(boardId)).thenReturn(board);

      // When I move the work item with id ""d1a947d2-93b6-4d9a-be8e-b35c47f085ff"" to stage B
      String unknownWorkItem = "d1a947d2-93b6-4d9a-be8e-b35c47f085ff";

      BoardIdentityService boardIdentityService = mock(BoardIdentityService.class);
      BoardApplicationService boardApplicationService = new BoardApplicationService(boardRepository,
        boardIdentityService);
      boardApplicationService.moveWorkItemToStage(boardId, unknownWorkItem, stageBId);

      // Then it should fail
    }

  }

  public static class MoveWorkItemToPosition {

    @Test
    public void itShouldSuccessfullyChangeThePositionOfWorkItems()
      throws StageNotInProcessException, WorkItemNotInStageException, BoardNotFoundException,
             WorkItemNotInProcessException {
      // Given a board with a stage having two work items A and B
      String boardId = "35a45cd4-f81f-11e5-9ce9-5e5517507c66";
      String aBoardName = "a board";
      Board board = new Board(boardId, aBoardName);

      String aStageId = "c7c66e8a-610d-40f5-a8b6-455fad0928f6";
      String aStageName = "to do";
      Stage stage = board.addStageToBoard(aStageId, aStageName);

      String workItemATitle = "A";
      String workItemADescription = "A desc";
      String workItemAId = "09021d01-3da9-4584-85c0-85211cfa8467";
      WorkItem workItemA = board.addWorkItemToStage(aStageId, workItemAId, workItemATitle, workItemADescription);

      String workItemBTitle = "B";
      String workItemBDescription = "B desc";
      String workItemBId = "81963606-76a1-41b5-82aa-5aba7b4dc115";
      WorkItem workItemB = board.addWorkItemToStage(aStageId, workItemBId, workItemBTitle, workItemBDescription);

      BoardRepository boardRepository = mock(BoardRepository.class);
      when(boardRepository.findOne(boardId)).thenReturn(board);

      // When I switch the order of work items
      BoardIdentityService boardIdentityService = mock(BoardIdentityService.class);
      BoardApplicationService boardApplicationService = new BoardApplicationService(boardRepository,
        boardIdentityService);

      boardApplicationService.moveWorkItemToPosition(board.getId(), workItemA.getId(), 2);

      // Then B is the first one and A is the last one
      assertThat(board.workItemsInStage(aStageId)).containsExactly(workItemB, workItemA);
    }

    @Test(expected = WorkItemNotInProcessException.class)
    public void itShouldFailIfTheWorkItemIsNotOnTheBoard()
      throws StageNotInProcessException, WorkItemNotInStageException, BoardNotFoundException,
             WorkItemNotInProcessException {
      // Given a board with a stage having two work items A and B
      String boardId = "35a45cd4-f81f-11e5-9ce9-5e5517507c66";
      String aBoardName = "a board";
      Board board = new Board(boardId, aBoardName);

      String aStageId = "c7c66e8a-610d-40f5-a8b6-455fad0928f6";
      String aStageName = "to do";
      Stage stage = board.addStageToBoard(aStageId, aStageName);

      String workItemATitle = "A";
      String workItemADescription = "A desc";
      String workItemAId = "09021d01-3da9-4584-85c0-85211cfa8467";
      WorkItem workItemA = board.addWorkItemToStage(aStageId, workItemAId, workItemATitle, workItemADescription);

      String workItemBTitle = "B";
      String workItemBDescription = "B desc";
      String workItemBId = "81963606-76a1-41b5-82aa-5aba7b4dc115";
      WorkItem workItemB = board.addWorkItemToStage(aStageId, workItemBId, workItemBTitle, workItemBDescription);

      BoardRepository boardRepository = mock(BoardRepository.class);
      when(boardRepository.findOne(boardId)).thenReturn(board);

      // When I change the order of an unknown work item
      String unknownWorkItemId = "1d0c28c7-64c3-41ef-bcd6-e0fce8cfcfa3";

      BoardIdentityService boardIdentityService = mock(BoardIdentityService.class);
      BoardApplicationService boardApplicationService = new BoardApplicationService(boardRepository,
        boardIdentityService);

      boardApplicationService.moveWorkItemToPosition(board.getId(), unknownWorkItemId, 2);

      // Then I got an exception
    }

  }

  public static class ArchiveWorkItem {

    @Test
    public void itShouldNotReturnArchivedWorkItems()
      throws StageNotInProcessException, WorkItemNotInProcessException, BoardNotFoundException {
      // Given a board with a stage having two work items A and B
      String boardId = "35a45cd4-f81f-11e5-9ce9-5e5517507c66";
      String aBoardName = "a board";
      Board board = new Board(boardId, aBoardName);

      String aStageId = "c7c66e8a-610d-40f5-a8b6-455fad0928f6";
      String aStageName = "to do";
      Stage stage = board.addStageToBoard(aStageId, aStageName);

      String workItemATitle = "A";
      String workItemADescription = "A desc";
      String workItemAId = "09021d01-3da9-4584-85c0-85211cfa8467";
      WorkItem workItemA = board.addWorkItemToStage(aStageId, workItemAId, workItemATitle, workItemADescription);

      String workItemBTitle = "B";
      String workItemBDescription = "B desc";
      String workItemBId = "81963606-76a1-41b5-82aa-5aba7b4dc115";
      WorkItem workItemB = board.addWorkItemToStage(aStageId, workItemBId, workItemBTitle, workItemBDescription);

      BoardRepository boardRepository = mock(BoardRepository.class);
      when(boardRepository.findOne(boardId)).thenReturn(board);

      // When I archiveWorkItem workItemA
      BoardIdentityService boardIdentityService = mock(BoardIdentityService.class);
      BoardApplicationService boardApplicationService = new BoardApplicationService(boardRepository,
        boardIdentityService);
      boardApplicationService.archiveWorkItem(boardId, workItemAId);

      // Then work item A should not be in the list of work items any more
      assertThat(board.getWorkItems()).doesNotContain(workItemA);
    }

  }

  public static class SendWorkItemBackToBoard {

    @Test
    public void itShouldMakeWorkItemSentBackToBoardVisible()
      throws StageNotInProcessException, BoardNotFoundException, WorkItemNotInProcessException,
             WorkItemNotArchivedException {
      // Given a board with a stage having a work item A archived
      String boardId = "35a45cd4-f81f-11e5-9ce9-5e5517507c66";
      String aBoardName = "a board";
      Board board = new Board(boardId, aBoardName);

      String aStageId = "c7c66e8a-610d-40f5-a8b6-455fad0928f6";
      String aStageName = "to do";
      Stage stage = board.addStageToBoard(aStageId, aStageName);

      String workItemATitle = "A";
      String workItemADescription = "A desc";
      String workItemAId = "09021d01-3da9-4584-85c0-85211cfa8467";
      WorkItem workItemA = board.addWorkItemToStage(aStageId, workItemAId, workItemATitle, workItemADescription);
      board.archiveWorkItemWithId(workItemAId);

      BoardRepository boardRepository = mock(BoardRepository.class);
      when(boardRepository.findOne(boardId)).thenReturn(board);

      // When I archiveWorkItem workItemA
      BoardIdentityService boardIdentityService = mock(BoardIdentityService.class);
      BoardApplicationService boardApplicationService = new BoardApplicationService(boardRepository,
        boardIdentityService);
      boardApplicationService.sendWorkItemBackToBoard(boardId, workItemAId);

      // Then work item A should not be in the list of work items any more
      assertThat(board.getWorkItems()).contains(workItemA);
    }

  }
}
