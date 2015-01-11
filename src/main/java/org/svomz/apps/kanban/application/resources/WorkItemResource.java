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

import jersey.repackaged.com.google.common.base.Preconditions;

import org.svomz.apps.kanban.application.models.WorkItemInputModel;
import org.svomz.apps.kanban.domain.entities.WorkItem;
import org.svomz.apps.kanban.domain.exceptions.StageNotInProcessException;
import org.svomz.apps.kanban.domain.exceptions.WorkItemNotOnBoardException;
import org.svomz.apps.kanban.domain.services.KanbanService;
import org.svomz.commons.persistence.EntityNotFoundException;

@Path("/boards/{boardId}/workitems")
public class WorkItemResource {

  private final KanbanService kanbanService;
  private final long boardId;

  @Inject
  public WorkItemResource(final KanbanService kanbanService,
      @PathParam("boardId") final long boardId) {
    Preconditions.checkNotNull(kanbanService);

    this.kanbanService = kanbanService;
    this.boardId = boardId;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public List<WorkItem> getWorkItems() throws EntityNotFoundException {
    return this.kanbanService.getWorkItems(this.boardId);
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response create(final WorkItemInputModel workItemInputModel)
      throws EntityNotFoundException, StageNotInProcessException {
    Preconditions.checkNotNull(workItemInputModel);

    final WorkItem workItem =
        this.kanbanService.addWorkItemToBoard(this.boardId, workItemInputModel.getStageId(),
            workItemInputModel.getText());

    return Response.status(Status.CREATED).entity(workItem).build();
  }

  @PUT
  @Path("{id}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public WorkItem update(@PathParam("id") final long workItemId,
      final WorkItemInputModel workItemInputModel) throws EntityNotFoundException,
      WorkItemNotOnBoardException, StageNotInProcessException {
    Preconditions.checkNotNull(workItemInputModel);

    return this.kanbanService.updateWorkItem(this.boardId, workItemId,
        workItemInputModel.getText(), workItemInputModel.getStageId());
  }

  @DELETE
  @Path("{id}")
  public void delte(@PathParam("id") final long workItemId) throws EntityNotFoundException,
      WorkItemNotOnBoardException {
    this.kanbanService.removeWorkItemFromBoard(this.boardId, workItemId);
  }

}
