package org.svomz.apps.kanban.application.resources;

import java.util.List;

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

import org.svomz.apps.kanban.application.models.StageInputModel;
import org.svomz.apps.kanban.domain.entities.Stage;
import org.svomz.apps.kanban.domain.entities.StageNotEmptyException;
import org.svomz.apps.kanban.domain.entities.StageNotInProcessException;
import org.svomz.apps.kanban.domain.services.KanbanService;
import org.svomz.commons.infrastructure.persistence.EntityNotFoundException;


@Path("/boards/{boardId}/stages")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class StageResource {
  
  private KanbanService kanbanService;
  private long boardId;

  public StageResource(final KanbanService kanbanService, @PathParam("boardId") final long boardId) throws EntityNotFoundException {
    Preconditions.checkNotNull(kanbanService, "No board service supplied.");
    
    this.kanbanService = kanbanService;
    this.boardId = boardId;
  }
  
  @GET
  public List<Stage> getStages() throws EntityNotFoundException {
    return this.kanbanService.getStages(this.boardId);
  }
  
  @POST
  public Response createStage(final StageInputModel stageInputModel) throws EntityNotFoundException {
    Preconditions.checkNotNull(stageInputModel);
    
    Stage persistedStage = this.kanbanService.addStageToBoard(this.boardId, stageInputModel.getName());
    return Response.status(Status.CREATED).entity(persistedStage).build();
  }
  
  @PUT
  @Path("{stageId}")
  public Stage updateStage(@PathParam("stageId") long stageId, final StageInputModel stageInputModel) throws EntityNotFoundException {
    Preconditions.checkNotNull(stageInputModel);
    
    return this.kanbanService.updateStage(stageId, stageInputModel.getName());
  }

  @DELETE
  @Path("{stageId}")
  public void delete(@PathParam("stageId") long stageId) throws EntityNotFoundException, StageNotInProcessException, StageNotEmptyException {
    this.kanbanService.removeStageFromBoard(this.boardId, stageId);
  }
  
}
