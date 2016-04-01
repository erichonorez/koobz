package org.svomz.apps.koobz.board.ports.adapters.rest.resources;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.springframework.stereotype.Component;
import org.svomz.apps.koobz.board.ports.adapters.rest.models.BoardInputModel;
import org.svomz.apps.koobz.board.ports.adapters.rest.models.BoardViewModel;
import org.svomz.apps.koobz.board.domain.Board;
import org.svomz.apps.koobz.board.domain.BoardRepository;
import org.svomz.apps.koobz.board.infrastructure.domain.EntityNotFoundException;

import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.base.Preconditions;

@Component
@Path("/boards")
@Transactional
public class BoardResource {

  private BoardRepository boardRepository;

  @Inject
  public BoardResource(final BoardRepository boardRepository) {
    Preconditions.checkNotNull(boardRepository);

    this.boardRepository = boardRepository;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @JsonView(BoardViewModel.SimpleView.class)
  public List<BoardViewModel> getBoards() {
    Iterable<Board> boards = this.boardRepository.findAll();

    List<BoardViewModel> viewModels = new ArrayList<>();
    boards.forEach(board -> viewModels.add(new BoardViewModel(board)));

    return viewModels;
  }

  @GET
  @Path("{boardId}")
  @Produces(MediaType.APPLICATION_JSON)
  @JsonView(BoardViewModel.FullView.class)
  public BoardViewModel getBoard(@PathParam("boardId") final String boardId)
    throws EntityNotFoundException {
    Board board = this.boardRepository.findOrThrowException(boardId);

    return new BoardViewModel(board);
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @JsonView(BoardViewModel.SimpleView.class)
  public Response createBoard(@NotNull @Valid final BoardInputModel boardInputModel) {
    Preconditions.checkNotNull(boardInputModel);

    Board board = new Board(boardInputModel.getName());
    this.boardRepository.save(board);
    
    BoardViewModel viewModel = new BoardViewModel(board);
    return Response.status(Status.CREATED).entity(viewModel).build();
  }

  @PUT
  @Path("{boardId}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @JsonView(BoardViewModel.SimpleView.class)
  public BoardViewModel updateBoard(@PathParam("boardId") final String boardId,
      @NotNull @Valid final BoardInputModel boardInputModel) throws EntityNotFoundException {
    Preconditions.checkNotNull(boardInputModel);
    
    Board board = this.boardRepository.findOrThrowException(boardId);
    board.setName(boardInputModel.getName());

    return new BoardViewModel(board);
  }

  @DELETE
  @Path("{boardId}")
  public void deleteBoard(@PathParam("boardId") final String boardId)
    throws EntityNotFoundException {
    Board persistedBoard = this.boardRepository.findOrThrowException(boardId);

    this.boardRepository.delete(persistedBoard);
  }

}
