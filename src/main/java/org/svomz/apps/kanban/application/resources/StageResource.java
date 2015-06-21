package org.svomz.apps.kanban.application.resources;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
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

import org.svomz.apps.kanban.application.models.StageInputModel;
import org.svomz.apps.kanban.domain.Board;
import org.svomz.apps.kanban.domain.BoardRepository;
import org.svomz.apps.kanban.domain.Stage;
import org.svomz.apps.kanban.domain.StageNotEmptyException;
import org.svomz.apps.kanban.domain.StageNotInProcessException;
import org.svomz.apps.kanban.domain.StageRepository;
import org.svomz.apps.kanban.infrastructure.domain.EntityNotFoundException;

import com.google.common.base.Preconditions;

@RequestScoped
@Transactional
@Path("/boards/{boardId}/stages")
public class StageResource {

  private BoardRepository boardRepository;
  private StageRepository stageRepository;
  
  public StageResource() {}
  
  @Inject
  public StageResource(final BoardRepository boardRepository, final StageRepository stageRepository) {
    Preconditions.checkNotNull(boardRepository);
    Preconditions.checkNotNull(stageRepository);
    
    this.boardRepository = boardRepository;
    this.stageRepository = stageRepository;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public List<Stage> getStages(@PathParam("boardId") final long boardId) throws EntityNotFoundException {
    Board board = this.boardRepository.find(boardId);
    return new ArrayList<>(board.getStages());
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response createStage(@PathParam("boardId") final long boardId, @NotNull @Valid final StageInputModel stageInputModel) throws EntityNotFoundException {
    Preconditions.checkNotNull(stageInputModel);

    Board board = this.boardRepository.find(boardId);
    Stage stage = new Stage(stageInputModel.getName());
    board.addStage(stage);
    
    return Response.status(Status.CREATED).entity(stage).build();
  }

  @PUT
  @Path("{stageId}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Stage updateStage(@PathParam("stageId") final long stageId,
      @NotNull @Valid final StageInputModel stageInputModel) throws EntityNotFoundException {
    Preconditions.checkNotNull(stageInputModel);

    Stage stage = this.stageRepository.find(stageId);
    stage.setName(stageInputModel.getName());
    
    return stage;
  }

  @DELETE
  @Path("{stageId}")
  public void delete(@PathParam("boardId") final long boardId, @PathParam("stageId") final long stageId) throws EntityNotFoundException,
      StageNotInProcessException, StageNotEmptyException {
    Board board = this.boardRepository.find(boardId);
    Stage stage = this.stageRepository.find(stageId);
    board.removeStage(stage);
  }

}
