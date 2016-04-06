package org.svomz.apps.koobz.ports.adapters.rest.resources;

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
import org.svomz.apps.koobz.application.WorkflowApplicationService;
import org.svomz.apps.koobz.application.WorkflowNotFoundException;
import org.svomz.apps.koobz.domain.model.WorkItemNotArchivedException;
import org.svomz.apps.koobz.ports.adapters.rest.models.WorkItemArchivingInputModel;
import org.svomz.apps.koobz.ports.adapters.rest.models.WorkItemInputModel;
import org.svomz.apps.koobz.ports.adapters.rest.models.WorkItemMoveInputModel;
import org.svomz.apps.koobz.ports.adapters.rest.models.WorkItemPositionInputModel;
import org.svomz.apps.koobz.ports.adapters.rest.models.WorkItemViewModel;
import org.svomz.apps.koobz.domain.model.StageNotInProcessException;
import org.svomz.apps.koobz.domain.model.WorkItem;
import org.svomz.apps.koobz.domain.model.WorkItemNotInStageException;
import org.svomz.apps.koobz.domain.model.WorkItemNotInProcessException;

import com.google.common.base.Preconditions;

@Component
@Path("/workflows/{workflowId}/workitems")
public class WorkItemResource {

  private final WorkflowApplicationService workflowApplicationService;

  @Inject
  public WorkItemResource(
    final WorkflowApplicationService workflowApplicationService) {
    this.workflowApplicationService = workflowApplicationService;
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response create(@NotNull @PathParam("workflowId") final String workflowId,
    @NotNull @Valid final WorkItemInputModel workItemInputModel)
    throws StageNotInProcessException, WorkflowNotFoundException {
    Preconditions.checkNotNull(workflowId);
    Preconditions.checkNotNull(workItemInputModel);

    WorkItem workItem = this.workflowApplicationService.addWorkItemToWorkflow(
      workflowId,
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
  public Response update(@PathParam("workflowId") final String workflowId, @PathParam("id") final String workItemId,
    @NotNull @Valid final WorkItemInputModel workItemInputModel) throws
                                                                 WorkItemNotInProcessException, StageNotInProcessException, WorkItemNotInStageException,
                                                                 WorkflowNotFoundException {

    Preconditions.checkNotNull(workflowId);
    Preconditions.checkNotNull(workItemId);
    Preconditions.checkNotNull(workItemInputModel);

    this.workflowApplicationService.changeWorkItemInformation(
      workflowId, workItemId, workItemInputModel.getTitle(), workItemInputModel.getDescription());
    
    return Response.status(Status.OK)
      .build();
  }

  @DELETE
  @Path("{id}")
  public void delete(@PathParam("workflowId") final String workflowId, @PathParam("id") final String workItemId)
    throws WorkItemNotInProcessException, WorkflowNotFoundException {
    Preconditions.checkNotNull(workflowId);
    Preconditions.checkNotNull(workItemId);

    this.workflowApplicationService.removeWorkItemFromWorkflow(workflowId, workItemId);
  }

  @POST
  @Path("{id}/move")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response move(@PathParam("workflowId") final String workflowId, @PathParam("id") final String workItemId,
    @NotNull @Valid final WorkItemMoveInputModel input)
    throws WorkflowNotFoundException, WorkItemNotInProcessException, StageNotInProcessException {
    this.workflowApplicationService.moveWorkItemToStage(workflowId, workItemId, input.getTo());

    return Response.status(Status.OK)
      .build();
  }

  @POST
  @Path("{id}/position")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response putAtPosition(@PathParam("workflowId") final String workflowId, @PathParam("id") final String workItemId,

    @NotNull @Valid final WorkItemPositionInputModel input)
    throws WorkItemNotInStageException, WorkflowNotFoundException, WorkItemNotInProcessException {

    this.workflowApplicationService.moveWorkItemToPosition(workflowId, workItemId, input.getNewPosition());

    return Response.status(Status.OK)
      .build();
  }

  @POST
  @Path("{id}/archiving")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response archiving(@PathParam("workflowId") final String workflowId, @PathParam("id") final String workItemId,
    @NotNull @Valid final WorkItemArchivingInputModel input)
    throws WorkItemNotInProcessException, WorkflowNotFoundException, WorkItemNotArchivedException {

    if (input.isArchived()) {
      this.workflowApplicationService.archiveWorkItem(workflowId, workItemId);
    } else {
      this.workflowApplicationService.sendWorkItemBackToWorkflow(workflowId, workItemId);
    }

    return Response.status(Status.OK)
      .build();
  }
}
