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
import org.svomz.apps.koobz.board.application.BoardApplicationService;
import org.svomz.apps.koobz.board.application.BoardNotFoundException;
import org.svomz.apps.koobz.board.ports.adapters.rest.models.StageInputModel;
import org.svomz.apps.koobz.board.domain.model.Board;
import org.svomz.apps.koobz.board.domain.model.BoardRepository;
import org.svomz.apps.koobz.board.domain.model.Stage;
import org.svomz.apps.koobz.board.domain.model.StageNotEmptyException;
import org.svomz.apps.koobz.board.domain.model.StageNotInProcessException;
import org.svomz.apps.koobz.board.domain.model.StageRepository;
import org.svomz.apps.koobz.board.infrastructure.domain.EntityNotFoundException;

import com.google.common.base.Preconditions;

@Component
@Path("/boards/{boardId}/stages")
@Transactional
public class StageResource {

  private final BoardApplicationService boardApplicationService;
  private BoardRepository boardRepository;
  private StageRepository stageRepository;
  
  @Inject
  public StageResource(final BoardRepository boardRepository, final StageRepository stageRepository, final
    BoardApplicationService boardApplicationService) {
    Preconditions.checkNotNull(boardRepository);
    Preconditions.checkNotNull(stageRepository);
    Preconditions.checkNotNull(boardApplicationService);
    
    this.boardRepository = boardRepository;
    this.stageRepository = stageRepository;
    this.boardApplicationService = boardApplicationService;
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response createStage(
    @NotNull @PathParam("boardId") final String boardId,
    @NotNull @Valid final StageInputModel stageInputModel)
    throws BoardNotFoundException {
    Preconditions.checkNotNull(boardId);
    Preconditions.checkNotNull(stageInputModel);

    Stage stage = this.boardApplicationService.createStage(boardId, stageInputModel.getName());
    
    return Response.status(Status.CREATED).entity(stage).build();
  }

  @PUT
  @Path("{stageId}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response updateStage(@PathParam("boardId") final String boardId,
      @PathParam("stageId") final String stageId,
      @NotNull @Valid final StageInputModel stageInputModel)
    throws StageNotInProcessException, BoardNotFoundException {
    Preconditions.checkNotNull(stageId);
    Preconditions.checkNotNull(stageInputModel);

    this.boardApplicationService.changeStageName(boardId, stageId, stageInputModel.getName());
    
    return Response.status(Status.OK)
      .build();
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
