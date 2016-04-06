package org.svomz.apps.koobz.applications;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.svomz.apps.koobz.application.WorkflowIdentityService;
import org.svomz.apps.koobz.application.WorkflowApplicationService;
import org.svomz.apps.koobz.application.WorkflowNotFoundException;
import org.svomz.apps.koobz.domain.model.Workflow;
import org.svomz.apps.koobz.domain.model.WorkflowRepository;
import org.svomz.apps.koobz.domain.model.Stage;
import org.svomz.apps.koobz.domain.model.StageNotEmptyException;
import org.svomz.apps.koobz.domain.model.StageNotInProcessException;
import org.svomz.apps.koobz.domain.model.WorkItem;
import org.svomz.apps.koobz.domain.model.WorkItemNotArchivedException;
import org.svomz.apps.koobz.domain.model.WorkItemNotInProcessException;
import org.svomz.apps.koobz.domain.model.WorkItemNotInStageException;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(Suite.class)
@Suite.SuiteClasses({
  WorkflowApplicationServiceUnitTest.CreateWorkflow.class,
  WorkflowApplicationServiceUnitTest.AddStageToWorkflow.class,
  WorkflowApplicationServiceUnitTest.AddWorkItemToWorkflow.class,
  WorkflowApplicationServiceUnitTest.MoveWorkItemToStage.class,
  WorkflowApplicationServiceUnitTest.MoveWorkItemToPosition.class,
  WorkflowApplicationServiceUnitTest.ArchiveWorkItem.class,
  WorkflowApplicationServiceUnitTest.ChangeWorkflowName.class,
  WorkflowApplicationServiceUnitTest.ChangeStageName.class,
  WorkflowApplicationServiceUnitTest.RemoveStageFromWorkflow.class,
  WorkflowApplicationServiceUnitTest.ChangeWorkItemInformation.class,
  WorkflowApplicationServiceUnitTest.RemoveWorkItemFromBoard.class,
  WorkflowApplicationServiceUnitTest.SendWorkItemBackToWorkflow.class
})
public class WorkflowApplicationServiceUnitTest {

  public static class CreateWorkflow {

    @Test
    public void itShouldCreateANewWorkflowSuccessfully() {
      WorkflowRepository workflowRepository = mock(WorkflowRepository.class);
      WorkflowIdentityService workflowIdentityService = mock(WorkflowIdentityService.class);
      when(workflowIdentityService.nextBoardIdentity()).thenReturn(UUID.randomUUID().toString());

      WorkflowApplicationService workflowApplicationService = new WorkflowApplicationService(workflowRepository,
        workflowIdentityService);

      String aBoardName = "A name";
      Workflow workflow = workflowApplicationService.createWorkflow(aBoardName);

      assertThat(workflow).isNotNull();
      assertThat(workflow.getName()).isEqualTo(aBoardName);
      assertThat(workflow.workItems()).isEmpty();
      assertThat(workflow.stages()).isEmpty();
      assertThat(workflow.getId()).isNotNull();
    }

  }

  public static class ChangeWorkflowName {

    @Test
    public void itShouldSuccessfullyChangeTheNameOfABoard() throws WorkflowNotFoundException {
      // Given a workflow with id "35a45cd4-f81f-11e5-9ce9-5e5517507c66" and name ",World! Hello"
      String workflowId = "35a45cd4-f81f-11e5-9ce9-5e5517507c66";
      String workflowName = ",World! Hello";

      WorkflowRepository workflowRepository = mock(WorkflowRepository.class);
      Workflow workflow = new Workflow(workflowId, workflowName);
      when(workflowRepository.findOne(workflowId)).thenReturn(workflow);

      // When I update the workflow name with "Hello, World!"
      String newBoardName = "Hello, World!";

      WorkflowIdentityService workflowIdentityService = mock(WorkflowIdentityService.class);
      WorkflowApplicationService workflowApplicationService = new WorkflowApplicationService(
        workflowRepository,
        workflowIdentityService);

      workflowApplicationService.changeWorkflowName(workflowId, newBoardName);

      // Then the name is updated
      assertThat(workflow.getName()).isEqualTo(newBoardName);
    }

  }

  public static class AddStageToWorkflow {

    @Test
    public void itShouldSuccessfullyCreateANewStage() throws WorkflowNotFoundException {
      // Given a workflow with id "35a45cd4-f81f-11e5-9ce9-5e5517507c66"
      String workflowId = "35a45cd4-f81f-11e5-9ce9-5e5517507c66";

      WorkflowRepository workflowRepository = mock(WorkflowRepository.class);
      Workflow workflow = new Workflow(workflowId, "a workflow");
      when(workflowRepository.findOne(workflowId)).thenReturn(workflow);

      // When I create a new stage on workflow with id "35a45cd4-f81f-11e5-9ce9-5e5517507c66"
      // And with "to do" as title
      String title = "to do";

      WorkflowIdentityService workflowIdentityService = mock(WorkflowIdentityService.class);
      when(workflowIdentityService.nextStageIdentity()).thenReturn(UUID.randomUUID().toString());

      WorkflowApplicationService workflowApplicationService = new WorkflowApplicationService(
        workflowRepository,
        workflowIdentityService);

      Stage stage = workflowApplicationService.addStageToWorkflow(workflowId, title);

      // Then the workflow has a new stage

      assertThat(workflow.stages()).contains(stage);
    }

    @Test(expected = WorkflowNotFoundException.class)
    public void itShouldThrowBoardNotFoundExceptionIfBoardDoesNotExist()
      throws WorkflowNotFoundException {
      // Given the workflow with id "35a45cd4-f81f-11e5-9ce9-5e5517507c66" does not exist
      String workflowId = "35a45cd4-f81f-11e5-9ce9-5e5517507c66";

      WorkflowRepository workflowRepository = mock(WorkflowRepository.class);
      when(workflowRepository.findOne(workflowId)).thenReturn(null);

      WorkflowIdentityService workflowIdentityService = mock(WorkflowIdentityService.class);

      WorkflowApplicationService workflowApplicationService = new WorkflowApplicationService(
        workflowRepository,
        workflowIdentityService);

      // When I create a new stage on workflow with id "35a45cd4-f81f-11e5-9ce9-5e5517507c66"
      // And with "to do" as title
      String title = "to do";

      Stage stage = workflowApplicationService.addStageToWorkflow(workflowId, title);

      // Then I get a BoardNotFoundException
    }

  }

  public static class ChangeStageName {

    @Test
    public void itShouldSuccessfullyChangeTheNameOfAStage()
      throws StageNotInProcessException, WorkflowNotFoundException {
      // Given a workflow with id "35a45cd4-f81f-11e5-9ce9-5e5517507c66"
      String workflowId = "35a45cd4-f81f-11e5-9ce9-5e5517507c66";
      Workflow workflow = new Workflow(workflowId, "a workflow");
      // And with a stage having id "36a45cd4-f81f-11e5-9ce9-5e5517507c67" and name "do To"
      String aStageId = "36a45cd4-f81f-11e5-9ce9-5e5517507c67";
      String aStageName = "do To";

      Stage stage = workflow.addStageToWorkflow(
        aStageId,
        aStageName
      );

      WorkflowRepository workflowRepository = mock(WorkflowRepository.class);
      when(workflowRepository.findOne(workflowId)).thenReturn(workflow);

      // When I update the workflow name with "To do"
      WorkflowIdentityService workflowIdentityService = mock(WorkflowIdentityService.class);
      WorkflowApplicationService workflowApplicationService = new WorkflowApplicationService(
        workflowRepository,
        workflowIdentityService);

      String newStageName = "To do";
      workflowApplicationService.changeStageName(workflowId, aStageId, newStageName);

      // Then the stage name is equal to "To do"
      assertThat(stage.getName()).isEqualTo(newStageName);
    }

  }

  public static class RemoveStageFromWorkflow {

    @Test
    public void itShouldSuccessfullyRemoveAnEmptyStage()
      throws WorkflowNotFoundException, StageNotEmptyException, StageNotInProcessException {
      // Given a workflow with id "35a45cd4-f81f-11e5-9ce9-5e5517507c66"
      String workflowId = "35a45cd4-f81f-11e5-9ce9-5e5517507c66";
      Workflow workflow = new Workflow(workflowId, "a workflow");
      // And with a stage having id "36a45cd4-f81f-11e5-9ce9-5e5517507c67"
      String aStageId = "36a45cd4-f81f-11e5-9ce9-5e5517507c67";
      String aStageName = "do To";

      Stage stage = workflow.addStageToWorkflow(aStageId, aStageName);

      WorkflowRepository workflowRepository = mock(WorkflowRepository.class);
      when(workflowRepository.findOne(workflowId)).thenReturn(workflow);

      // When I delete the stage
      WorkflowIdentityService workflowIdentityService = mock(WorkflowIdentityService.class);
      WorkflowApplicationService workflowApplicationService = new WorkflowApplicationService(
        workflowRepository,
        workflowIdentityService);

      String newStageName = "To do";
      workflowApplicationService.removeStageFromWorkflow(workflowId, aStageId);

      // Then workflow does not contains the stage any more
      assertThat(workflow.stages()).doesNotContain(stage);
    }

  }

  public static class AddWorkItemToWorkflow {

    @Test
    public void itShouldSuccessfullyCreateAWorkItem()
      throws StageNotInProcessException, WorkflowNotFoundException {
      // Given a workflow with id "35a45cd4-f81f-11e5-9ce9-5e5517507c66" and title "a workflow"
      String workflowId = "35a45cd4-f81f-11e5-9ce9-5e5517507c66";
      String aBoardName = "a workflow";
      // And having a Stage with id "ac329010-f837-11e5-9ce9-5e5517507c66" and name "to do"
      String aStageId = "35a45cd4-f81f-11e5-9ce9-5e5517507c66";
      String aStageName = "to do";

      Workflow workflow = new Workflow(workflowId, aBoardName);
      Stage stage = workflow.addStageToWorkflow(aStageId, aStageName);

      WorkflowRepository workflowRepository = mock(WorkflowRepository.class);
      when(workflowRepository.findOne(workflowId)).thenReturn(workflow);

      // When user adds a Work Item with title "Drink coffee" to the stage with id "ac329010-f837-11e5-9ce9-5e5517507c66"
      String aWorkItemTitle = "Drink coffee";
      String aWorkItemDescription = "At Starbuck";

      WorkflowIdentityService workflowIdentityService = mock(WorkflowIdentityService.class);
      when(workflowIdentityService.nextWorkItemIdentity()).thenReturn(UUID.randomUUID().toString());

      WorkflowApplicationService workflowApplicationService = new WorkflowApplicationService(
        workflowRepository,
        workflowIdentityService);

      WorkItem workItem = workflowApplicationService.addWorkItemToWorkflow(
        workflowId,
        aStageId,
        aWorkItemTitle,
        aWorkItemDescription
      );

      // Then the workflow contains the work item
      assertThat(workflow.workItems()).contains(workItem);
      assertThat(workItem.getTitle()).isEqualTo(aWorkItemTitle);
      assertThat(workItem.getDescription()).isEqualTo(aWorkItemDescription);
    }

    @Test(expected = WorkflowNotFoundException.class)
    public void itShouldThrowBoardNotFoundExceptionIfBoardDoesNotExist()
      throws StageNotInProcessException, WorkflowNotFoundException {
      // Given the workflow with id "35a45cd4-f81f-11e5-9ce9-5e5517507c66" does not exist
      String workflowId = "35a45cd4-f81f-11e5-9ce9-5e5517507c66";
      String stageId = "35a45cd4-f81f-11e5-9ce9-5e5517507c66";

      WorkflowRepository workflowRepository = mock(WorkflowRepository.class);
      when(workflowRepository.findOne(workflowId)).thenReturn(null);

      WorkflowIdentityService workflowIdentityService = mock(WorkflowIdentityService.class);
      WorkflowApplicationService workflowApplicationService = new WorkflowApplicationService(
        workflowRepository,
        workflowIdentityService);

      // When user adds a Work Item with title "Drink coffee" to the stage with id "ac329010-f837-11e5-9ce9-5e5517507c66"
      String aWorkItemTitle = "Drink coffee";
      String aWorkItemDescription = "At Starbuck";

      WorkItem workItem = workflowApplicationService.addWorkItemToWorkflow(
        workflowId,
        stageId,
        aWorkItemTitle,
        aWorkItemDescription
      );

      // Then I get a BoardNotFoundException
    }

    @Test(expected = StageNotInProcessException.class)
    public void itShouldThrowStageNotInProcessExceptionIfStageDoesNotExist()
      throws StageNotInProcessException, WorkflowNotFoundException {
      // Given a workflow with id "35a45cd4-f81f-11e5-9ce9-5e5517507c66" and title "a workflow"
      String workflowId = "35a45cd4-f81f-11e5-9ce9-5e5517507c66";
      String aBoardName = "a workflow";
      // And not having a Stage with id "ac329010-f837-11e5-9ce9-5e5517507c66"
      String stageId = "35a45cd4-f81f-11e5-9ce9-5e5517507c66";

      Workflow workflow = new Workflow(workflowId, aBoardName);

      WorkflowRepository workflowRepository = mock(WorkflowRepository.class);
      when(workflowRepository.findOne(workflowId)).thenReturn(workflow);

      // When user adds a Work Item with title "Drink coffee" to the stage with id "ac329010-f837-11e5-9ce9-5e5517507c66"
      String aWorkItemTitle = "Drink coffee";
      String aWorkItemDescription = "At Starbuck";

      WorkflowIdentityService workflowIdentityService = mock(WorkflowIdentityService.class);
      when(workflowIdentityService.nextWorkItemIdentity()).thenReturn(UUID.randomUUID().toString());
      WorkflowApplicationService workflowApplicationService = new WorkflowApplicationService(
        workflowRepository,
        workflowIdentityService);

      WorkItem workItem = workflowApplicationService.addWorkItemToWorkflow(
        workflowId,
        stageId,
        aWorkItemTitle,
        aWorkItemDescription
      );

      // Then I get a StageNotInProcessException
    }

  }

  public static class ChangeWorkItemInformation {

    @Test
    public void itShouldSuccessfullyUpdateWorkItemTitleAndDescription()
      throws StageNotInProcessException, WorkItemNotInProcessException, WorkflowNotFoundException {
      // Given a workflow with a stage having a work item A
      String workflowId = "35a45cd4-f81f-11e5-9ce9-5e5517507c66";
      String aBoardName = "a workflow";
      Workflow workflow = new Workflow(workflowId, aBoardName);

      String aStageId = "c7c66e8a-610d-40f5-a8b6-455fad0928f6";
      String aStageName = "to do";
      Stage stage = workflow.addStageToWorkflow(aStageId, aStageName);

      String workItemTitle = "A";
      String workItemDescription = "A desc";
      String workItemId = "09021d01-3da9-4584-85c0-85211cfa8467";
      WorkItem workItem = workflow
        .addWorkItemToStage(aStageId, workItemId, workItemTitle, workItemDescription);

      WorkflowRepository workflowRepository = mock(WorkflowRepository.class);
      when(workflowRepository.findOne(workflowId)).thenReturn(workflow);

      // When I update the work item title with "Ticket 42" and description "Bla bla bla"
      String newWorkItemName = "Ticket 42";
      String newWorkItemDescription = "Bla bla bla";

      WorkflowIdentityService workflowIdentityService = mock(WorkflowIdentityService.class);
      WorkflowApplicationService workflowApplicationService = new WorkflowApplicationService(
        workflowRepository,
        workflowIdentityService);

      workflowApplicationService
        .changeWorkItemInformation(workflowId, workItemId, newWorkItemName, newWorkItemDescription);

      // Then work item name is "Ticket 42" and its description is "Bla bla bla".
      assertThat(workflow.workItems()).contains(workItem);
    }

  }

  public static class RemoveWorkItemFromBoard {

    @Test
    public void itShouldSuccessfullyRemoveWorkItemFromBoard()
      throws StageNotInProcessException, WorkItemNotInProcessException, WorkflowNotFoundException {
      // Given a workflow with a stage having a work item A
      String workflowId = "35a45cd4-f81f-11e5-9ce9-5e5517507c66";
      String aBoardName = "a workflow";
      Workflow workflow = new Workflow(workflowId, aBoardName);

      String aStageId = "c7c66e8a-610d-40f5-a8b6-455fad0928f6";
      String aStageName = "to do";
      Stage stage = workflow.addStageToWorkflow(aStageId, aStageName);

      String workItemTitle = "A";
      String workItemDescription = "A desc";
      String workItemId = "09021d01-3da9-4584-85c0-85211cfa8467";
      WorkItem workItem = workflow
        .addWorkItemToStage(aStageId, workItemId, workItemTitle, workItemDescription);

      WorkflowRepository workflowRepository = mock(WorkflowRepository.class);
      when(workflowRepository.findOne(workflowId)).thenReturn(workflow);

      // When I delete the work item
      WorkflowIdentityService workflowIdentityService = mock(WorkflowIdentityService.class);
      WorkflowApplicationService workflowApplicationService = new WorkflowApplicationService(
        workflowRepository,
        workflowIdentityService);

      workflowApplicationService.removeWorkItemFromWorkflow(workflowId, workItemId);

      // Then the workflow does not contains the work item anymore.
      assertThat(workflow.workItems()).doesNotContain(workItem);
    }

  }

  public static class MoveWorkItemToStage {

    @Test
    public void itShouldSuccessfullyMoveWorkItemToStage()
      throws StageNotInProcessException, WorkflowNotFoundException, WorkItemNotInProcessException {
      // Given a workflow having two stages and a work item in the first one
      String workflowId = "35a45cd4-f81f-11e5-9ce9-5e5517507c66";
      String aBoardName = "a workflow";
      Workflow workflow = new Workflow(workflowId, aBoardName);

      String stageAId = "c7c66e8a-610d-40f5-a8b6-455fad0928f6";
      String stageAName = "to do";
      Stage stageA = workflow.addStageToWorkflow(stageAId, stageAName);

      String stageBId = "7d1eee25-5d9e-4a95-98b0-032a17960a7a";
      String stageBName = "done";
      Stage stageB = workflow.addStageToWorkflow(stageBId, stageBName);

      String aWorkItemTitle = "Drink coffee";
      String aWorkItemDescription = "At Starbuck";
      String aWorkItemId = "09021d01-3da9-4584-85c0-85211cfa8467";
      WorkItem workItem = workflow
        .addWorkItemToStage(stageAId, aWorkItemId, aWorkItemTitle, aWorkItemDescription);

      WorkflowRepository workflowRepository = mock(WorkflowRepository.class);
      when(workflowRepository.findOne(workflowId)).thenReturn(workflow);

      // When I move the work item from stage A to stage B
      WorkflowIdentityService workflowIdentityService = mock(WorkflowIdentityService.class);
      WorkflowApplicationService workflowApplicationService = new WorkflowApplicationService(
        workflowRepository,
        workflowIdentityService);

      workflowApplicationService.moveWorkItemToStage(workflowId, aWorkItemId, stageBId);

      // Then the work item is in stage B
      assertThat(workflow.workItemsInStage(stageBId)).contains(workItem);
      // And the work item is no more in stage A
      assertThat(workflow.workItemsInStage(stageAId)).doesNotContain(workItem);
    }

    @Test(expected = StageNotInProcessException.class)
    public void itShouldFailIfTheBoardDoesNotHaveTheSpecifiedStage()
      throws WorkflowNotFoundException, WorkItemNotInProcessException, StageNotInProcessException {
      // Given a workflow having two stages and a work item in the first one
      String workflowId = "35a45cd4-f81f-11e5-9ce9-5e5517507c66";
      String aBoardName = "a workflow";
      Workflow workflow = new Workflow(workflowId, aBoardName);

      String stageAId = "c7c66e8a-610d-40f5-a8b6-455fad0928f6";
      String stageAName = "to do";
      Stage stageA = workflow.addStageToWorkflow(stageAId, stageAName);

      String stageBId = "7d1eee25-5d9e-4a95-98b0-032a17960a7a";
      String stageBName = "done";
      Stage stageB = workflow.addStageToWorkflow(stageBId, stageBName);

      String aWorkItemTitle = "Drink coffee";
      String aWorkItemDescription = "At Starbuck";
      String aWorkItemId = "09021d01-3da9-4584-85c0-85211cfa8467";
      WorkItem workItem = workflow
        .addWorkItemToStage(stageAId, aWorkItemId, aWorkItemTitle, aWorkItemDescription);

      WorkflowRepository workflowRepository = mock(WorkflowRepository.class);
      when(workflowRepository.findOne(workflowId)).thenReturn(workflow);

      // When I move the work item from stage A to a stage with id "d1a947d2-93b6-4d9a-be8e-b35c47f085ff"
      String stageCId = "d1a947d2-93b6-4d9a-be8e-b35c47f085ff";

      WorkflowIdentityService workflowIdentityService = mock(WorkflowIdentityService.class);
      WorkflowApplicationService workflowApplicationService = new WorkflowApplicationService(
        workflowRepository,
        workflowIdentityService);
      workflowApplicationService.moveWorkItemToStage(workflowId, aWorkItemId, stageCId);

      // Then it should fail
    }

    @Test(expected = WorkItemNotInProcessException.class)
    public void itShouldFailIfTheBoardDoesNotHaveTheSpecifiedWorkItem()
      throws StageNotInProcessException, WorkItemNotInProcessException, WorkflowNotFoundException {
      // Given a workflow having two stages and a work item in the first one
      String workflowId = "35a45cd4-f81f-11e5-9ce9-5e5517507c66";
      String aBoardName = "a workflow";
      Workflow workflow = new Workflow(workflowId, aBoardName);

      String stageAId = "c7c66e8a-610d-40f5-a8b6-455fad0928f6";
      String stageAName = "to do";
      Stage stageA = workflow.addStageToWorkflow(stageAId, stageAName);

      String stageBId = "7d1eee25-5d9e-4a95-98b0-032a17960a7a";
      String stageBName = "done";
      Stage stageB = workflow.addStageToWorkflow(stageBId, stageBName);

      String aWorkItemTitle = "Drink coffee";
      String aWorkItemDescription = "At Starbuck";
      String aWorkItemId = "09021d01-3da9-4584-85c0-85211cfa8467";
      WorkItem workItem = workflow
        .addWorkItemToStage(stageAId, aWorkItemId, aWorkItemTitle, aWorkItemDescription);

      WorkflowRepository workflowRepository = mock(WorkflowRepository.class);
      when(workflowRepository.findOne(workflowId)).thenReturn(workflow);

      // When I move the work item with id ""d1a947d2-93b6-4d9a-be8e-b35c47f085ff"" to stage B
      String unknownWorkItem = "d1a947d2-93b6-4d9a-be8e-b35c47f085ff";

      WorkflowIdentityService workflowIdentityService = mock(WorkflowIdentityService.class);
      WorkflowApplicationService workflowApplicationService = new WorkflowApplicationService(
        workflowRepository,
        workflowIdentityService);
      workflowApplicationService.moveWorkItemToStage(workflowId, unknownWorkItem, stageBId);

      // Then it should fail
    }

  }

  public static class MoveWorkItemToPosition {

    @Test
    public void itShouldSuccessfullyChangeThePositionOfWorkItems()
      throws StageNotInProcessException, WorkItemNotInStageException, WorkflowNotFoundException,
             WorkItemNotInProcessException {
      // Given a workflow with a stage having two work items A and B
      String workflowId = "35a45cd4-f81f-11e5-9ce9-5e5517507c66";
      String aBoardName = "a workflow";
      Workflow workflow = new Workflow(workflowId, aBoardName);

      String aStageId = "c7c66e8a-610d-40f5-a8b6-455fad0928f6";
      String aStageName = "to do";
      Stage stage = workflow.addStageToWorkflow(aStageId, aStageName);

      String workItemATitle = "A";
      String workItemADescription = "A desc";
      String workItemAId = "09021d01-3da9-4584-85c0-85211cfa8467";
      WorkItem workItemA = workflow
        .addWorkItemToStage(aStageId, workItemAId, workItemATitle, workItemADescription);

      String workItemBTitle = "B";
      String workItemBDescription = "B desc";
      String workItemBId = "81963606-76a1-41b5-82aa-5aba7b4dc115";
      WorkItem workItemB = workflow
        .addWorkItemToStage(aStageId, workItemBId, workItemBTitle, workItemBDescription);

      WorkflowRepository workflowRepository = mock(WorkflowRepository.class);
      when(workflowRepository.findOne(workflowId)).thenReturn(workflow);

      // When I switch the order of work items
      WorkflowIdentityService workflowIdentityService = mock(WorkflowIdentityService.class);
      WorkflowApplicationService workflowApplicationService = new WorkflowApplicationService(
        workflowRepository,
        workflowIdentityService);

      workflowApplicationService.moveWorkItemToPosition(workflow.getId(), workItemA.getId(), 2);

      // Then B is the first one and A is the last one
      assertThat(workflow.workItemsInStage(aStageId)).containsExactly(workItemB, workItemA);
    }

    @Test(expected = WorkItemNotInProcessException.class)
    public void itShouldFailIfTheWorkItemIsNotOnTheBoard()
      throws StageNotInProcessException, WorkItemNotInStageException, WorkflowNotFoundException,
             WorkItemNotInProcessException {
      // Given a workflow with a stage having two work items A and B
      String workflowId = "35a45cd4-f81f-11e5-9ce9-5e5517507c66";
      String aBoardName = "a workflow";
      Workflow workflow = new Workflow(workflowId, aBoardName);

      String aStageId = "c7c66e8a-610d-40f5-a8b6-455fad0928f6";
      String aStageName = "to do";
      Stage stage = workflow.addStageToWorkflow(aStageId, aStageName);

      String workItemATitle = "A";
      String workItemADescription = "A desc";
      String workItemAId = "09021d01-3da9-4584-85c0-85211cfa8467";
      WorkItem workItemA = workflow
        .addWorkItemToStage(aStageId, workItemAId, workItemATitle, workItemADescription);

      String workItemBTitle = "B";
      String workItemBDescription = "B desc";
      String workItemBId = "81963606-76a1-41b5-82aa-5aba7b4dc115";
      WorkItem workItemB = workflow
        .addWorkItemToStage(aStageId, workItemBId, workItemBTitle, workItemBDescription);

      WorkflowRepository workflowRepository = mock(WorkflowRepository.class);
      when(workflowRepository.findOne(workflowId)).thenReturn(workflow);

      // When I change the order of an unknown work item
      String unknownWorkItemId = "1d0c28c7-64c3-41ef-bcd6-e0fce8cfcfa3";

      WorkflowIdentityService workflowIdentityService = mock(WorkflowIdentityService.class);
      WorkflowApplicationService workflowApplicationService = new WorkflowApplicationService(
        workflowRepository,
        workflowIdentityService);

      workflowApplicationService.moveWorkItemToPosition(workflow.getId(), unknownWorkItemId, 2);

      // Then I got an exception
    }

  }

  public static class ArchiveWorkItem {

    @Test
    public void itShouldNotReturnArchivedWorkItems()
      throws StageNotInProcessException, WorkItemNotInProcessException, WorkflowNotFoundException {
      // Given a workflow with a stage having two work items A and B
      String workflowId = "35a45cd4-f81f-11e5-9ce9-5e5517507c66";
      String aBoardName = "a workflow";
      Workflow workflow = new Workflow(workflowId, aBoardName);

      String aStageId = "c7c66e8a-610d-40f5-a8b6-455fad0928f6";
      String aStageName = "to do";
      Stage stage = workflow.addStageToWorkflow(aStageId, aStageName);

      String workItemATitle = "A";
      String workItemADescription = "A desc";
      String workItemAId = "09021d01-3da9-4584-85c0-85211cfa8467";
      WorkItem workItemA = workflow
        .addWorkItemToStage(aStageId, workItemAId, workItemATitle, workItemADescription);

      String workItemBTitle = "B";
      String workItemBDescription = "B desc";
      String workItemBId = "81963606-76a1-41b5-82aa-5aba7b4dc115";
      WorkItem workItemB = workflow
        .addWorkItemToStage(aStageId, workItemBId, workItemBTitle, workItemBDescription);

      WorkflowRepository workflowRepository = mock(WorkflowRepository.class);
      when(workflowRepository.findOne(workflowId)).thenReturn(workflow);

      // When I archiveWorkItem workItemA
      WorkflowIdentityService workflowIdentityService = mock(WorkflowIdentityService.class);
      WorkflowApplicationService workflowApplicationService = new WorkflowApplicationService(
        workflowRepository,
        workflowIdentityService);
      workflowApplicationService.archiveWorkItem(workflowId, workItemAId);

      // Then work item A should not be in the list of work items any more
      assertThat(workflow.workItems()).doesNotContain(workItemA);
    }

  }

  public static class SendWorkItemBackToWorkflow {

    @Test
    public void itShouldMakeWorkItemSentBackToBoardVisible()
      throws StageNotInProcessException, WorkflowNotFoundException, WorkItemNotInProcessException,
             WorkItemNotArchivedException {
      // Given a workflow with a stage having a work item A archived
      String workflowId = "35a45cd4-f81f-11e5-9ce9-5e5517507c66";
      String aBoardName = "a workflow";
      Workflow workflow = new Workflow(workflowId, aBoardName);

      String aStageId = "c7c66e8a-610d-40f5-a8b6-455fad0928f6";
      String aStageName = "to do";
      Stage stage = workflow.addStageToWorkflow(aStageId, aStageName);

      String workItemATitle = "A";
      String workItemADescription = "A desc";
      String workItemAId = "09021d01-3da9-4584-85c0-85211cfa8467";
      WorkItem workItemA = workflow
        .addWorkItemToStage(aStageId, workItemAId, workItemATitle, workItemADescription);
      workflow.archiveWorkItemWithId(workItemAId);

      WorkflowRepository workflowRepository = mock(WorkflowRepository.class);
      when(workflowRepository.findOne(workflowId)).thenReturn(workflow);

      // When I archiveWorkItem workItemA
      WorkflowIdentityService workflowIdentityService = mock(WorkflowIdentityService.class);
      WorkflowApplicationService workflowApplicationService = new WorkflowApplicationService(
        workflowRepository,
        workflowIdentityService);
      workflowApplicationService.sendWorkItemBackToWorkflow(workflowId, workItemAId);

      // Then work item A should not be in the list of work items any more
      assertThat(workflow.workItems()).contains(workItemA);
    }

  }
}
