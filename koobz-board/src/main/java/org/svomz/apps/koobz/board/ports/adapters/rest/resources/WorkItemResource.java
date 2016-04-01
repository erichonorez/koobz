package org.svomz.apps.koobz.board.ports.adapters.rest.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
import org.svomz.apps.koobz.board.ports.adapters.rest.models.WorkItemInputModel;
import org.svomz.apps.koobz.board.ports.adapters.rest.models.WorkItemViewModel;
import org.svomz.apps.koobz.board.domain.model.Board;
import org.svomz.apps.koobz.board.domain.model.BoardRepository;
import org.svomz.apps.koobz.board.domain.model.Stage;
import org.svomz.apps.koobz.board.domain.model.StageNotInProcessException;
import org.svomz.apps.koobz.board.domain.model.StageRepository;
import org.svomz.apps.koobz.board.domain.model.WorkItem;
import org.svomz.apps.koobz.board.domain.model.WorkItemNotInStageException;
import org.svomz.apps.koobz.board.domain.model.WorkItemNotInProcessException;
import org.svomz.apps.koobz.board.domain.model.WorkItemRepository;
import org.svomz.apps.koobz.board.infrastructure.domain.EntityNotFoundException;

import com.google.common.base.Preconditions;

@Component
@Path("/boards/{boardId}/workitems")
@Transactional
public class WorkItemResource {

  private final BoardApplicationService boardApplicationService;

  private BoardRepository boardRepository;
  private StageRepository stageRepository;
  private WorkItemRepository workItemRepository;


  @Inject
  public WorkItemResource(
    final BoardRepository boardRepository,
    final StageRepository stageRepository,
    final WorkItemRepository workItemRepository,
    final BoardApplicationService boardApplicationService) {
    Preconditions.checkNotNull(boardRepository);
    Preconditions.checkNotNull(stageRepository);
    Preconditions.checkNotNull(workItemRepository);

    this.boardRepository = boardRepository;
    this.stageRepository = stageRepository;
    this.workItemRepository = workItemRepository;
    this.boardApplicationService = boardApplicationService;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public List<WorkItemViewModel> getWorkItems(@PathParam("boardId") final String boardId)
    throws EntityNotFoundException {
    Set<WorkItem> workItems = this.boardRepository.findOrThrowException(boardId).getWorkItems();
    
    List<WorkItemViewModel> workItemViewModels = new ArrayList<>();
    for (WorkItem workItem : workItems) {
      workItemViewModels.add(new WorkItemViewModel(workItem));
    }
    return workItemViewModels;
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response create(@NotNull @PathParam("boardId") final String boardId,
    @NotNull @Valid final WorkItemInputModel workItemInputModel)
    throws StageNotInProcessException, BoardNotFoundException {
    Preconditions.checkNotNull(boardId);
    Preconditions.checkNotNull(workItemInputModel);

    WorkItem workItem = this.boardApplicationService.createWorkItem(
      boardId,
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
  public WorkItemViewModel update(@PathParam("boardId") final String boardId, @PathParam("id") final String workItemId,
      @NotNull @Valid final WorkItemInputModel workItemInputModel) throws
                                                                   WorkItemNotInProcessException,
                                                                   StageNotInProcessException,
                                                                   WorkItemNotInStageException,
                                                                   EntityNotFoundException {
    Preconditions.checkNotNull(workItemInputModel);

    WorkItem workItem = this.workItemRepository.findOrThrowException(workItemId);
    if (!workItem.getTitle().equals(workItemInputModel.getTitle())) {
      workItem.setTitle(workItemInputModel.getTitle());
    }

    Stage stage = this.stageRepository.findByBoardIdAndStageId(boardId,
      workItemInputModel.getStageId());
    if (stage == null) {
      throw new EntityNotFoundException();
    }

    Board board = this.boardRepository.findOrThrowException(boardId);
    if (stage.getId() != workItem.getStage().getId()) {
      board.moveWorkItem(workItem, stage);
    }

    Integer order = workItemInputModel.getOrder();
    if (order != null && workItem.getOrder() != order) {
      board.reoderWorkItem(workItem, order);
    }

    String description = workItemInputModel.getDescription();
    if (description != null && !description.equals(workItem.getDescription())) {
      workItem.setDescription(description);
    }
    
    return new WorkItemViewModel(workItem);
  }

  @DELETE
  @Path("{id}")
  public void delete(@PathParam("boardId") final String boardId, @PathParam("id") final String workItemId)

    throws WorkItemNotInProcessException, EntityNotFoundException {
    Board board = this.boardRepository.findOrThrowException(boardId);
    WorkItem workItem = this.workItemRepository.findOrThrowException(workItemId);
    board.removeWorkItem(workItem);
  }

}
