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
import org.svomz.apps.kanban.domain.entities.StageNotInProcessException;
import org.svomz.apps.kanban.domain.entities.WorkItem;
import org.svomz.apps.kanban.domain.entities.WorkItemNotOnBoardException;
import org.svomz.apps.kanban.domain.services.KanbanService;
import org.svomz.commons.infrastructure.persistence.EntityNotFoundException;

@Path("/boards/{boardId}/workitems")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class WorkItemResource {

  private final KanbanService kanbanService;
  private long boardId;

  @Inject
  public WorkItemResource(KanbanService kanbanService, @PathParam("boardId") long boardId) {
    Preconditions.checkNotNull(kanbanService);

    this.kanbanService = kanbanService;
    this.boardId = boardId;
  }

  @GET
  public List<WorkItem> getWorkItems() throws EntityNotFoundException {
    return this.kanbanService.getWorkItems(this.boardId);
  }

  @POST
  public Response create(WorkItemInputModel workItemInputModel) throws EntityNotFoundException,
      StageNotInProcessException {
    Preconditions.checkNotNull(workItemInputModel);

    WorkItem workItem =
        this.kanbanService.addWorkItemToBoard(this.boardId, workItemInputModel.getStageId(),
            workItemInputModel.getText());
    return Response.status(Status.CREATED).entity(workItem).build();
  }

  @PUT
  @Path("{id}")
  public WorkItem update(@PathParam("id") long workItemId,
      final WorkItemInputModel workItemInputModel) throws EntityNotFoundException,
      WorkItemNotOnBoardException, StageNotInProcessException {
    Preconditions.checkNotNull(workItemInputModel);

    return this.kanbanService.updateWorkItem(boardId, workItemId, workItemInputModel.getText(),
        workItemInputModel.getStageId());
  }

  @DELETE
  @Path("{id}")
  public void delte(@PathParam("id") long workItemId) throws EntityNotFoundException,
      WorkItemNotOnBoardException {
    this.kanbanService.removeWorkItemFromBoard(this.boardId, workItemId);
  }

}
