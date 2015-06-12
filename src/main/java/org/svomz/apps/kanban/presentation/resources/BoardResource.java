package org.svomz.apps.kanban.presentation.resources;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
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

import org.svomz.apps.kanban.application.services.KanbanService;
import org.svomz.apps.kanban.domain.entities.Board;
import org.svomz.apps.kanban.presentation.models.BoardInputModel;
import org.svomz.apps.kanban.presentation.models.BoardViewModel;
import org.svomz.commons.persistence.EntityNotFoundException;

import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.base.Preconditions;

@Path("/boards")
public class BoardResource {

  private final KanbanService kanbanService;

  @Inject
  public BoardResource(final KanbanService kanbanService) {
    Preconditions.checkNotNull(kanbanService);

    this.kanbanService = kanbanService;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @JsonView(BoardViewModel.SimpleView.class)
  public List<BoardViewModel> getBoards() {
    List<BoardViewModel> viewModels = new ArrayList<>();
    List<Board> boards = this.kanbanService.getAll();
    for (Board board : boards) {
      viewModels.add(new BoardViewModel(board));
    }
    return viewModels;
  }

  @GET
  @Path("{boardId}")
  @Produces(MediaType.APPLICATION_JSON)
  @JsonView(BoardViewModel.FullView.class)
  public BoardViewModel getBoard(@PathParam("boardId") final long boardId) throws EntityNotFoundException {
    Board board = kanbanService.get(boardId);
    return new BoardViewModel(board);
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @JsonView(BoardViewModel.SimpleView.class)
  public Response createBoard(@NotNull @Valid final BoardInputModel boardInputModel) {
    Preconditions.checkNotNull(boardInputModel);

    final Board createdBoard = this.kanbanService.createBoard(boardInputModel.getName());
    BoardViewModel viewModel = new BoardViewModel(createdBoard);
    return Response.status(Status.CREATED).entity(viewModel).build();
  }

  @PUT
  @Path("{boardId}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @JsonView(BoardViewModel.SimpleView.class)
  public BoardViewModel updateBoard(@PathParam("boardId") final long boardId,
      @NotNull @Valid final BoardInputModel boardInputModel) throws EntityNotFoundException {
    Preconditions.checkNotNull(boardInputModel);

    Board updateBoard = this.kanbanService.updateBoard(boardId, boardInputModel.getName());
    return new BoardViewModel(updateBoard);
  }

  @DELETE
  @Path("{boardId}")
  public void deleteBoard(@PathParam("boardId") final long boardId) throws EntityNotFoundException {
    this.kanbanService.deleteBoard(boardId);
  }

}
