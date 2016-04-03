package org.svomz.apps.koobz.board.ports.adapters.rest.resources;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
import org.svomz.apps.koobz.board.domain.model.WorkItemNotArchivedException;
import org.svomz.apps.koobz.board.ports.adapters.rest.models.WorkItemArchivingInputModel;
import org.svomz.apps.koobz.board.ports.adapters.rest.models.WorkItemInputModel;
import org.svomz.apps.koobz.board.ports.adapters.rest.models.WorkItemMoveInputModel;
import org.svomz.apps.koobz.board.ports.adapters.rest.models.WorkItemPositionInputModel;
import org.svomz.apps.koobz.board.ports.adapters.rest.models.WorkItemViewModel;
import org.svomz.apps.koobz.board.domain.model.StageNotInProcessException;
import org.svomz.apps.koobz.board.domain.model.WorkItem;
import org.svomz.apps.koobz.board.domain.model.WorkItemNotInStageException;
import org.svomz.apps.koobz.board.domain.model.WorkItemNotInProcessException;

import com.google.common.base.Preconditions;

@Component
@Path("/boards/{boardId}/workitems")
public class WorkItemResource {

  private final BoardApplicationService boardApplicationService;

  @Inject
  public WorkItemResource(
    final BoardApplicationService boardApplicationService) {
    this.boardApplicationService = boardApplicationService;
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response create(@NotNull @PathParam("boardId") final String boardId,
    @NotNull @Valid final WorkItemInputModel workItemInputModel)
    throws StageNotInProcessException, BoardNotFoundException {
    Preconditions.checkNotNull(boardId);
    Preconditions.checkNotNull(workItemInputModel);

    WorkItem workItem = this.boardApplicationService.createWorkItem(
      boardId,
      workItemInputModel.getStageId(),
      workItemInputModel.getTitle(),
      workItemInputModel.getDescription()
    );

    return Response.status(Status.CREATED).entity(new WorkItemViewModel(workItem)).build();
  }

  @PUT
  @Path("{id}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response update(@PathParam("boardId") final String boardId, @PathParam("id") final String workItemId,
    @NotNull @Valid final WorkItemInputModel workItemInputModel) throws
    WorkItemNotInProcessException, StageNotInProcessException, WorkItemNotInStageException, BoardNotFoundException {

    Preconditions.checkNotNull(boardId);
    Preconditions.checkNotNull(workItemId);
    Preconditions.checkNotNull(workItemInputModel);

    this.boardApplicationService.changeWorkItemInformation(
      boardId, workItemId, workItemInputModel.getTitle(), workItemInputModel.getDescription());
    
    return Response.status(Status.OK)
      .build();
  }

  @DELETE
  @Path("{id}")
  public void delete(@PathParam("boardId") final String boardId, @PathParam("id") final String workItemId)
    throws WorkItemNotInProcessException, BoardNotFoundException {
    Preconditions.checkNotNull(boardId);
    Preconditions.checkNotNull(workItemId);

    this.boardApplicationService.removeWorkItemFromBoard(boardId, workItemId);
  }

  @POST
  @Path("{id}/move")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response move(@PathParam("boardId") final String boardId, @PathParam("id") final String workItemId,
    @NotNull @Valid final WorkItemMoveInputModel input)
    throws BoardNotFoundException, WorkItemNotInProcessException, StageNotInProcessException {
    this.boardApplicationService.moveWorkItemToStage(boardId, workItemId, input.getTo());

    return Response.status(Status.OK)
      .build();
  }

  @POST
  @Path("{id}/position")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response putAtPosition(@PathParam("boardId") final String boardId, @PathParam("id") final String workItemId,
    @NotNull @Valid final WorkItemPositionInputModel input)
    throws WorkItemNotInStageException, BoardNotFoundException, WorkItemNotInProcessException {

    this.boardApplicationService.moveWorkItemToPosition(boardId, workItemId, input.getNewPosition());

    return Response.status(Status.OK)
      .build();
  }

  @POST
  @Path("{id}/archiving")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response archiving(@PathParam("boardId") final String boardId, @PathParam("id") final String workItemId,
    @NotNull @Valid final WorkItemArchivingInputModel input)
    throws WorkItemNotInProcessException, BoardNotFoundException, WorkItemNotArchivedException {

    if (input.isArchived()) {
      this.boardApplicationService.archiveWorkItem(boardId, workItemId);
    } else {
      this.boardApplicationService.sendWorkItemBackToBoard(boardId, workItemId);
    }

    return Response.status(Status.OK)
      .build();
  }
}
