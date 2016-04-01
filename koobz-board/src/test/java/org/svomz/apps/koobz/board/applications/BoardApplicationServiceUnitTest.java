package org.svomz.apps.koobz.board.applications;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.svomz.apps.koobz.board.application.BoardApplicationService;
import org.svomz.apps.koobz.board.application.BoardNotFoundException;
import org.svomz.apps.koobz.board.domain.model.Board;
import org.svomz.apps.koobz.board.domain.model.BoardRepository;
import org.svomz.apps.koobz.board.domain.model.Stage;
import org.svomz.apps.koobz.board.domain.model.StageNotInProcessException;
import org.svomz.apps.koobz.board.domain.model.WorkItem;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(Suite.class)
@Suite.SuiteClasses({
  BoardApplicationServiceUnitTest.CreateBoard.class,
  BoardApplicationServiceUnitTest.CreateStage.class,
  BoardApplicationServiceUnitTest.CreateWorkItem.class
})
public class BoardApplicationServiceUnitTest {

  public static class CreateBoard {

    @Test
    public void itShouldCreateANewBoardSuccessfully() {
      BoardRepository boardRepository = mock(BoardRepository.class);
      BoardApplicationService boardService = new BoardApplicationService(boardRepository);

      when(boardRepository.nextIdentity()).thenReturn(UUID.randomUUID().toString());
      String aBoardName = "A name";
      Board board = boardService.createBoard(aBoardName);

      assertThat(board).isNotNull();
      assertThat(board.getName()).isEqualTo(aBoardName);
      assertThat(board.getWorkItems()).isEmpty();
      assertThat(board.getStages()).isEmpty();
      assertThat(board.getId()).isNotNull();
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

      BoardApplicationService boardApplicationService = new BoardApplicationService(boardRepository);

      // When I create a new stage on board with id "35a45cd4-f81f-11e5-9ce9-5e5517507c66"
      // And with "to do" as title
      String title = "to do";

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

      BoardApplicationService boardApplicationService = new BoardApplicationService(boardRepository);

      // When I create a new stage on board with id "35a45cd4-f81f-11e5-9ce9-5e5517507c66"
      // And with "to do" as title
      String title = "to do";

      Stage stage = boardApplicationService.createStage(boardId, title);

      // Then I get a BoardNotFoundException
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
      String stageId = "35a45cd4-f81f-11e5-9ce9-5e5517507c66";
      String aStageName = "to do";

      Board board = new Board(boardId, aBoardName);
      Stage stage = new Stage(stageId, aStageName);

      stage.addToBoard(board);

      BoardRepository boardRepository = mock(BoardRepository.class);
      when(boardRepository.findOne(boardId)).thenReturn(board);

      // When user adds a Work Item with title "Drink coffee" to the stage with id "ac329010-f837-11e5-9ce9-5e5517507c66"
      String aWorkItemTitle = "Drink coffee";
      String aWorkItemDescription = "At Starbuck";

      BoardApplicationService boardApplicationService = new BoardApplicationService(boardRepository);
      WorkItem workItem = boardApplicationService.createWorkItem(
        boardId,
        stageId,
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

      BoardApplicationService boardApplicationService = new BoardApplicationService(boardRepository);

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

      BoardApplicationService boardApplicationService = new BoardApplicationService(boardRepository);
      WorkItem workItem = boardApplicationService.createWorkItem(
        boardId,
        stageId,
        aWorkItemTitle,
        aWorkItemDescription
      );

      // Then I get a StageNotInProcessException
    }

  }

}
