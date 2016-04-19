package org.svomz.apps.koobz.application;

import com.google.common.base.Preconditions;

import org.springframework.stereotype.Service;
import org.svomz.apps.koobz.domain.model.Workflow;
import org.svomz.apps.koobz.domain.model.WorkflowRepository;
import org.svomz.apps.koobz.domain.model.Stage;
import org.svomz.apps.koobz.domain.model.StageNotEmptyException;
import org.svomz.apps.koobz.domain.model.StageNotInProcessException;
import org.svomz.apps.koobz.domain.model.WorkItem;
import org.svomz.apps.koobz.domain.model.WorkItemNotArchivedException;
import org.svomz.apps.koobz.domain.model.WorkItemNotInProcessException;
import org.svomz.apps.koobz.domain.model.WorkItemNotInStageException;


import java.util.Optional;

import javax.inject.Inject;
import javax.transaction.Transactional;

@Service
public class WorkflowApplicationService {

  private final WorkflowRepository workflowRepository;
  private final WorkflowIdentityService workflowIdentityService;

  @Inject
  public WorkflowApplicationService(final WorkflowRepository aWorkflowRepository,
    WorkflowIdentityService aWorkflowIdentityService) {
    this.workflowRepository = Preconditions.checkNotNull(aWorkflowRepository);
    this.workflowIdentityService = Preconditions.checkNotNull(aWorkflowIdentityService);
  }

  @Transactional
  public Workflow createWorkflow(final String aWorkflowName) {
    Preconditions.checkNotNull(aWorkflowName);

    Workflow workflow = new Workflow(
      this.workflowIdentityService().nextBoardIdentity(),
      aWorkflowName
    );
    this.workflowRepository().save(workflow);
    return workflow;
  }

  @Transactional
  public void changeWorkflowName(final String workflowId, final String newWorkflowName)
    throws WorkflowNotFoundException {
    Preconditions.checkNotNull(workflowId);
    Preconditions.checkNotNull(newWorkflowName);

    Workflow workflow = this.existingWorkflowOfId(workflowId);
    workflow.setName(newWorkflowName);
  }

  @Transactional
  public Stage addStageToWorkflow(final String aWorkflowId, final String aStageTitle) throws
                                                                                WorkflowNotFoundException {
    Preconditions.checkNotNull(aWorkflowId);
    Preconditions.checkNotNull(aStageTitle);

    Workflow workflow = this.existingWorkflowOfId(aWorkflowId);
    Stage stage = workflow.addStageToWorkflow(
      this.workflowIdentityService.nextStageIdentity(),
      aStageTitle
    );

    return stage;
  }

  @Transactional
  public void changeStageName(final String aWorkflowId, final String aStageId, final String newStageName)
    throws WorkflowNotFoundException, StageNotInProcessException {
    Preconditions.checkNotNull(aWorkflowId);
    Preconditions.checkNotNull(aStageId);
    Preconditions.checkNotNull(newStageName);

    Workflow workflow = this.existingWorkflowOfId(aWorkflowId);
    Optional<Stage> optionalStage = workflow.stageOfId(aStageId);
    if (!optionalStage.isPresent()) {
      throw new StageNotInProcessException();
    }

    Stage stage = optionalStage.get();
    stage.setName(newStageName);
  }

  @Transactional
  public void removeStageFromWorkflow(final String workflowId, final String aStageId)
    throws WorkflowNotFoundException, StageNotInProcessException, StageNotEmptyException {
    Preconditions.checkNotNull(workflowId);
    Preconditions.checkNotNull(aStageId);

    Workflow workflow = this.existingWorkflowOfId(workflowId);
    workflow.removeStageWithId(aStageId);
  }

  @Transactional
  public WorkItem addWorkItemToWorkflow(final String workflowId, final String stageId, final String aWorkItemTitle,
    final String aWorkItemDescription) throws WorkflowNotFoundException, StageNotInProcessException {
    Preconditions.checkNotNull(workflowId);
    Preconditions.checkNotNull(stageId);
    Preconditions.checkNotNull(aWorkItemDescription);

    Workflow workflow = this.existingWorkflowOfId(workflowId);
    return workflow.addWorkItemToStage(
      stageId,
      this.workflowIdentityService().nextWorkItemIdentity(),
      aWorkItemTitle,
      aWorkItemDescription
    );
  }

  @Transactional
  public void changeWorkItemInformation(final String workflowId,final String workItemId,
    final String newWorkItemTitle, final String newWorkItemDescription)
    throws WorkflowNotFoundException, WorkItemNotInProcessException {
    Preconditions.checkNotNull(workflowId);
    Preconditions.checkNotNull(workItemId);
    Preconditions.checkNotNull(newWorkItemTitle);
    Preconditions.checkNotNull(newWorkItemDescription);

    Workflow workflow = this.existingWorkflowOfId(workflowId);
    Optional<WorkItem> optionalWorkItem = workflow.workItemOfId(workItemId);
    if (!optionalWorkItem.isPresent()) {
      throw new WorkItemNotInProcessException();
    }

    WorkItem workItem = optionalWorkItem.get();
    workItem.setTitle(newWorkItemTitle);
    workItem.setDescription(newWorkItemDescription);
  }

  @Transactional
  public void removeWorkItemFromWorkflow(final String boardId, final String workItemId)
    throws WorkflowNotFoundException, WorkItemNotInProcessException {
    Preconditions.checkNotNull(boardId);
    Preconditions.checkNotNull(workItemId);

    Workflow workflow = this.existingWorkflowOfId(boardId);
    workflow.removeWorkItemWithId(workItemId);
  }

  @Transactional
  public void moveWorkItemToStage(final String boardId, final String aWorkItemId, final String aStageId)
    throws WorkflowNotFoundException, WorkItemNotInProcessException, StageNotInProcessException {
    Preconditions.checkNotNull(boardId);
    Preconditions.checkNotNull(aWorkItemId);

    Workflow workflow = this.existingWorkflowOfId(boardId);
    workflow.moveWorkItemWithIdToStageWithId(aWorkItemId, aStageId);
  }

  @Transactional
  public void changeWorkItemPriority(final String boardId, final String workItemId, int newPriority)
    throws WorkflowNotFoundException, WorkItemNotInProcessException, WorkItemNotInStageException {
    Preconditions.checkNotNull(boardId);
    Preconditions.checkNotNull(workItemId);

    Workflow workflow = this.existingWorkflowOfId(boardId);
    workflow.changePriorityOfWorkItemWithId(workItemId, newPriority);
  }

  @Transactional
  public void archiveWorkItem(final String boardId, final String workItemId)
    throws WorkflowNotFoundException, WorkItemNotInProcessException {
    Preconditions.checkNotNull(boardId);
    Preconditions.checkNotNull(workItemId);

    Workflow workflow = this.existingWorkflowOfId(boardId);
    workflow.archiveWorkItemWithId(workItemId);
  }

  @Transactional
  public void sendWorkItemBackToWorkflow(final String boardId, final String workItemId)
    throws WorkflowNotFoundException, WorkItemNotArchivedException {
    Preconditions.checkNotNull(boardId);
    Preconditions.checkNotNull(workItemId);

    Workflow workflow = this.existingWorkflowOfId(boardId);
    workflow.sendBackToWorkflowWorkItemWithId(workItemId);
  }

  private Workflow existingWorkflowOfId(String boardId) throws WorkflowNotFoundException {
    Workflow workflow = this.workflowRepository().findOne(boardId);
    if (workflow == null) {
      throw new WorkflowNotFoundException(boardId);
    }
    return workflow;
  }

  private WorkflowRepository workflowRepository() {
    return workflowRepository;
  }

  private WorkflowIdentityService workflowIdentityService() {
    return this.workflowIdentityService;
  }

}
