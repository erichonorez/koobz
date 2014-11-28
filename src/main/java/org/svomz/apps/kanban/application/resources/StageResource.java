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

import jersey.repackaged.com.google.common.base.Preconditions;

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
  public Stage createStage(Stage stage) throws EntityNotFoundException {
    Preconditions.checkNotNull(stage);
    
    return this.kanbanService.addStageToBoard(this.boardId, stage.getName());
  }
  
  @PUT
  @Path("{stageId}")
  public Stage updateStage(@PathParam("stageId") long stageId, Stage stage) throws EntityNotFoundException {
    Preconditions.checkArgument(stageId == stage.getId());
    
    return this.kanbanService.updateStage(stageId, stage.getName());
  }

  @DELETE
  @Path("{stageId}")
  public void delete(@PathParam("stageId") long stageId) throws EntityNotFoundException, StageNotInProcessException, StageNotEmptyException {
    this.kanbanService.removeStageFromBoard(this.boardId, stageId);
  }
  
}
