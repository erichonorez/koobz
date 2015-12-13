package org.svomz.apps.kanban.application.resources;

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
import org.svomz.apps.kanban.application.models.WorkItemInputModel;
import org.svomz.apps.kanban.application.models.WorkItemViewModel;
import org.svomz.apps.kanban.domain.Board;
import org.svomz.apps.kanban.domain.BoardRepository;
import org.svomz.apps.kanban.domain.Stage;
import org.svomz.apps.kanban.domain.StageNotInProcessException;
import org.svomz.apps.kanban.domain.StageRepository;
import org.svomz.apps.kanban.domain.WorkItem;
import org.svomz.apps.kanban.domain.WorkItemNotInStageException;
import org.svomz.apps.kanban.domain.WorkItemNotInProcessException;
import org.svomz.apps.kanban.domain.WorkItemRepository;
import org.svomz.apps.kanban.infrastructure.domain.EntityNotFoundException;

import com.google.common.base.Preconditions;

@Component
@Path("/boards/{boardId}/workitems")
@Transactional
public class WorkItemResource {

  private BoardRepository boardRepository;
  private StageRepository stageRepository;
  private WorkItemRepository workItemRepository;


  @Inject
  public WorkItemResource(final BoardRepository boardRepository,final StageRepository stageRepository, final WorkItemRepository workItemRepository) {
    Preconditions.checkNotNull(boardRepository);
    Preconditions.checkNotNull(stageRepository);
    Preconditions.checkNotNull(workItemRepository);

    this.boardRepository = boardRepository;
    this.stageRepository = stageRepository;
    this.workItemRepository = workItemRepository;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public List<WorkItemViewModel> getWorkItems(@PathParam("boardId") final long boardId)
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
  public Response create(@PathParam("boardId") final long boardId, @NotNull @Valid final WorkItemInputModel workItemInputModel)

    throws StageNotInProcessException, EntityNotFoundException {
    Preconditions.checkNotNull(workItemInputModel);

    Board board = this.boardRepository.findOrThrowException(boardId);
    Stage stage = this.stageRepository.findOrThrowException(workItemInputModel.getStageId());
    WorkItem workItem = new WorkItem(workItemInputModel.getText());
    board.addWorkItem(workItem, stage);

    return Response.status(Status.CREATED).entity(new WorkItemViewModel(workItem)).build();
  }

  @PUT
  @Path("{id}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public WorkItemViewModel update(@PathParam("boardId") final long boardId, @PathParam("id") final long workItemId,
      @NotNull @Valid final WorkItemInputModel workItemInputModel) throws
                                                                   WorkItemNotInProcessException,
                                                                   StageNotInProcessException,
                                                                   WorkItemNotInStageException,
                                                                   EntityNotFoundException {
    Preconditions.checkNotNull(workItemInputModel);

    WorkItem workItem = this.workItemRepository.findOrThrowException(workItemId);
    if (!workItem.getText().equals(workItemInputModel.getText())) {
      workItem.setText(workItemInputModel.getText());
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
    
    if (workItemInputModel.getOrder() != null && workItem.getOrder() != workItemInputModel.getOrder()) {
      board.reoderWorkItem(workItem, workItemInputModel.getOrder());
    }
    
    return new WorkItemViewModel(workItem);
  }

  @DELETE
  @Path("{id}")
  public void delete(@PathParam("boardId") final long boardId, @PathParam("id") final long workItemId)

    throws WorkItemNotInProcessException, EntityNotFoundException {
    Board board = this.boardRepository.findOrThrowException(boardId);
    WorkItem workItem = this.workItemRepository.findOrThrowException(workItemId);
    board.removeWorkItem(workItem);
  }

}
