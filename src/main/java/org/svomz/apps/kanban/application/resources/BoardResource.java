package org.svomz.apps.kanban.application.resources;

import java.util.List;

import javax.inject.Inject;
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

import org.svomz.apps.kanban.application.models.BoardInputModel;
import org.svomz.apps.kanban.domain.entities.Board;
import org.svomz.apps.kanban.domain.services.KanbanService;
import org.svomz.commons.persistence.EntityNotFoundException;

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
  public List<Board> getBoards() {
    return this.kanbanService.getAll();
  }

  /**
   * TODO the result of this method should return the board with associated entities.
   */
  @GET
  @Path("{boardId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Board getBoard(@PathParam("boardId") final long boardId) throws EntityNotFoundException {
    return this.kanbanService.get(boardId);
  }

  /**
   * TODO the result of this method should return the board without associated entities. TODO create
   * a specific object for the request + TEST to prove what I want. TODO add validation on the input
   * + TEST to prove what I want. TODO response 201
   */
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response createBoard(final BoardInputModel boardInputModel) {
    Preconditions.checkNotNull(boardInputModel);

    final Board createdBoard = this.kanbanService.createBoard(boardInputModel.getName());
    return Response.status(Status.CREATED).entity(createdBoard).build();
  }

  @PUT
  @Path("{boardId}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Board updateBoard(@PathParam("boardId") final long boardId,
      final BoardInputModel boardInputModel) throws EntityNotFoundException {
    Preconditions.checkNotNull(boardInputModel);

    return this.kanbanService.updateBoard(boardId, boardInputModel.getName());
  }

  @DELETE
  @Path("{boardId}")
  public void deleteBoard(@PathParam("boardId") final long boardId) throws EntityNotFoundException {
    this.kanbanService.deleteBoard(boardId);
  }

}
