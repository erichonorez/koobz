package org.svomz.apps.koobz.board.applications;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.svomz.apps.koobz.board.application.BoardApplicationService;
import org.svomz.apps.koobz.board.application.BoardNotFoundException;
import org.svomz.apps.koobz.board.domain.model.Board;
import org.svomz.apps.koobz.board.domain.model.BoardRepository;
import org.svomz.apps.koobz.board.domain.model.Stage;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(Suite.class)
@Suite.SuiteClasses({
  BoardApplicationServiceUnitTest.CreateBoard.class,
  BoardApplicationServiceUnitTest.FindBoard.class,
  BoardApplicationServiceUnitTest.CreateStage.class,
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

  public static class FindBoard {

    @Test
    public void itShouldSuccessfullyFindAnExistingBoard() throws BoardNotFoundException {
      // Given a persisted board with id "35a45cd4-f81f-11e5-9ce9-5e5517507c66"
      BoardRepository boardRepository = mock(BoardRepository.class);
      BoardApplicationService boardService = new BoardApplicationService(boardRepository);

      when(boardRepository.nextIdentity()).thenReturn("35a45cd4-f81f-11e5-9ce9-5e5517507c66");
      String aBoardName = "A name";
      Board persistedBoard = boardService.createBoard(aBoardName);

      when(boardRepository.findOne(persistedBoard.getId())).thenReturn(persistedBoard);

      // When a user makes a query to retrive board with id "35a45cd4-f81f-11e5-9ce9-5e5517507c66"
      Board board = boardService.findBoard("35a45cd4-f81f-11e5-9ce9-5e5517507c66");

      // Then the user gets the corresponding board
      assertThat(board).isNotNull();
      assertThat(board.getName()).isEqualTo(aBoardName);
      assertThat(board.getWorkItems()).isEmpty();
      assertThat(board.getStages()).isEmpty();
      assertThat(board.getId()).isNotNull();
    }

    @Test(expected = BoardNotFoundException.class)
    public void itShouldThrowBoardNotFoundExceptionIfBoardDoesNotExist()
      throws BoardNotFoundException {
      // Given the board with id "35a45cd4-f81f-11e5-9ce9-5e5517507c66" does not exist
      BoardRepository boardRepository = mock(BoardRepository.class);
      when(boardRepository.findOne("35a45cd4-f81f-11e5-9ce9-5e5517507c66"))
        .thenReturn(null);

      // When I query the for this id
      BoardApplicationService boardService = new BoardApplicationService(boardRepository);
      boardService.findBoard("35a45cd4-f81f-11e5-9ce9-5e5517507c66");

      //then I get a BoardNotFoundException
    }

  }

  public static class CreateStage {

    @Test
    public void itShouldSuccessfullyCreateANewStage() throws BoardNotFoundException {
      // Given a board with id "35a45cd4-f81f-11e5-9ce9-5e5517507c66"
      String boardId = "35a45cd4-f81f-11e5-9ce9-5e5517507c66";

      BoardRepository boardRepository = mock(BoardRepository.class);
      when(boardRepository.findOne(boardId)).thenReturn(new Board(boardId, "a board"));

      BoardApplicationService boardApplicationService = new BoardApplicationService(boardRepository);

      // When I create a new stage on board with id "35a45cd4-f81f-11e5-9ce9-5e5517507c66"
      // And with "to do" as title
      String title = "to do";

      Stage stage = boardApplicationService.createStage(boardId, title);

      // Then the board has a new stage
      Board board = boardApplicationService.findBoard(boardId);

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

}
