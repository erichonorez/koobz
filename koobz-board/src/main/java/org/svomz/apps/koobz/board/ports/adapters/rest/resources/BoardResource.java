package org.svomz.apps.koobz.board.ports.adapters.rest.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
import org.svomz.apps.koobz.board.application.BoardApplicationService;
import org.svomz.apps.koobz.board.application.BoardNotFoundException;
import org.svomz.apps.koobz.board.application.BoardQueryService;
import org.svomz.apps.koobz.board.ports.adapters.rest.models.BoardInputModel;
import org.svomz.apps.koobz.board.ports.adapters.rest.models.BoardViewModel;
import org.svomz.apps.koobz.board.domain.model.Board;
import org.svomz.apps.koobz.board.domain.model.BoardRepository;
import org.svomz.apps.koobz.board.infrastructure.domain.EntityNotFoundException;

import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.base.Preconditions;

@Component
@Path("/boards")
public class BoardResource {

  private final BoardApplicationService boardApplicationService;
  private final BoardQueryService boardQueryService;

  private BoardRepository boardRepository;

  @Inject
  public BoardResource(final BoardRepository boardRepository, final BoardApplicationService boardApplicationService, final
    BoardQueryService boardQueryService) {
    Preconditions.checkNotNull(boardRepository);
    Preconditions.checkNotNull(boardApplicationService);
    Preconditions.checkNotNull(boardQueryService);

    this.boardRepository = boardRepository;
    this.boardApplicationService = boardApplicationService;
    this.boardQueryService = boardQueryService;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @JsonView(BoardViewModel.SimpleView.class)
  @Transactional
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
  public Response getBoard(@NotNull @PathParam("boardId") final String boardId)
    throws BoardNotFoundException {
    Preconditions.checkNotNull(boardId);

    Optional<Board> optionalBoard = this.boardQueryService.findBoard(boardId);

    Response response;
    if (optionalBoard.isPresent()) {
      response = Response.status(Status.OK)
        .entity(new BoardViewModel(optionalBoard.get()))
        .build();
    } else {
      response = Response.status(Status.NOT_FOUND)
        .build();
    }

    return response;
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @JsonView(BoardViewModel.SimpleView.class)
  public Response createBoard(@NotNull @Valid final BoardInputModel boardInputModel) {
    Preconditions.checkNotNull(boardInputModel);

    Board board = this.boardApplicationService.createBoard(boardInputModel.getName());
    
    BoardViewModel viewModel = new BoardViewModel(board);
    return Response.status(Status.CREATED).entity(viewModel).build();
  }

  @PUT
  @Path("{boardId}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @JsonView(BoardViewModel.SimpleView.class)
  @Transactional
  public Response updateBoard(@PathParam("boardId") final String boardId,
      @NotNull @Valid final BoardInputModel boardInputModel)
    throws EntityNotFoundException, BoardNotFoundException {
    Preconditions.checkNotNull(boardInputModel);

    this.boardApplicationService.changeBoardName(boardId, boardInputModel.getName());

    return Response.status(Status.OK)
      .build();
  }

  @DELETE
  @Path("{boardId}")
  @Transactional
  public void deleteBoard(@PathParam("boardId") final String boardId)
    throws EntityNotFoundException {
    Board persistedBoard = this.boardRepository.findOrThrowException(boardId);

    this.boardRepository.delete(persistedBoard);
  }

}
