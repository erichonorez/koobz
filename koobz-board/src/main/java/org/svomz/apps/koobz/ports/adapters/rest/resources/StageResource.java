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
import org.svomz.apps.koobz.ports.adapters.rest.models.StageInputModel;
import org.svomz.apps.koobz.domain.model.Stage;
import org.svomz.apps.koobz.domain.model.StageNotEmptyException;
import org.svomz.apps.koobz.domain.model.StageNotInProcessException;

import com.google.common.base.Preconditions;

@Component
@Path("/workflows/{workflowId}/stages")
public class StageResource {

  private final WorkflowApplicationService workflowApplicationService;
  
  @Inject
  public StageResource(final WorkflowApplicationService workflowApplicationService) {
    Preconditions.checkNotNull(workflowApplicationService);

    this.workflowApplicationService = workflowApplicationService;
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response createStage(
    @NotNull @PathParam("workflowId") final String workflowId,
    @NotNull @Valid final StageInputModel stageInputModel)
    throws WorkflowNotFoundException {
    Preconditions.checkNotNull(workflowId);
    Preconditions.checkNotNull(stageInputModel);

    Stage stage = this.workflowApplicationService.addStageToWorkflow(workflowId, stageInputModel.getName());
    
    return Response.status(Status.CREATED).entity(stage).build();
  }

  @PUT
  @Path("{stageId}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response updateStage(@PathParam("workflowId") final String workflowId,
      @PathParam("stageId") final String stageId,
      @NotNull @Valid final StageInputModel stageInputModel)
    throws StageNotInProcessException, WorkflowNotFoundException {
    Preconditions.checkNotNull(stageId);
    Preconditions.checkNotNull(stageInputModel);

    this.workflowApplicationService.changeStageName(workflowId, stageId, stageInputModel.getName());
    
    return Response.status(Status.OK)
      .build();
  }

  @DELETE
  @Path("{stageId}")
  public void delete(@NotNull @PathParam("workflowId") final String workflowId, @NotNull @PathParam("stageId") final String stageId)
    throws StageNotInProcessException, StageNotEmptyException, WorkflowNotFoundException {
    Preconditions.checkNotNull(workflowId);
    Preconditions.checkNotNull(stageId);

    this.workflowApplicationService.removeStageFromWorkflow(workflowId, stageId);
  }

}
