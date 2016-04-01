package org.svomz.apps.koobz.board.applications;

import org.junit.Test;
import org.svomz.apps.koobz.board.application.BoardApplicationService;
import org.svomz.apps.koobz.board.application.BoardNotFoundException;
import org.svomz.apps.koobz.board.application.BoardQueryService;
import org.svomz.apps.koobz.board.domain.model.Board;
import org.svomz.apps.koobz.board.domain.model.BoardRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BoardQueryServiceUnitTest {

  @Test
  public void itShouldSuccessfullyFindAnExistingBoard() throws BoardNotFoundException {
    // Given a persisted board with id "35a45cd4-f81f-11e5-9ce9-5e5517507c66"
    String boardId = "35a45cd4-f81f-11e5-9ce9-5e5517507c66";
    // And with "A name" as name
    String aBoardName = "A name";

    BoardRepository boardRepository = mock(BoardRepository.class);
    BoardQueryService boardService = new BoardQueryService(boardRepository);

    when(boardRepository.findOne(boardId)).thenReturn(new Board(boardId, aBoardName));

    // When a user makes a query to retrive board with id "35a45cd4-f81f-11e5-9ce9-5e5517507c66"
    Optional<Board> optionalBoard = boardService.findBoard(boardId);

    // Then the user gets the corresponding board
    assertThat(optionalBoard.isPresent()).isNotNull();

    Board board = optionalBoard.get();
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
    BoardQueryService boardService = new BoardQueryService(boardRepository);
    boardService.findBoard("35a45cd4-f81f-11e5-9ce9-5e5517507c66");

    //then I get a BoardNotFoundException
  }

}
