package org.svomz.apps.koobz.board.ports.adapters.rest.resources;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
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

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.svomz.apps.koobz.board.ports.adapters.rest.models.StageInputModel;
import org.svomz.apps.koobz.board.domain.Board;
import org.svomz.apps.koobz.board.domain.BoardRepository;
import org.svomz.apps.koobz.board.domain.Stage;
import org.svomz.apps.koobz.board.domain.StageNotEmptyException;
import org.svomz.apps.koobz.board.domain.StageNotInProcessException;
import org.svomz.apps.koobz.board.domain.StageRepository;
import org.svomz.apps.koobz.board.infrastructure.domain.EntityNotFoundException;

import com.google.common.base.Preconditions;

@Component
@Path("/boards/{boardId}/stages")
@Transactional
public class StageResource {

  private BoardRepository boardRepository;
  private StageRepository stageRepository;
  
  @Inject
  public StageResource(final BoardRepository boardRepository, final StageRepository stageRepository) {
    Preconditions.checkNotNull(boardRepository);
    Preconditions.checkNotNull(stageRepository);
    
    this.boardRepository = boardRepository;
    this.stageRepository = stageRepository;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public List<Stage> getStages(@PathParam("boardId") final String boardId)
    throws EntityNotFoundException {
    Board board = this.boardRepository.findOrThrowException(boardId);
    return new ArrayList<>(board.getStages());
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response createStage(@PathParam("boardId") final String boardId, @NotNull @Valid final StageInputModel stageInputModel)

    throws EntityNotFoundException {
    Preconditions.checkNotNull(stageInputModel);

    Board board = this.boardRepository.findOrThrowException(boardId);
    Stage stage = new Stage(stageInputModel.getName());
    board.addStage(stage);
    
    return Response.status(Status.CREATED).entity(stage).build();
  }

  @PUT
  @Path("{stageId}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Stage updateStage(@PathParam("stageId") final String stageId,
      @NotNull @Valid final StageInputModel stageInputModel) throws EntityNotFoundException {
    Preconditions.checkNotNull(stageId);
    Preconditions.checkNotNull(stageInputModel);

    Stage stage = this.stageRepository.findOrThrowException(stageId);
    stage.setName(stageInputModel.getName());
    
    return stage;
  }

  @DELETE
  @Path("{stageId}")
  public void delete(@NotNull @PathParam("boardId") final String boardId, @NotNull @PathParam("stageId") final String stageId)
    throws StageNotInProcessException, StageNotEmptyException, EntityNotFoundException {
    Preconditions.checkNotNull(boardId);
    Preconditions.checkNotNull(stageId);

    Board board = this.boardRepository.findOrThrowException(boardId);
    Stage stage = this.stageRepository.findOrThrowException(stageId);
    board.removeStage(stage);
  }

}
