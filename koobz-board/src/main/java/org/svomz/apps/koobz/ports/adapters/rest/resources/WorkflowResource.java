package org.svomz.apps.koobz.ports.adapters.rest.resources;

import java.util.Optional;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
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
import org.svomz.apps.koobz.application.WorkflowQueryService;
import org.svomz.apps.koobz.application.WorkflowApplicationService;
import org.svomz.apps.koobz.application.WorkflowNotFoundException;
import org.svomz.apps.koobz.ports.adapters.rest.models.WorkflowInputModel;
import org.svomz.apps.koobz.ports.adapters.rest.models.WorkflowViewModel;
import org.svomz.apps.koobz.domain.model.Workflow;
import org.svomz.apps.koobz.domain.model.WorkflowRepository;

import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.base.Preconditions;

@Component
@Path("/workflows")
public class WorkflowResource {

  private final WorkflowApplicationService workflowApplicationService;

  private final WorkflowQueryService workflowQueryService;

  @Inject
  public WorkflowResource(final WorkflowRepository workflowRepository, final WorkflowApplicationService workflowApplicationService, final
  WorkflowQueryService workflowQueryService) {
    Preconditions.checkNotNull(workflowApplicationService);
    Preconditions.checkNotNull(workflowQueryService);

    this.workflowApplicationService = workflowApplicationService;
    this.workflowQueryService = workflowQueryService;
  }

  @GET
  @Path("{workflowId}")
  @Produces(MediaType.APPLICATION_JSON)
  @JsonView(WorkflowViewModel.FullView.class)
  public Response getWorkflow(@NotNull @PathParam("workflowId") final String workflowId)
    throws WorkflowNotFoundException {
    Preconditions.checkNotNull(workflowId);

    Optional<Workflow> optionalWorkflow = this.workflowQueryService.findBoard(workflowId);

    Response response;
    if (optionalWorkflow.isPresent()) {
      response = Response.status(Status.OK)
        .entity(new WorkflowViewModel(optionalWorkflow.get()))
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
  @JsonView(WorkflowViewModel.SimpleView.class)
  public Response createBoard(@NotNull @Valid final WorkflowInputModel workflowInputModel) {
    Preconditions.checkNotNull(workflowInputModel);

    Workflow workflow = this.workflowApplicationService.createWorkflow(workflowInputModel.getName());
    
    WorkflowViewModel viewModel = new WorkflowViewModel(workflow);
    return Response.status(Status.CREATED).entity(viewModel).build();
  }

  @PUT
  @Path("{workflowId}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @JsonView(WorkflowViewModel.SimpleView.class)
  @Transactional
  public Response updateBoard(@PathParam("workflowId") final String workflowId,
      @NotNull @Valid final WorkflowInputModel workflowInputModel)
    throws WorkflowNotFoundException {
    Preconditions.checkNotNull(workflowInputModel);

    this.workflowApplicationService.changeWorkflowName(workflowId, workflowInputModel.getName());

    return Response.status(Status.OK)
      .build();
  }

}
